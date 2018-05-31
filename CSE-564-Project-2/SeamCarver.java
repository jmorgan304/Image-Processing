import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class SeamCarver {

	static float[] dual_gradient_energy(ImageData img){
		// Sobel operator:
		// Gx = {1, 0 -1, 2, 0, 2, 1, 0, -1} Gy = {1, 2, 1, 0, 0, 0, -1, -2, -1}
		int[] gx = {1, 0 -1, 2, 0, -2, 1, 0, -1};
		int[] gy = {1, 2, 1, 0, 0, 0, -1, -2, -1};
		float[] redBand = img.getRedBand();
		float[] greenBand = img.getGreenBand();
		float[] blueBand = img.getBlueBand();
		int width = img.getWidth();
		int height = img.getHeight();
		
		// Horizontal Gradients
		// Crops the image by one on all sides to handle edge cases
		float[] redHorizontal = directionalGradient(gx, redBand, width, height);
		float[] greenHorizontal = directionalGradient(gx, greenBand, width, height);
		float[] blueHorizontal = directionalGradient(gx, blueBand, width, height);
		
		// Vertical Gradients
		// Crops the image by one on all sides to handle edge cases
		float[] redVertical = directionalGradient(gy, redBand, width, height);
		float[] greenVertical = directionalGradient(gy, greenBand, width, height);
		float[] blueVertical = directionalGradient(gy, blueBand, width, height);
		
		// Calculating the energy of each pixel
		float[] energy = new float[redHorizontal.length];
		for(int i = 0; i < redHorizontal.length; i++){
			float horizontalSum = redHorizontal[i] + greenHorizontal[i] + blueHorizontal[i];
			float verticalSum = redVertical[i] + greenVertical[i] + blueVertical[i];
			energy[i] = (float) Math.sqrt(horizontalSum * horizontalSum + verticalSum * verticalSum);
		}
		
		return energy;
	}
	
	private static float[] directionalGradient(int[] directionalKernel, float[] colorBand, int imageWidth, int imageHeight){
		// Assumes an NxN kernel
		int sideLength = (int) Math.sqrt(directionalKernel.length + 1);
		// Side Length (of the kernel) Over Two
		int SLOT = sideLength / 2;
		float[] output = new float[(imageHeight - 2 * SLOT) * (imageWidth - 2 * SLOT)];
		int outputIndex = 0;
		// Ordering the kernel for convolution
		directionalKernel = flipKernel(directionalKernel);

		// Crops the image by SLOT on all sides to handle edge cases
		for(int row = SLOT; row < imageHeight - SLOT; row++){
			for(int column = SLOT; column < imageWidth - SLOT; column++){
				int centerIndex = row * imageWidth + column;
				float[] pixels = new float[directionalKernel.length + 1];
				// Get surrounding pixels
				int pixelIndex = 0;
				for(int i = -SLOT; i <= SLOT; i++){
					for(int j = -SLOT; j <= SLOT; j++){
						pixels[pixelIndex] = colorBand[centerIndex + (i * imageWidth) + j];
						pixelIndex++;
					}
				}
				// Convolution
				float pixelValue = 0.0f;
				for(int i = 0; i < directionalKernel.length; i++){
					pixelValue += directionalKernel[i] * pixels[i];
				}
				output[outputIndex] = pixelValue;
				outputIndex++;
			}
		}
		return output;
	}
	
	private static int[] flipKernel(int[] kernel){
		// Flips the rows and columns for convolution
		for(int i = 0; i < kernel.length / 2; i++){
			int temp = kernel[i];
			kernel[i] = kernel[kernel.length - i - 1];
			kernel[kernel.length - i - 1] = temp;
		}
		return kernel;
	}
	
	static int[] find_seam(ImageData img){
		int height = img.getHeight() - 2;
		int width = img.getWidth() - 2;
		float[] energy = dual_gradient_energy(img);
		System.out.println(energy.length);
		// Due to cropping for the convolutions
		int[] minSeam = new int[height];
		float minDisruption = Float.MAX_VALUE;
		for(int column = 0; column < width; column++){
			// Go though the reduced image column by column
			int[] seam = find_seam_helper(energy, width, 0, column, minSeam, 0);
			// Get the seam for each column
			float currentDisruption = 0.0f;
			for(int row = 0; row < seam.length; row++){
				// Calculate the disruption of each seam
				int seamIndex = seam[row];
				currentDisruption += energy[seamIndex];
			}
			if(currentDisruption < minDisruption){
				minSeam = seam;
				minDisruption = currentDisruption;
			}
		}
		return minSeam;
	}
	
	static int[] find_seam_helper(float[] energy, int imageWidth, int currentRow, int currentColumn, int[] seam, int seamIndex){
		if(seamIndex == seam.length - 1){
			return seam;
		}
		
		float diagonalLeft = energy[(currentRow + 1) * imageWidth + currentColumn - 1];
		float down = energy[(currentRow + 1) * imageWidth + currentColumn];
		float diagonalRight = energy[(currentRow + 1) * imageWidth + currentColumn + 1];
		float min = Math.min(diagonalLeft, Math.min(down, diagonalRight));
		
		if(min == diagonalLeft){
			seam[seamIndex] = (currentRow + 1) * imageWidth + currentColumn - 1;
			return find_seam_helper(energy, imageWidth, currentRow + 1, currentColumn - 1, seam, seamIndex + 1);
		}
		else if(min == down){
			seam[seamIndex] = (currentRow + 1) * imageWidth + currentColumn;
			return find_seam_helper(energy, imageWidth, currentRow + 1, currentColumn, seam, seamIndex + 1);
		}
		else if(min == diagonalRight){
			seam[seamIndex] = (currentRow + 1) * imageWidth + currentColumn + 1;
			return find_seam_helper(energy, imageWidth, currentRow + 1, currentColumn + 1, seam, seamIndex + 1);
		}
		return null;
	}
	
	static ImageData plot_seam(ImageData img, int[] seam){
		// To get it to the size of the energy data
		img.trim();
		for(int row = 0; row < seam.length; row++){
			int column = seam[row] % img.getWidth();
			img.setPixel(column, row, Pixel.getIntColor(0.0f,0.0f,0.0f));
		}
		return img;
	}
	
	static ImageData remove_seam(ImageData img,  int[] seam, boolean trimRight){
		// The boolean flag is to discern whether to trim from the left or right side
		// Ideally alternate side trimming to decrease visual issues
		if(trimRight){
			// Shift to the left and remove the right most column
			for(int row = 0; row < seam.length; row++){
				int seamColumn = seam[row] % img.getWidth();
				for(int i = 0; i < img.getWidth() - seamColumn - 1; i++){
					// Shift the pixels to the right of the seam to the left by one
					Pixel left = img.getPixel(seamColumn + i + 1, row);
					img.setPixel(seamColumn + i, row, left);
				}
			}
			img.trimRight();
		}
		else{
			// Shift to the right and remove the left most column
			for(int row = 0; row < seam.length; row++){
				int seamColumn = seam[row] % img.getWidth();
				for(int i = seamColumn; i > 1; i--){
					// Shift the pixels to the left of the seam to the right by one
					Pixel right = img.getPixel(i - 1, row);
					img.setPixel(i, row, right);
				}
			}
			img.trimLeft();
		}
		return img;
	}
	
	static BufferedImage getEnergyMap(ImageData img){
		Raster raster1 = img.getRaster();
		ColorModel originalCM = img.getColorModel();
		WritableRaster wr = raster1.createCompatibleWritableRaster();
		float[] energy = SeamCarver.dual_gradient_energy(img);
		float[] gradientImage = new float[energy.length * 4];
		float maxEnergy = Float.MIN_VALUE;
		// Finding the max value for mapping purposes
		for(int i = 0; i < energy.length; i++){
			if(energy[i] > maxEnergy){
				maxEnergy = energy[i];
			}
		}
		for(int i = 0; i < energy.length * 4; i++){
			if((i - 3) % 4 == 0){
				// Alpha
				gradientImage[i] = 255.0f;
			}
			else{
				// Mapping the values
				gradientImage[i] = 255.0f / maxEnergy * energy[i / 4];
			}
			
		}
		wr.setPixels(1, 1, img.getWidth() - 2, img.getHeight() - 2, gradientImage);
		return new BufferedImage(originalCM, wr, false, null);
	}

	
	public static void main(String[] args) {
		// Original, Energy, Seam, No Seam
		ImageData img1 = new ImageData("src/TestImage2.PNG");
		int[] seam = find_seam(img1);
		BufferedImage energyMap = getEnergyMap(img1);
		// These can be uncommented for each view, the windows tend to overlap when created all at once
		new ImageDisplay(energyMap);
		//new ImageDisplay(plot_seam(img1, seam).img);
		//new ImageDisplay(remove_seam(img1, seam, true).img);
		
	}
}
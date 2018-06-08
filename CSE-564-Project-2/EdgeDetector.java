import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class EdgeDetector {
	
	static BufferedImage getEnergyMap(ImageData img){
		Raster raster1 = img.getRaster();
		ColorModel originalCM = img.getColorModel();
		WritableRaster wr = raster1.createCompatibleWritableRaster();
		float[] energy = dual_gradient_energy(img);
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
}

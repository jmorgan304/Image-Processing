import java.awt.image.BufferedImage;
import java.io.IOException;

public class SeamCarver {

	static int[] find_seam(float[] energy, ImageData img){
		int height = img.getHeight() - 2;
		int width = img.getWidth() - 2;
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
	
	public static void main(String[] args) {
		// Original, Energy, Seam, No Seam
		try {
			ImageData img1 = new ImageData("TestImage2.PNG");

			// Sobel operator:
			// Gx = {1, 0 -1, 2, 0, 2, 1, 0, -1} Gy = {1, 2, 1, 0, 0, 0, -1, -2, -1}
			Integer[] gx1 = { 1, 0 - 1, 2, 0, -2, 1, 0, -1 };
			Integer[] gy1 = { 1, 2, 1, 0, 0, 0, -1, -2, -1 };
			Integer[] gx2 = { -1, -1, -1, -1, 8, -1, -1, -1, -1 };
			Integer[] gy2 = { -1, -1, -1, -1, 8, -1, -1, -1, -1 };
			Kernel horizontal1 = new Kernel<Integer>(gx1, 3);
			Kernel vertical1 = new Kernel<Integer>(gy1, 3);
			Kernel horizontal2 = new Kernel<Integer>(gx2, 3);
			Kernel vertical2 = new Kernel<Integer>(gy2, 3);
			
			
			
			EdgeDetector test1 = new EdgeDetector(img1, horizontal1, vertical1);
			BufferedImage energyMap1 = test1.getEnergyMap();
			EdgeDetector test2 = new EdgeDetector(img1, horizontal2, vertical2);
			BufferedImage energyMap2 = test2.getEnergyMap();
			
			//int[] seam = find_seam(test.getDualGradientEnergy(), img1);
			// These can be uncommented for each view, the windows tend to overlap when created all at once
			new ImageDisplay(energyMap1);
			new ImageDisplay(energyMap2);
			//new ImageDisplay(plot_seam(img1, seam).img);
			//new ImageDisplay(remove_seam(img1, seam, true).img);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
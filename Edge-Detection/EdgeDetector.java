import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class EdgeDetector {
	private ImageData img;
	private Kernel horizontal;
	private Kernel vertical;
	private float[] dualGradientEnergy;
	
	EdgeDetector(ImageData img, Kernel horizontal, Kernel vertical) {
		this.img = img;
		this.horizontal = horizontal;
		this.vertical = vertical;
		this.dualGradientEnergy = dual_gradient_energy();
	}
	
	public BufferedImage getEnergyMap() {
		Raster raster1 = img.getRaster();
		ColorModel originalCM = img.getColorModel();
		WritableRaster wr = raster1.createCompatibleWritableRaster();
		float[] energy = this.dualGradientEnergy;
		float[] gradientImage = new float[energy.length * 4];
		float maxEnergy = Float.MIN_VALUE;
		// Finding the max value for mapping purposes
		for (int i = 0; i < energy.length; i++) {
			if (energy[i] > maxEnergy) {
				maxEnergy = energy[i];
			}
		}
		for (int i = 0; i < energy.length * 4; i++) {
			if ((i - 3) % 4 == 0) {
				// Alpha
				gradientImage[i] = 255.0f;
			} else {
				// Mapping the values
				gradientImage[i] = 255.0f / maxEnergy * energy[i / 4];
			}

		}
		wr.setPixels(1, 1, img.getWidth() - 2, img.getHeight() - 2, gradientImage);
		return new BufferedImage(originalCM, wr, false, null);
	}

	private float[] dual_gradient_energy() {
		float[] redBand = img.getRedBand();
		float[] greenBand = img.getGreenBand();
		float[] blueBand = img.getBlueBand();
		int width = img.getWidth();
		int height = img.getHeight();

		// Horizontal Gradients
		// Crops the image by one on all sides to handle edge cases
		float[] redHorizontal = directionalGradient(horizontal, redBand, width, height);
		float[] greenHorizontal = directionalGradient(horizontal, greenBand, width, height);
		float[] blueHorizontal = directionalGradient(horizontal, blueBand, width, height);

		// Vertical Gradients
		// Crops the image by one on all sides to handle edge cases
		float[] redVertical = directionalGradient(vertical, redBand, width, height);
		float[] greenVertical = directionalGradient(vertical, greenBand, width, height);
		float[] blueVertical = directionalGradient(vertical, blueBand, width, height);

		// Calculating the energy of each pixel
		float[] energy = new float[redHorizontal.length];
		for (int i = 0; i < redHorizontal.length; i++) {
			float horizontalSum = redHorizontal[i] + greenHorizontal[i] + blueHorizontal[i];
			float verticalSum = redVertical[i] + greenVertical[i] + blueVertical[i];
			energy[i] = (float) Math.sqrt(horizontalSum * horizontalSum + verticalSum * verticalSum);
		}
		return energy;
	}

	private static float[] directionalGradient(Kernel directionalKernel, float[] colorBand, int imageWidth,
			int imageHeight) {
		// Assumes an NxN kernel
		int sideLength = (int) Math.sqrt(directionalKernel.getLength() + 1);
		// Side Length (of the kernel) Over Two
		int SLOT = sideLength / 2;
		float[] output = new float[(imageHeight - 2 * SLOT) * (imageWidth - 2 * SLOT)];
		int outputIndex = 0;
		// Ordering the kernel for convolution

		// Crops the image by SLOT on all sides to handle edge cases
		for (int row = SLOT; row < imageHeight - SLOT; row++) {
			for (int column = SLOT; column < imageWidth - SLOT; column++) {
				int centerIndex = row * imageWidth + column;
				float[] pixels = new float[directionalKernel.getLength() + 1];
				// Get surrounding pixels
				int pixelIndex = 0;
				for (int i = -SLOT; i <= SLOT; i++) {
					for (int j = -SLOT; j <= SLOT; j++) {
						pixels[pixelIndex] = colorBand[centerIndex + (i * imageWidth) + j];
						pixelIndex++;
					}
				}
				// Convolution
				float pixelValue = directionalKernel.convolute(pixels);
				output[outputIndex] = pixelValue;
				outputIndex++;
			}
		}
		return output;
	}

	public float[] getDualGradientEnergy() {
		return dualGradientEnergy;
	}

}

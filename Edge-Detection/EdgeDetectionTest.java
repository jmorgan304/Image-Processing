import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.Test;

public class EdgeDetectionTest {

	@Test
	public void testKernel() {
		Integer[] kernelValues = { -1, -1, -1, -1, 8, -1, -1, -1, -1 };
		Kernel test = new Kernel<Integer>(kernelValues, 3);
		float[] testValues = { 1.0f, 10.0f, 9.0f, 20.0f, 255.0f, 100.0f, 32.0f, 16.0f, 8.0f };
		float result = test.convolute(testValues);
		assertEquals(result, 1844.0f, Float.MIN_VALUE);
	}

	public BufferedImage testImageData(String path) {
		try {
			ImageData test = new ImageData(path);
			return test.img;
		}
		catch (IOException e) {
			return null;
		}
	}

	@Test
	public void testImageDisplay() {
		String path = "TestImage1.PNG";
		BufferedImage img = testImageData(path);
		if (img == null) {
			fail();
		}
		ImageDisplay test = new ImageDisplay(img);
	}

	@Test
	public void testEdgeDetector() {
		try {
			ImageData imgData = new ImageData("TestImage1.PNG");
			// Sobel operator:
			// Gx = {1, 0 -1, 2, 0, 2, 1, 0, -1} Gy = {1, 2, 1, 0, 0, 0, -1, -2, -1}
			// Integer[] gx = { 1, 0 - 1, 2, 0, -2, 1, 0, -1 };
			// Integer[] gy = { 1, 2, 1, 0, 0, 0, -1, -2, -1 };
			Integer[] gx = { -1, -1, -1, -1, 8, -1, -1, -1, -1 };
			Integer[] gy = { -1, -1, -1, -1, 8, -1, -1, -1, -1 };
			Kernel horizontal = new Kernel<Integer>(gx, 3);
			Kernel vertical = new Kernel<Integer>(gy, 3);

			EdgeDetector test = new EdgeDetector(imgData, horizontal, vertical);
			BufferedImage result = test.getEnergyMap();
			ImageDisplay viewResult = new ImageDisplay(result);
		}
		catch (IOException e) {
			fail();
		}
	}

}

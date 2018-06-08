
public class Kernel<T extends Number> {
	private T[] kernelValues;
	private int dimension;

	Kernel(T[] kernelValues, int dimension) {
		this.kernelValues = kernelValues;
		this.dimension = dimension;
	}

	public float convolute(float[] imageSection) {
		float output = 0.0f;
		flipKernel();

		for (int i = 0; i < this.kernelValues.length; i++) {
			output += kernelValues[i].floatValue() * imageSection[i];
		}
		return output;
	}

	private void flipKernel() {
		// Flips the rows and columns for convolution
		for (int i = 0; i < this.kernelValues.length / 2; i++) {
			T temp = this.kernelValues[i];
			this.kernelValues[i] = this.kernelValues[this.kernelValues.length - i - 1];
			this.kernelValues[this.kernelValues.length - i - 1] = temp;
		}
	}

	public String toString() {
		String kernel = "";
		for (int i = 0; i < this.dimension; i++) {
			for (int j = 0; j < this.dimension; j++) {
				kernel += this.kernelValues[i * this.dimension + j] + " ";
			}
			kernel += "\n";
		}
		return kernel;
	}
	
	public int getLength() {
		return this.kernelValues.length;
	}
}

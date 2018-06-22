
public enum ActivationFunction {
	NONE, SIGMOID, TANH, RELU;

	public double apply(double input) {
		switch (this) {
		case NONE:
			return input;
		case SIGMOID:
			return sigmoid(input);
		case TANH:
			return tanh(input);
		case RELU:
			return relu(input);
		default:
			return Double.NEGATIVE_INFINITY;
		}
	}

	public double derivative(double input) {
		switch (this) {
		case NONE:
			return input;
		case SIGMOID:
			return sigmoidDerivative(input);
		case TANH:
			return tanhDerivative(input);
		case RELU:
			return reluDerivative(input);
		default:
			return Double.NEGATIVE_INFINITY;
		}
	}

	public static double sigmoid(double input) {
		double denominator = 1 + Math.pow(Math.E, -input);
		double output = 1 / denominator;
		return output;
	}

	public static double sigmoidDerivative(double input) {
		double numerator = Math.pow(Math.E, -input);
		double denominator = Math.pow((numerator + 1), 2);
		double output = numerator / denominator;
		return output;
	}

	private static double tanh(double input) {
		return Math.tanh(input);
	}

	private static double tanhDerivative(double input) {
		double denominator = Math.pow((Math.pow(Math.E, input) + Math.pow(Math.E, -input)), 2);
		return 4 / denominator;
	}

	private static double relu(double input) {
		return Math.max(0, input);
	}

	private static double reluDerivative(double input) {
		// Constant as derivative of x = 1, 0 otherwise
		return input > 0 ? 1 : 0;
	}
}

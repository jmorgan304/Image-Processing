
public enum ActivationFunction {
	NONE, SIGMOID, TANH, RELU, SOFTMAX;

	public double apply(double zValue, double[] layerZValues) {
		switch (this) {
		case NONE:
			return zValue;
		case SIGMOID:
			return sigmoid(zValue);
		case TANH:
			return tanh(zValue);
		case RELU:
			return relu(zValue);
		case SOFTMAX:
			return softmax(zValue, layerZValues);
		default:
			return Double.NEGATIVE_INFINITY;
		}
	}

	public double derivative(double activationValue, double[] activationValues) {
		// NOTE since the derivatives can be better computed when defined in terms of
		// f(zValue) = activationValue
		// Passing the activation value instead of the zValue is more efficient
		switch (this) {
		case NONE:
			return activationValue;
		case SIGMOID:
			return sigmoidDerivative(activationValue);
		case TANH:
			return tanhDerivative(activationValue);
		case RELU:
			return reluDerivative(activationValue);
		case SOFTMAX:
			return softmaxDerivative(activationValue, activationValues);
		default:
			return Double.NEGATIVE_INFINITY;
		}
	}

	private static double sigmoid(double zValue) {
		double denominator = 1 + Math.pow(Math.E, -zValue);
		double output = 1 / denominator;
		return output;
	}

	private static double sigmoidDerivative(double activationValue) {
		// If sigmoid(z) = f(z) = a then: sigmoid'(z) = f'(z) = f(z)*(1- f(z)) = a*(1-a)
		double output = sigmoid(activationValue) * (1 - activationValue);
		return output;
	}

	private static double tanh(double zValue) {
		return Math.tanh(zValue);
	}

	private static double tanhDerivative(double activationValue) {
		// If tanh(z) = f(z) = a then: tanh'(z) = f'(z) = 1 - (f(z))^2 = 1 - a^2
		double denominator = 1 - Math.pow(activationValue, 2);
		return 4 / denominator;
	}

	private static double relu(double zValue) {
		return Math.max(0, zValue);
	}

	private static double reluDerivative(double activationValue) {
		// Constant as derivative of x = 1, 0 otherwise
		return activationValue > 0 ? 1 : 0;
	}

	private static double softmax(double zValue, double[] zValues) {
		double numerator = Math.pow(Math.E, zValue);
		double denominator = 0.0;
		for (double z : zValues) {
			denominator += Math.pow(Math.E, z);
		}
		return numerator / denominator;
	}

	private static double softmaxDerivative(double activationValue, double[] activationValues) {
		// Given the activation value = softmax(z), softmax(z)' = softmax(z) * log(e)
		return Double.NEGATIVE_INFINITY;
	}
}

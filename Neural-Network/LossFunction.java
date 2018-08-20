
public enum LossFunction {
	SSE, LOGLOSS;

	public double apply(double predicted, double actual) {
		switch (this) {
		case SSE:
			return sse(predicted, actual);
		case LOGLOSS:
			return logLoss(predicted, actual);
		default:
			return Double.NEGATIVE_INFINITY;
		}
	}

	public double derivative(double predicted, double actual) {
		switch (this) {
		case SSE:
			return sseDerivative(predicted, actual);
		case LOGLOSS:
			return logLossDerivative(predicted, actual);
		default:
			return Double.NEGATIVE_INFINITY;
		}
	}

	private static double sse(double predicted, double actual) {
		return Math.pow((predicted - actual), 2);
	}

	private static double sseDerivative(double predicted, double actual) {
		return 2 * (predicted - actual);
	}

	private static double logLoss(double predicted, double actual) {
		return (-actual * Math.log(predicted)) - (1 - actual) * (Math.log((1 - predicted)));
	}

	public static double logLossDerivative(double predicted, double actual) {
		return Double.NEGATIVE_INFINITY;
	}
}

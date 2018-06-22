public class Node {
	private Edge[] inputEdges;
	private Edge[] outputEdges;
	private double activationValue;
	private double bias;
	private ActivationFunction activationFunction;

	public Node(double activationValue, double bias, ActivationFunction activationFunction) {
		this.activationValue = activationValue;
		this.bias = bias;
		this.activationFunction = activationFunction;
	}

	public double calcZvalue() {
		if (this.inputEdges == null) {
			// If this is an input node, return the activation/input value
			return this.activationValue;
		}
		// (sum of (weights * previous activation values)) + bias
		double weightedSum = 0.0f;

		for (Edge inputEdge : this.inputEdges) {
			weightedSum += inputEdge.getWeight() * inputEdge.getFromNode().getActivationValue();
		}
		weightedSum += this.bias;

		return weightedSum;
	}

	public Edge[] getInputEdges() {
		return inputEdges;
	}

	public void setInputEdges(Edge[] inputEdges) {
		this.inputEdges = inputEdges;
	}

	public Edge[] getOutputEdges() {
		return outputEdges;
	}

	public void setOutputEdges(Edge[] outputEdges) {
		this.outputEdges = outputEdges;
	}

	public double getActivationValue() {
		return activationValue;
	}

	public void setActivationValue(double activationValue) {
		this.activationValue = activationValue;
	}

	public double getBias() {
		return bias;
	}

	public void setBias(double bias) {
		this.bias = bias;
	}

	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}

	public void setActivationFunction(ActivationFunction activationFunction) {
		this.activationFunction = activationFunction;
	}

}

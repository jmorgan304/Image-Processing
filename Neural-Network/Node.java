
public class Node {
	private double activationValue;
	private double bias;
	private Edge[] inputEdges;
	private Edge[] outputEdges;
	private boolean isInputNode;
	private boolean isOutputNode;

	Node(double activationValue, double bias, boolean isInputNode) {
		this.activationValue = activationValue;
		this.bias = bias;
		this.isOutputNode = false;
		this.isInputNode = isInputNode;
	}

	Node(double activationValue, double bias, Edge[] inputEdges, Edge[] outputEdges) {
		this.activationValue = activationValue;
		this.bias = bias;
		this.inputEdges = inputEdges;
		this.outputEdges = outputEdges;
		if (inputEdges == null) {
			this.isInputNode = true;
		} else if (outputEdges == null) {
			this.isOutputNode = true;
		} else {
			this.isOutputNode = false;
			this.isInputNode = false;
		}
	}

	public String toString() {
		return "Node, AV: " + this.activationValue + ", Bias: " + this.bias;
	}

	public double getActivationValue() {
		return activationValue;
	}

	public void setActivationValue(double activationValue) {
		if (activationValue >= 0.0 && activationValue <= 1.0) {
			this.activationValue = activationValue;
		}
	}

	public double getBias() {
		return bias;
	}

	public void setBias(double bias) {
		if(this.isInputNode) {
			this.bias = 0;
		}
		else {
			this.bias = bias;
		}
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

	public boolean isInputNode() {
		return isInputNode;
	}

	public void setInputNode(boolean isInputNode) {
		this.isInputNode = isInputNode;
	}

	public boolean isOutputNode() {
		return isOutputNode;
	}

	public void setOutputNode(boolean isOutputNode) {
		this.isOutputNode = isOutputNode;
	}
	
	public double[] getInputWeights() {
		double[] inputWeights = new double[this.inputEdges.length];
		for(int i = 0; i < inputEdges.length; i++) {
			inputWeights[i] = this.inputEdges[i].getWeight();
		}
		return inputWeights;
	}

}

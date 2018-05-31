
public class Node {
	private float activationValue;
	private float bias;
	Edge[] inputEdges;
	Edge[] outputEdges;
	private boolean isInputNode;
	private boolean isOutputNode;

	Node(float activationValue, float bias) {
		this.activationValue = activationValue;
		this.bias = bias;
		this.isOutputNode = false;
		this.isInputNode = false;
	}

	Node(float activationValue, float bias, Edge[] inputEdges, Edge[] outputEdges) {
		this.activationValue = activationValue;
		this.bias = bias;
		this.inputEdges = inputEdges;
		this.outputEdges = outputEdges;
		if (inputEdges == null) {
			this.isInputNode = true;
		}
		else if (outputEdges == null) {
			this.isOutputNode = true;
		}
		else {
			this.isOutputNode = false;
			this.isInputNode = false;
		}
	}

	public float getActivationValue() {
		return activationValue;
	}

	public void setActivationValue(float activationValue) {
		this.activationValue = activationValue;
	}

	public float getBias() {
		return bias;
	}

	public void setBias(float bias) {
		this.bias = bias;
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
	
	
}

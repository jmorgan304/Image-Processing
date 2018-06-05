
public class Layer {
	private Node[] nodes;
	private Edge[][] inputEdges;
	private Edge[][] outputEdges;
	private double[] zValues;
	private double[][] outputWeights;
	private double[] activationValues;
	private double[] biases;
	private int size;
	private boolean isInputLayer;
	private boolean isOutputLayer;

	Layer(Node[] nodes, Edge[][] inputEdges, Edge[][] outputEdges) {
		this.nodes = nodes;
		this.inputEdges = inputEdges;
		this.outputEdges = outputEdges;
		if (nodes != null) {
			this.size = nodes.length;
		}
	}

	public String toString() {
		String inputEdges = "";
		if (this.isInputLayer) {
			inputEdges += "Input Values:\n";
			for (Edge[] nodeOutputEdges : this.outputEdges) {
				inputEdges += "\t" + nodeOutputEdges[0].getFromNode().getActivationValue() + "\n";
			}
		}
		String outputEdges = "Output Edges:\n";
		if (!this.isOutputLayer) {
			for (Edge[] nodeEdges : this.outputEdges) {
				for (Edge outputEdge : nodeEdges) {
					outputEdges += "\t" + outputEdge.toString() + "\n";
				}
			}
		} else {
			outputEdges = "Output Values:\n";
			for (Node outputNode : this.nodes) {
				outputEdges += "\t" + outputNode.getActivationValue() + "\n";
			}
		}

		return inputEdges + outputEdges;
	}

	public Node[] getNodes() {
		return nodes;
	}

	public void setNodes(Node[] nodes) {
		this.nodes = nodes;
		if (nodes != null) {
			this.size = nodes.length;
			double[] biases = new double[nodes.length];
			double[] activationValues = new double[nodes.length];
			for (int i = 0; i < nodes.length; i++) {
				biases[i] = nodes[i].getBias();
				activationValues[i] = nodes[i].getActivationValue();
			}
			this.activationValues = activationValues;
			this.biases = biases;
		} else {
			this.size = 0;
		}
	}

	public Edge[][] getInputEdges() {
		return inputEdges;
	}

	public void setInputEdges(Edge[][] inputEdges) {
		this.inputEdges = inputEdges;
	}

	public Edge[][] getOutputEdges() {
		return outputEdges;
	}

	public void setOutputEdges(Edge[][] outputEdges) {
		this.outputEdges = outputEdges;
		double[][] outputWeights = new double[outputEdges.length][outputEdges[0].length];

		for (int i = 0; i < outputEdges.length; i++) {
			for (int j = 0; j < outputEdges[i].length; j++) {
				outputWeights[i][j] = outputEdges[i][j].getWeight();
				// Set the outputWeights to the weights of the output edges
			}
		}
		this.outputWeights = outputWeights;
	}

	public int getSize() {
		return this.size;
	}

	public boolean isInputLayer() {
		return this.isInputLayer;
	}

	public boolean isOutputLayer() {
		return this.isOutputLayer;
	}

	public void setIsInputLayer(boolean value) {
		this.isInputLayer = value;
		if (value) {
			this.inputEdges = null;
		}
	}

	public void setIsOutputLayer(boolean value) {
		this.isOutputLayer = value;
		if (value) {
			this.outputEdges = null;
		}
	}

	public double[] getzValues() {
		return zValues;
	}

	public void setzValues(double[] zValues) {
		this.zValues = zValues;
	}

	public double[][] getOutputWeights() {
		return outputWeights;
	}

	public void setWeights(double[][] inputWeights, double[][] outputWeights) {
		if (inputWeights != null && outputWeights != null) {
			// Hidden layer
			for(int i = 0; i < this.inputEdges.length; i++) {
				for(int j = 0; j < this.inputEdges[i].length; j++) {
					double weight = inputWeights[i][j];
					this.inputEdges[i][j].setWeight(weight);
				}
			}
			for(int i = 0; i < this.outputEdges.length; i++) {
				for(int j = 0; j < this.outputEdges[i].length; j++) {
					double weight = outputWeights[i][j];
					this.outputEdges[i][j].setWeight(weight);
				}
			}
			for(int i = 0; i < this.nodes.length; i++) {
				this.nodes[i].setInputEdges(this.inputEdges[i]);
			}
			for(int i = 0; i < this.nodes.length; i++) {
				this.nodes[i].setOutputEdges(this.outputEdges[i]);
			}
		} else if(inputWeights != null && this.isOutputLayer){
			// output layer
			for(int i = 0; i < this.inputEdges.length; i++) {
				for(int j = 0; j < this.inputEdges[i].length; j++) {
					double weight = inputWeights[j][i];
					this.inputEdges[i][j].setWeight(weight);
				}
			}
			for(int i = 0; i < this.nodes.length; i++) {
				this.nodes[i].setInputEdges(this.inputEdges[i]);
			}
		}  else if(outputWeights != null && this.isInputLayer){
			// input layer
			for(int i = 0; i < this.outputEdges.length; i++) {
				for(int j = 0; j < this.outputEdges[i].length; j++) {
					double weight = outputWeights[i][j];
					this.outputEdges[i][j].setWeight(weight);
				}
			}
			for(int i = 0; i < this.nodes.length; i++) {
				this.nodes[i].setOutputEdges(this.outputEdges[i]);
			}
		}
	}

	public double[] getBiases() {
		return biases;
	}

	public void setBiases(double[] biases) {
		if (biases.length == this.nodes.length) {
			this.biases = biases;
			for (int i = 0; i < this.nodes.length; i++) {
				this.nodes[i].setBias(biases[i]);
			}
		}
	}

	public double[] getActivationValues() {
		double[] updatedValues = new double[this.nodes.length];
		for(int i = 0; i < this.nodes.length; i++) {
			updatedValues[i] = this.nodes[i].getActivationValue();
		}
		this.activationValues = updatedValues;
		return activationValues;
	}

}

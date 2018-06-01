
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
	
	Layer(Node[] nodes, Edge[][] inputEdges, Edge[][] outputEdges){
		this.nodes = nodes;
		this.inputEdges = inputEdges;
		this.outputEdges = outputEdges;
		if(nodes != null) {
			this.size = nodes.length;
		}
	}
	
	public String toString() {
		String inputEdges = "";
		if(this.isInputLayer) {
			inputEdges += "Input Values:\n";
			for(Edge[] nodeOutputEdges : this.outputEdges) {
				inputEdges += "\t" + nodeOutputEdges[0].getFromNode().getActivationValue() + "\n";
			}
		}
		String outputEdges = "Output Edges:\n";
		if(! this.isOutputLayer) {
			for(Edge[] nodeEdges : this.outputEdges) {
				for(Edge outputEdge : nodeEdges) {
					outputEdges += "\t" + outputEdge.toString() + "\n";
				}
			}
		}
		else {
			outputEdges = "Output Values:\n";
			for(Node outputNode : this.nodes) {
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
		if(nodes != null) {
			this.size = nodes.length;
			double[] biases = new double[nodes.length];
			double[] activationValues = new double[nodes.length];
			for(int i = 0; i < nodes.length; i++) {
				biases[i] = nodes[i].getBias();
				activationValues[i] = nodes[i].getActivationValue();
			}
			this.activationValues = activationValues;
			this.biases = biases;
		}
		else {
			this.size = 0;
		}
	}

	public Edge[][] getInputEdges() {
		return inputEdges;
	}

	public void setInputEdges(Edge[][] inputEdges) {
		this.inputEdges = inputEdges;
//		if (outputEdges == null && this.inputEdges != null) {
//			this.isOutputLayer = true;
//		}
	}

	public Edge[][] getOutputEdges() {
		return outputEdges;
	}

	public void setOutputEdges(Edge[][] outputEdges) {
		this.outputEdges = outputEdges;
		double[][] outputWeights = new double[outputEdges.length][outputEdges[0].length];
		
		for(int i = 0; i < outputEdges.length; i++) {
			for(int j = 0; j < outputEdges[i].length; i++) {
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
		if(value) {
			this.inputEdges = null;
		}
	}
	
	public void setIsOutputLayer(boolean value) {
		this.isOutputLayer = value;
		if(value) {
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


	public double[] getBiases() {
		return biases;
	}

	public double[] getActivationValues() {
		return activationValues;
	}

	
	
}

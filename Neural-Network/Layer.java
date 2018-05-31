
public class Layer {
	private Node[] nodes;
	private Edge[] inputEdges;
	private Edge[] outputEdges;
	private int size;
	private boolean isInputLayer;
	private boolean isOutputLayer;
	
	Layer(Node[] nodes, Edge[] inputEdges, Edge[] outputEdges){
		this.nodes = nodes;
		this.inputEdges = inputEdges;
		this.outputEdges = outputEdges;
		if(nodes != null) {
			this.size = nodes.length;
		}
		if (inputEdges == null && outputEdges != null) {
			this.isInputLayer = true;
		}
		else if (outputEdges == null && inputEdges != null) {
			this.isOutputLayer = true;
		}
		else {
			this.isOutputLayer = false;
			this.isInputLayer = false;
		}
	}

	public Node[] getNodes() {
		return nodes;
	}

	public void setNodes(Node[] nodes) {
		this.nodes = nodes;
		if(nodes != null) {
			this.size = nodes.length;
		}
	}

	public Edge[] getInputEdges() {
		return inputEdges;
	}

	public void setInputEdges(Edge[] inputEdges) {
		this.inputEdges = inputEdges;
		if (outputEdges == null && this.inputEdges != null) {
			this.isOutputLayer = true;
		}
	}

	public Edge[] getOutputEdges() {
		return outputEdges;
	}

	public void setOutputEdges(Edge[] outputEdges) {
		this.outputEdges = outputEdges;
		if (inputEdges == null && this.outputEdges != null) {
			this.isInputLayer = true;
		}
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
}

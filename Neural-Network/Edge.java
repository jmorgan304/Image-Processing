
public class Edge {
	private Node fromNode;
	private Node toNode;
	private double weight;
	
	Edge(Node fromNode, Node toNode, double weight) {
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.weight = weight;
	}
	
	public String toString() {
		String fromString = fromNode.toString();
		String toString = toNode.toString();
		return "Edge, From: " + fromString + " To: " + toString + " Weight: " + this.weight;
	}

	public Node getFromNode() {
		return fromNode;
	}

	public void setFromNode(Node fromNode) {
		this.fromNode = fromNode;
	}

	public Node getToNode() {
		return toNode;
	}

	public void setToNode(Node toNode) {
		this.toNode = toNode;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	
}

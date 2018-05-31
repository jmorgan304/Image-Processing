
public class Edge {
	private Node fromNode;
	private Node toNode;
	private float weight;
	
	Edge(Node fromNode, Node toNode, float weight) {
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.weight = weight;
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

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	
}

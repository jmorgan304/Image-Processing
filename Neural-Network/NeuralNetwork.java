
public class NeuralNetwork {
	private Layer inputLayer;
	private Layer[] hiddenLayers;
	private Layer outputLayer;
	
	NeuralNetwork(int[] layerSizes){
		if(layerSizes.length >= 2) {
			initAndConnect(layerSizes);
		}
	}
	
	private void initAndConnect(int[] layerSizes) {
		Layer[] layers = new Layer[layerSizes.length];
		
		for(int i = 0; i < layerSizes.length; i++) {
			// For each layer create its set of nodes
			Node[] nodes = new Node[layerSizes[i]];
			
			for(int j = 0; j < layerSizes[i]; j++) {
				// For each node in the layer assign it a random activation values and bias
				double activationValue = Math.random();
				double bias = Math.random() * 10;
				nodes[j] = new Node(activationValue, bias);
			}
			layers[i] = new Layer(nodes, null, null);
			// Connect the nodes and assign the edges to the layers after
		}
		
		for(int i = 0; i < layers.length - 1; i++) {
			// For each layer except the last one, connect its output nodes to the next layer
			Layer layer1 = layers[i];
			Layer layer2 = layers[i + 1];
			
			for(Node fromNode : layer1.getNodes()) {
				// For each node in layer1 generate its output edges and connect them to layer2 as input edges
				Edge[] outputEdges = new Edge[layer2.getSize()];
				
				for(int j = 0; j < layer2.getSize(); j++) {
					// Create the edges from each individual fromNode to every toNode
					Node toNode = layer2.getNodes()[j];
					
					double weight = Math.random() * 5;
					Edge outputEdge = new Edge(fromNode, toNode, weight);
					outputEdges[j] = outputEdge;
				}
				layer1.setOutputEdges(outputEdges);
				layer2.setInputEdges(outputEdges);
				// The output edges for layer1 are layer2's input edges
			}
		}
		
		this.inputLayer = layers[0];
		this.hiddenLayers = new Layer[layers.length - 2];
		for(int i = 1; i < layers.length - 1; i++) {
			this.hiddenLayers[i - 1] = layers[i];
		}
		this.outputLayer = layers[layers.length - 1];
		
	}
	
	

	public Layer getInputLayer() {
		return inputLayer;
	}

	public Layer[] getHiddenLayers() {
		return hiddenLayers;
	}

	public Layer getOutputLayer() {
		return outputLayer;
	}
	
	
}

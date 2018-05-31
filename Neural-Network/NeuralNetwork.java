
public class NeuralNetwork {
	private Layer inputLayer;
	private Layer[] hiddenLayers;
	private Layer outputLayer;
	
	NeuralNetwork(int[] layerSizes, double minBias, double maxBias, double minWeight, double maxWeight){
		if(layerSizes.length >= 2) {
			initAndConnect(layerSizes, minBias, maxBias, minWeight, maxWeight);
			updateActivationValues();
		}
	}
	
	private void initAndConnect(int[] layerSizes, double minBias, double maxBias, double minWeight, double maxWeight) {
		Layer[] layers = new Layer[layerSizes.length];
		
		for(int i = 0; i < layerSizes.length; i++) {
			// For each layer create its set of nodes
			Node[] nodes = new Node[layerSizes[i]];
			
			for(int j = 0; j < layerSizes[i]; j++) {
				// For each node in the layer assign it a random activation values and bias
				double bias = Math.random() * maxBias + Math.random() * minBias;
				double activationValue = Math.random();
				
				if(j > 0) {
					// All activation values except the inputLayer are based on the previous ones
					activationValue = -1;
				}
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
					
					double weight = Math.random() * maxWeight + Math.random() * minWeight;
					Edge outputEdge = new Edge(fromNode, toNode, weight);
					outputEdges[j] = outputEdge;
				}
				layer1.setOutputEdges(outputEdges);
				layer2.setInputEdges(outputEdges);
				// The output edges for layer1 are layer2's input edges
			}
		}
		
		this.inputLayer = layers[0];
		layers[0].setIsInputLayer(true);
		this.hiddenLayers = new Layer[layers.length - 2];
		for(int i = 1; i < layers.length - 1; i++) {
			this.hiddenLayers[i - 1] = layers[i];
		}
		this.outputLayer = layers[layers.length - 1];
		layers[layers.length - 1].setIsOutputLayer(true);
	}
	
	private void updateActivationValues() {
		for(Layer layer : this.hiddenLayers) {
			setActivationValues(layer);
		}
		setActivationValues(this.outputLayer);
	}
	
	private void setActivationValues(Layer layer) {
		if(! layer.isInputLayer()) {
			// The input layer's activation values are the inputs themselves
			for(Node node : layer.getNodes()) {
				// For each node in the given layer, find its activation value
				double activationValue = getActivationValue(node);
				node.setActivationValue(activationValue);
			}
		}
	}
	
	private double getActivationValue(Node node) {
		double total = 0.0;
		Edge[] inputEdges = node.getInputEdges();
		if(inputEdges == null) {
			// The node is an input node, set to a random value
			return Math.random();
		}
		else {
			for(Edge inputEdge : inputEdges) {
				// Summation of weights and activation values
				double fromNodeActivation = inputEdge.getFromNode().getActivationValue();
				if(fromNodeActivation == -1) {
					// The value hasn't been set yet, recursively set the previous required layers until its valid
					fromNodeActivation = getActivationValue(inputEdge.getFromNode());
				}
				double weight = inputEdge.getWeight();
				total += fromNodeActivation * weight;
			}
			total += node.getBias();
			// Add the bias
			double activationValue = sigmoid(total);
			// Use the sigmoid function to normalize it
			return activationValue;
		}
	}
	
	private double sigmoid(double x) {
		// sigmoid(x) = 1 / (1 + e^(-x))
		double denominator = 1.0d + Math.pow(Math.E, (-x));
		return 1.0d / denominator;
	}
	
	public void visualize() {
		System.out.println("----Input Layer----");
		System.out.println(this.inputLayer.toString());
		for(int i = 0; i < this.hiddenLayers.length; i++) {
			System.out.println("----Hidden Layer " + (i + 1) + "/" + this.hiddenLayers.length + "----");
			System.out.println(this.hiddenLayers[i].toString());
		}
		System.out.println("----Output Layer----");
		System.out.println(this.outputLayer.toString());
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

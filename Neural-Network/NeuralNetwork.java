
public class NeuralNetwork {
	private Layer inputLayer;
	private Layer[] hiddenLayers;
	private Layer outputLayer;

	NeuralNetwork(int[] layerSizes, double minBias, double maxBias, double minWeight, double maxWeight) {
		if (layerSizes.length >= 2) {
			initAndConnect(layerSizes, minBias, maxBias, minWeight, maxWeight);
			//updateZValues();
		}
	}

	private void initAndConnect(int[] layerSizes, double minBias, double maxBias, double minWeight, double maxWeight) {
		Layer[] layers = new Layer[layerSizes.length];

		for (int i = 0; i < layerSizes.length; i++) {
			// For each layer create its set of nodes
			Node[] nodes = new Node[layerSizes[i]];

			for (int j = 0; j < layerSizes[i]; j++) {
				// For each node in the layer assign it a random activation values and bias
				double bias = Math.random() * maxBias + Math.random() * minBias;
				double activationValue = Math.random();

				if(i == 0) {
					// Input nodes do not have biases
					bias = 0;
					nodes[j] = new Node(activationValue, bias, true);
				}
				else {
					// All activation values except the inputLayer are based on the previous ones
					nodes[j] = new Node(-1.0, bias, false);
				}
				
			}
			layers[i] = new Layer(nodes, null, null);
			// Connect the nodes and assign the edges to the layers after
		}

		for (int i = 0; i < layers.length - 1; i++) {
			// For each layer except the last one, connect its output nodes to the next
			// layer
			Layer layer1 = layers[i];
			Layer layer2 = layers[i + 1];
			Edge[][] layer1OutputEdges = new Edge[layer1.getSize()][layer2.getSize()];
			// Each node in the layer has an array of in and output edges
			Edge[][] layer2InputEdges = new Edge[layer2.getSize()][layer1.getSize()];
			// The number of input edges for each node in layer2 is the number of nodes in layer 1
			
			for (int j = 0; j < layer1.getSize(); j++) {
				// For each node in layer1 generate its output edges and connect them to layer2
				// as input edges
				Node fromNode = layer1.getNodes()[j];
				Edge[] fromNodeOutputEdges = new Edge[layer2.getSize()];

				for (int k = 0; k < layer2.getSize(); k++) {
					// Create the edges from each individual fromNode to every toNode
					Node toNode = layer2.getNodes()[k];

					double weight = Math.random() * maxWeight + Math.random() * minWeight;
					Edge outputEdge = new Edge(fromNode, toNode, weight);
					fromNodeOutputEdges[k] = outputEdge;
					// Put the individual output edge into the set of output edges for the from node
				}
				fromNode.setOutputEdges(fromNodeOutputEdges);
				// Set the output edges for the individual fromNode
				layer1OutputEdges[j] = fromNodeOutputEdges;
				// Set the output edges for the fromNode in the layer
			}
			layer1.setOutputEdges(layer1OutputEdges);
			
			for(int j = 0; j < layer2.getSize(); j++) {
				// For each node in layer 2
				for(int k = 0; k < layer1.getSize(); k++) {
					// For each node in layer 1
					layer2InputEdges[j][k] = layer1OutputEdges[k][j];
				}
			}
			layer2.setInputEdges(layer2InputEdges);
			// The output edges for layer1 are layer2's input edges
		}

		this.inputLayer = layers[0];
		layers[0].setIsInputLayer(true);
		// Set the inputLayer and its value
		this.hiddenLayers = new Layer[layers.length - 2];
		for (int i = 1; i < layers.length - 1; i++) {
			// All but the first and last layers are hidden layers
			this.hiddenLayers[i - 1] = layers[i];
		}
		this.outputLayer = layers[layers.length - 1];
		layers[layers.length - 1].setIsOutputLayer(true);
		// Set the outputLayer and its value
	}

	private void updateZValues() {
		Layer previousLayer = this.inputLayer;
		for (int i = 0; i < this.hiddenLayers.length; i++) {
			// For each of the hidden layers, set the z values starting with the first one
			// as current and updating to step through
			Layer currentLayer = this.hiddenLayers[i];
			setZValuesForLayer(previousLayer, currentLayer);
			previousLayer = currentLayer;
		}
		if (this.hiddenLayers.length == 0) {
			// If there are no hidden layers
			setZValuesForLayer(this.inputLayer, this.outputLayer);
		} else {
			setZValuesForLayer(previousLayer, this.outputLayer);
		}
	}

	private void updateActivationValues() {
		for (Layer layer : this.hiddenLayers) {
			setLayerActivationValues(layer);
		}
		setLayerActivationValues(this.outputLayer);
	}

	private void setLayerActivationValues(Layer layer) {
		if (!layer.isInputLayer()) {
			// The input layer's activation values are the inputs themselves
			for (Node node : layer.getNodes()) {
				// For each node in the given layer, find its activation value
				double activationValue = getActivationValue(node);
				node.setActivationValue(activationValue);
			}
		}
	}

	private double getActivationValue(Node node) {
		double total = 0.0;
		Edge[] inputEdges = node.getInputEdges();
		if (node.isInputNode()) {
			// The node is an input node, shouldn't ever get here because of how setLayerActivationValues is called
			return node.getActivationValue();
		} else {
			for (Edge inputEdge : inputEdges) {
				// Summation of weights and activation values
				double fromNodeActivation = inputEdge.getFromNode().getActivationValue();
				if (fromNodeActivation == -1) {
					// The value hasn't been set yet, recursively set the previous required layers
					// until its valid
					fromNodeActivation = getActivationValue(inputEdge.getFromNode());
				}
				double weight = inputEdge.getWeight();
				total += fromNodeActivation * weight;
				System.out.println("Activation: " + fromNodeActivation + " Weight: " + weight);
			}
			total += node.getBias();
			System.out.println("Bias: " + node.getBias());
			System.out.println("Total: " + total);
			// Add the bias
			double activationValue = sigmoid(total);
			System.out.println("Sigmoid total: " + activationValue);
			// Use the sigmoid function to normalize it
			return activationValue;
		}
	}

	public void inputValues(double[] values) {
		if (values.length != this.inputLayer.getSize()) {
			System.out.println("The given input is not the correct size, no inputs changed.");
		} else {
			for (int i = 0; i < values.length; i++) {
				this.inputLayer.getNodes()[i].setActivationValue(values[i]);
				// For each node in the input layer, set the activation value to the
				// corresponding input
			}
			updateActivationValues();
			updateZValues();
			// Propagate the changes through the network
		}
	}

	public void setOutputValues(double[] values) {
		if (values.length != this.outputLayer.getSize()) {
			System.out.println("The given output data is not the correct size, no weights/biases changed.");
		} else {
			for (int i = 0; i < values.length; i++) {
				this.outputLayer.getNodes()[i].setActivationValue(values[i]);
				// For each node in the output layer, set the activation value to the
				// corresponding input
			}
			backPropagate();
			// Propagate the changes through the network
		}
	}

	private double costValue(double[] correctOutput) {
		if (correctOutput.length != this.outputLayer.getSize()) {
			System.out.println("The given output data is not the correct size, no weights/biases changed.");
			return Double.MAX_VALUE;
		} else {
			double sumOfSquaredErrors = 0.0;
			for (int i = 0; i < correctOutput.length; i++) {
				sumOfSquaredErrors += Math.pow((this.outputLayer.getNodes()[i].getActivationValue() - correctOutput[i]),
						2);
			}
			return sumOfSquaredErrors;
		}
	}

	private void backPropagate() {

	}

	private void setZValuesForLayer(Layer previousLayer, Layer currentLayer) {
		// z = sum of ((weights of edges between previous and current layer) *
		// (activation values of previous layer) + biases of current layer)
		if (previousLayer.isInputLayer()) {
			System.out.println(" input layer size: " + previousLayer.getSize());
			System.out.println(" input nodes: " + previousLayer.getNodes().length);
			// Z values are directly determinable
			// Base Case
			double[] zValues = new double[currentLayer.getSize()];
			// Each node's activation value has a corresponding z value
			double[] networkInputActivations = previousLayer.getActivationValues();
			// The activation values of the nodes in the previous layer

			for (int i = 0; i < currentLayer.getSize(); i++) {
				// For each node in the current layer, get the weights, and its bias to
				// calculate z
				double[] inputWeights = currentLayer.getNodes()[i].getInputWeights();
				// The weights from the node in the current layer going back to the previous
				// layer
				double bias = currentLayer.getNodes()[i].getBias();
				System.out.println(currentLayer.getSize());
				System.out.println(inputWeights.length);
				System.out.println(networkInputActivations.length);
				if(networkInputActivations.length != inputWeights.length) {
					System.out.println("WRONG");
				}
				// The bias for the current node in the current layer
				double zValue = calcZValue(networkInputActivations, inputWeights, bias);
				zValues[i] = zValue;
			}
			currentLayer.setzValues(zValues);
			// Set the Z values for the current layer
		} else if (previousLayer.getzValues() != null) {
			// The previousLayer's z values are known
			double[] previousZValues = previousLayer.getzValues();
			double[] previousActivations = new double[previousLayer.getSize()];

			for (int i = 0; i < previousActivations.length; i++) {
				// For each of the previous layer z values, find previous activation =
				// sigmoid(previous layer z value)
				previousActivations[i] = sigmoid(previousZValues[i]);
			}

			double[] zValues = new double[currentLayer.getSize()];
			// Each node's activation value has a corresponding z value

			for (int i = 0; i < currentLayer.getSize(); i++) {
				// For each node in the current layer, get the weights, and its bias to
				// calculate z
				double[] weights = previousLayer.getOutputWeights()[i];
				// The weights from the node in the current layer going back to the previous
				// layer
				double bias = currentLayer.getNodes()[i].getBias();
				// The bias for the current node in the current layer
				double zValue = calcZValue(previousActivations, weights, bias);
				zValues[i] = zValue;
			}
			currentLayer.setzValues(zValues);
			// Set the Z values for the current layer
		} else {
			// The previous layer doesnt have z values and its not the input layer
			// Should be used starting with input layer to avoid recursive calls but here as
			// a failsafe
			System.out.println("Youre doing it wrong.");
		}
	}

	private double calcZValue(double[] previousActivations, double[] inputWeights, double bias) {
		// Takes the input weights for a given node in level L and the previous activations of all the nodes in L - 1
		double zValue = 0.0;
		for (int j = 0; j < previousActivations.length; j++) {
			// The dot product of the weights and previous activations
			zValue += inputWeights[j] * previousActivations[j];
		}
		zValue += bias;
		// Add the bias as well
		return zValue;
	}

	private double sigmoid(double x) {
		// sigmoid(x) = 1 / (1 + e^(-x))
		double denominator = 1.0d + Math.pow(Math.E, (-x));
		return 1.0d / denominator;
	}

	public void visualize() {
		System.out.println("----Input Layer----");
		System.out.println(this.inputLayer.toString());

		for (int i = 0; i < this.hiddenLayers.length; i++) {
			System.out.println("----Hidden Layer " + (i + 1) + "/" + this.hiddenLayers.length + "----");
			System.out.println(this.hiddenLayers[i].toString());
		}

		System.out.println("----Output Layer----");
		System.out.println(this.outputLayer.toString());
	}

	public void setNetworkBiases(double[][] biases) {
		if (biases.length == this.hiddenLayers.length + 1) {
			// Only the hidden layers and the output layer have biases
			for (int i = 0; i < hiddenLayers.length; i++) {
				hiddenLayers[i].setBiases(biases[i]);
			}
			this.outputLayer.setBiases(biases[biases.length - 1]);
		}
	}

	public void setNetworkWeights(double[][][] weights) {
		if (weights.length == this.hiddenLayers.length + 1) {
			// There are hidden layers + 1 number of sets of edges in the network
			this.inputLayer.setWeights(null, weights[0]);
			for (int i = 1; i < hiddenLayers.length; i++) {
				// The output weights of the previous layer are the input weights of the next
				// layer
				hiddenLayers[i].setWeights(weights[i - 1], weights[i]);
			}
			this.outputLayer.setWeights(weights[weights.length - 1], null);
		}
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

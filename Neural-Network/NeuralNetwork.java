import java.util.concurrent.ThreadLocalRandom;

public class NeuralNetwork {
	private Node[][] layers;
	private double learningRate;
	private ActivationFunction[] activationFunctions;
	private Regularization regularization;
	private LossFunction lossFunction;

	public NeuralNetwork(int[] layerSizes, double learningRate, ActivationFunction[] activationFunctions,
			LossFunction lossFunction, Regularization regularization) {
		if (layerSizes.length >= 2) {
			// Must have at least two layers to be a valid network
			this.layers = new Node[layerSizes.length][];
			this.learningRate = learningRate;
			this.activationFunctions = activationFunctions;
			// Activation function may vary with layer, first value should always be
			// ActivationFunction.INPUTLAYER, which does nothing to the inputs
			this.regularization = regularization;
			this.lossFunction = lossFunction;

			for (int i = 0; i < layerSizes.length; i++) {
				// Initialize the layers
				this.layers[i] = new Node[layerSizes[i]];
			}

			initAndConnect(-1.0f, 1.0f, -1.0f, 1.0f);
			// Default values of bias and weights between -1 and 1
		}

	}

	public NeuralNetwork(int[] layerSizes, double learningRate, ActivationFunction[] activationFunctions,
			LossFunction lossFunction, Regularization regularization, double biasLower, double biasUpper,
			double weightLower, double weightUpper) {
		if (layerSizes.length >= 2) {
			// Must have at least two layers to be a valid network
			this.layers = new Node[layerSizes.length][];
			this.learningRate = learningRate;
			this.activationFunctions = activationFunctions;
			// Activation function may vary with layer, first value should always be
			// ActivationFunction.INPUTLAYER, which does nothing to the inputs
			this.regularization = regularization;
			this.lossFunction = lossFunction;

			for (int i = 0; i < layerSizes.length; i++) {
				// Initialize the layers
				this.layers[i] = new Node[layerSizes[i]];
			}

			initAndConnect(biasLower, biasUpper, weightLower, weightUpper);
		}

	}

	private void initAndConnect(double biasLower, double biasUpper, double weightLower, double weightUpper) {
		// Initialize individual nodes, 0.0 activations, random biases
		// Connect edges, random weights
		for (int i = 0; i < this.layers.length; i++) {
			// For each layer
			for (int j = 0; j < this.layers[i].length; j++) {
				// For each Node in the layer
				double bias;
				if (i == 0) {
					// The input Nodes do not have biases, or activation functions
					bias = 0.0f;
					layers[i][j] = new Node(0.0f, bias, ActivationFunction.NONE);
				}
				else {
					// Any other node has an initially random bias and potentially different
					// activation function based on the layer
					bias = (double) (biasLower + ThreadLocalRandom.current().nextDouble() * (biasUpper - biasLower));
					layers[i][j] = new Node(0.0f, bias, this.activationFunctions[i]);
				}

			}
		}

		for (int i = 0; i < this.layers.length - 1; i++) {
			// For each layer index excluding the last one, use the current and the next
			// layer
			// Connect the Nodes with new sets of Edges
			Node[] fromNodes = layers[i];
			Node[] toNodes = layers[i + 1];

			for (int j = 0; j < fromNodes.length; j++) {
				// For each Node in the current layer
				Edge[] outputEdges = new Edge[toNodes.length];
				// Each fromNode has as many output edges as Nodes in the next layer

				for (int k = 0; k < toNodes.length; k++) {
					// For each Node in the next layer, make Edges from the fromNode to each toNode
					double weight = (double) (weightLower + Math.random() * (weightUpper - weightLower));
					outputEdges[k] = new Edge(fromNodes[j], toNodes[k], weight);
				}

				fromNodes[j].setOutputEdges(outputEdges);
			}

			for (int j = 0; j < toNodes.length; j++) {
				// For each Node in the next layer
				Edge[] inputEdges = new Edge[fromNodes.length];
				// Each toNode has as many input edges as Nodes in the previous layer

				for (int k = 0; k < fromNodes.length; k++) {
					// For each Node in the current layer
					inputEdges[k] = fromNodes[k].getOutputEdges()[j];
					// Each toNode's position in the fromNode's outputEdges array is its position in
					// its layer
				}

				toNodes[j].setInputEdges(inputEdges);
			}
			// Note for the edge cases, input Nodes have no input Edges, output Nodes have
			// no output Edges, they are already set to null on Node creation
		}

	}

	public boolean input(double[] input) {
		if (input.length == this.layers[0].length) {
			// If the input is the correct size
			for (int i = 0; i < input.length; i++) {
				this.layers[0][i].setActivationValue(input[i]);
				// Set the input layer activation values
			}

			propagate();
			// Update the rest of the activation values accordingly
			return true;
		}
		return false;
		// Incorrect input size
	}

	private void propagate() {
		for (int i = 1; i < this.layers.length; i++) {
			// For each layer except the input layer
			for (Node node : this.layers[i]) {
				double z = node.calcZvalue();
				// The weighted sum of the previous layer plus the bias
				double newActivationValue = node.getActivationFunction().apply(z, null);
				// Apply the activation function to the z value
				node.setActivationValue(newActivationValue);
			}
		}
	}

	public void updateWeights(double[] observations) {
		double[][] weightGradient = getWeightGradient(observations);
		Edge[][] edges = this.getEdges();
		for (int i = 0; i < edges.length; i++) {
			for (int j = 0; j < edges[i].length; j++) {
				double updatedValue = edges[i][j].getWeight() - weightGradient[i][j];
				edges[i][j].setWeight(updatedValue);
			}
		}
	}

	private double[][] getWeightGradient(double[] observations) {
		Edge[][] edges = this.getEdges();
		double[][] gradient = new double[edges.length][];

		for (int i = 0; i < edges.length; i++) {
			// For each set of edges between the layers
			gradient[i] = new double[edges[i].length];
			for (int j = 0; j < edges[i].length; j++) {
				// For each edge in the set of edges
				double lossPartialWithRespectToWeight = 0.0;
				double[] lossDerivatives = this.getLossDerivatives(observations);
				Edge finalInputEdge = edges[i][j];

				for (int k = 0; k < this.layers[layers.length - 1].length; k++) {
					// For each output node
					Node outputNode = this.layers[layers.length - 1][k];
					double lossDerivative = lossDerivatives[k];
					lossPartialWithRespectToWeight += lossPartialWithRespectToWeight(outputNode, lossDerivative,
							finalInputEdge);
				}
				gradient[i][j] = this.learningRate * lossPartialWithRespectToWeight;
			}
		}
		return gradient;
	}

	private double lossPartialWithRespectToWeight(Node outputNode, double lossDerivative, Edge finalInputEdge) {
		// An individual weight partial derivative with respect to loss is:
		// loss derivative * output activation partial with respect to final activation
		// * final activation partial with respect to weight
		Node finalNode = finalInputEdge.getToNode();
		double outputActivationPartial = outputActivationChainRule(outputNode, finalNode);
		double finalNodeToWeight = weightPartialDerivative(finalInputEdge);
		return lossDerivative * outputActivationPartial * finalNodeToWeight;
	}

	private double outputActivationChainRule(Node outputNode, Node otherNode) {
		// Finds the partial derivative of the activation of the output value with
		// respect to the activation of the other node by applying the chain rule
		return chainRuleHelper(outputNode, otherNode);
	}

	private double chainRuleHelper(Node givenNode, Node finalNode) {
		// Recursively builds the chain rule summations from the givenNode to the
		// finalNode
		if (givenNode.equals(finalNode)) {
			return 0.0;
		}

		for (Edge inputEdge : givenNode.getInputEdges()) {
			if (inputEdge.getFromNode().equals(finalNode)) {
				// Only use the finalNode from the next layer
				return activationPartialDerivative(inputEdge);
			}
		}

		double partialDerivativeSum = 0.0;
		for (Edge inputEdge : givenNode.getInputEdges()) {
			partialDerivativeSum += activationPartialDerivative(inputEdge)
					* chainRuleHelper(inputEdge.getFromNode(), finalNode);
		}
		return partialDerivativeSum;
	}

	private double weightPartialDerivative(Edge inputEdge) {
		// The partial derivative of the activation value of the toNode of the inputEdge
		// with respect to the weight of that edge
		// Should be derivative of activation function * activation value of the
		// fromNode
		Node toNode = inputEdge.getToNode();
		double activationFunctionPrime = toNode.getActivationFunction().derivative(toNode.getActivationValue(), null);
		return activationFunctionPrime * inputEdge.getFromNode().getActivationValue();
	}

	private double activationPartialDerivative(Edge inputEdge) {
		// Input Edge to a given Node to calculate the partial derivative
		// Of the toNode activation value with respect to the fromNode activation value
		// Should be derivative of the activation function * the weight between them
		Node toNode = inputEdge.getToNode();
		double activationFunctionPrime = toNode.getActivationFunction().derivative(toNode.getActivationValue(), null);
		return activationFunctionPrime * inputEdge.getWeight();
	}

	private double[] getLossDerivatives(double[] observations) {
		double[] loss = new double[this.layers[layers.length - 1].length];
		// The same size as the outputlayer
		for (int i = 0; i < loss.length; i++) {
			loss[i] = this.lossFunction.derivative(this.layers[0][i].getActivationValue(), observations[i]);
		}

		return loss;
	}

	private double[][] getActivationValues() {
		double[][] activationValues = new double[this.layers.length][];
		for (int i = 0; i < this.layers.length; i++) {
			// For each layer
			activationValues[i] = new double[this.layers[i].length];
			for (int j = 0; j < this.layers[i].length; j++) {
				// For each node in the layer
				activationValues[i][j] = this.layers[i][j].getActivationValue();
			}
		}
		return activationValues;
	}

	private double[][] getZvalues() {
		double[][] zValues = new double[this.layers.length][];

		for (int i = 0; i < this.layers.length; i++) {
			// For each layer of Nodes
			zValues[i] = new double[this.layers[i].length];

			for (int j = 0; j < layers[i].length; j++) {
				// For each Node in the layer
				zValues[i][j] = this.layers[i][j].calcZvalue();
				// Note the input nodes return their activation values as they have no input
				// edges or biases
			}
		}
		return zValues;
	}

	private Edge[][] getEdges() {
		Edge[][] edges = new Edge[this.layers.length - 1][];
		// For N layer network there are N-1 sets of edges
		for (int i = 0; i < this.layers.length - 1; i++) {
			// For each layer except the last one
			edges[i] = new Edge[this.layers[i].length * this.layers[i + 1].length];
			// There are fromNodes * toNodes number of edges between the layers of
			// fromNodes and toNodes
			for (int j = 0; j < this.layers[i].length; j++) {
				// For each node in the fromNodes
				Edge[] fromNodeOutputEdges = this.layers[i][j].getOutputEdges();
				for (int k = 0; k < fromNodeOutputEdges.length; k++) {
					// For each output edge of the fromNode
					int edgeIndex = j * this.layers[i + 1].length + k;
					// [weightSetIndex][fromNode0OutputEdge0, ..., fromNodeOutputEdge(Layer+1.length
					// - 1),fromNode1OutputEdge0, ..., fromNodeOutputEdge(Layer+1.length)]
					edges[i][edgeIndex] = fromNodeOutputEdges[k];
				}
			}
		}
		return edges;
	}

	public void printWeights() {
		for (Edge[] edgeLayer : this.getEdges()) {
			System.out.println("Layer: ");
			for (Edge edge : edgeLayer) {
				System.out.println(edge.getWeight());
			}
		}
	}

	public ActivationFunction[] getActivationFunctions() {
		return activationFunctions;
	}

	public void setActivationFunctions(ActivationFunction[] activationFunctions) {
		this.activationFunctions = activationFunctions;
	}

	public Node[][] getLayers() {
		return layers;
	}

	public void setLayers(Node[][] layers) {
		this.layers = layers;
	}

	public LossFunction getLossFunction() {
		return lossFunction;
	}

	public void setLossFunction(LossFunction lossFunction) {
		this.lossFunction = lossFunction;
	}

	public Regularization getRegularization() {
		return regularization;
	}

	public void setRegularization(Regularization regularization) {
		this.regularization = regularization;
	}

}

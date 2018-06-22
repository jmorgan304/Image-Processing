import java.util.concurrent.ThreadLocalRandom;

public class NeuralNetwork {
	private Node[][] layers;
	private ActivationFunction[] activationFunctions;
	private Regularization regularization;
	private LossFunction lossFunction;

	public NeuralNetwork(int[] layerSizes, ActivationFunction[] activationFunctions, LossFunction lossFunction,
			Regularization regularization) {
		if (layerSizes.length >= 2) {
			// Must have at least two layers to be a valid network
			this.layers = new Node[layerSizes.length][];
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

	public NeuralNetwork(int[] layerSizes, ActivationFunction[] activationFunctions, LossFunction lossFunction,
			Regularization regularization, double biasLower, double biasUpper, double weightLower, double weightUpper) {
		if (layerSizes.length >= 2) {
			// Must have at least two layers to be a valid network
			this.layers = new Node[layerSizes.length][];
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
				double newActivationValue = node.getActivationFunction().apply(z);
				// Apply the activation function to the z value
				node.setActivationValue(newActivationValue);
			}
		}
	}

	private void backPropagate() {

	}

	private double[] getWeightGradient(double[][] zValues) {
		return null;
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

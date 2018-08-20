import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NeuralNetworkTest {
	@Test
	public void testInit() {
		int[] layerSizes = { 1, 3, 5, 3, 1 };
		ActivationFunction[] functions = { ActivationFunction.NONE, ActivationFunction.RELU, ActivationFunction.RELU,
				ActivationFunction.RELU, ActivationFunction.SIGMOID };
		LossFunction loss = LossFunction.SSE;
		Regularization reg = Regularization.NONE;

		NeuralNetwork nn = new NeuralNetwork(layerSizes, .01, functions, loss, reg);
		assertTrue(nn.getLossFunction() == loss);
		assertTrue(nn.getRegularization() == reg);

		Node[][] layers = nn.getLayers();
		assertTrue(layers.length == layerSizes.length);

		for (int i = 0; i < layerSizes.length; i++) {
			// For each layer specified in the test sizes
			assertTrue(layers[i].length == layerSizes[i]);
			// Each layer should be the specified size

			for (Node node : layers[i]) {
				// For each node in the size verified layer
				assertTrue(node.getActivationFunction() == functions[i]);
				assertTrue(node.getActivationValue() == 0);

				if (i < layerSizes.length - 1) {
					// For all layers but the last one
					assertTrue(node.getOutputEdges().length == layerSizes[i + 1]);
					// Each node's number of output edges should be the number of nodes in
					// the next layer
					for (Edge outputEdge : node.getOutputEdges()) {
						assertTrue(outputEdge != null);
						// Each edge shouldn't be null
					}
				}
				else {
					assertTrue(node.getOutputEdges() == null);
					// The array of output edges for the outputNodes should be null
				}

				if (i > 0) {
					// For all layers but the first one
					assertTrue(node.getInputEdges().length == layerSizes[i - 1]);
					// Each node's number of input edges should be the number of nodes in the
					// previous layer
					for (Edge inputEdge : node.getInputEdges()) {
						assertTrue(inputEdge != null);
						// Each edge shouldn't be null
					}
				}
				else {
					assertTrue(node.getInputEdges() == null);
					// The array of input edges for the input nodes should be null
				}

			}
		}
	}

	@Test
	public void testForwardProp() {
		int[] layerSizes = { 3, 3, 1 };
		ActivationFunction relu = ActivationFunction.RELU;
		ActivationFunction sigmoid = ActivationFunction.SIGMOID;

		ActivationFunction[] functions = { ActivationFunction.NONE, relu, sigmoid };
		LossFunction loss = LossFunction.SSE;
		Regularization reg = Regularization.NONE;

		NeuralNetwork nn = new NeuralNetwork(layerSizes, .01, functions, loss, reg, 1.0, 1.0, 1.0, 1.0);
		// All weights and biases should be 1.0

		double[] input = { 1.0, 2.0, 3.0 };

		nn.input(input);

		Node[][] layers = nn.getLayers();

		for (int i = 0; i < input.length; i++) {
			assertEquals(input[i], layers[0][i].getActivationValue(), .0000001);
			// Input layer
		}
		for (int i = 0; i < 3; i++) {
			double weightedSum = 1.0 * 1.0 + 1.0 * 2.0 + 3.0 * 1.0;
			double z = weightedSum + 1.0;
			double activationValue = relu.apply(z, null);

			assertEquals(activationValue, layers[1][i].getActivationValue(), .0000001);
			// Single hidden layer with relu activation
		}
		double outputWeightedSum = 6.0 * 1.0 + 6.0 * 1.0 + 6.0 * 1.0;
		double outputZ = outputWeightedSum + 1.0;
		double outputActivationValue = sigmoid.apply(outputZ, null);

		assertEquals(outputActivationValue, layers[2][0].getActivationValue(), .0000001);
	}

	@Test
	public void testBackProp() {
		int[] layerSizes = { 3, 3, 2 };
		ActivationFunction relu = ActivationFunction.RELU;
		ActivationFunction sigmoid = ActivationFunction.SIGMOID;

		ActivationFunction[] functions = { ActivationFunction.NONE, relu, sigmoid };
		LossFunction loss = LossFunction.SSE;
		Regularization reg = Regularization.NONE;

		NeuralNetwork nn = new NeuralNetwork(layerSizes, .01, functions, loss, reg, -.1, .1, -1, 1);

		double[] input = { 1.0, -2.0, 3.0 };
		double[] observations = { 0.0, 1.0 };

		for (int i = 0; i < 1000001; i++) {
			if (i % 100000 == 0) {
				nn.input(input);
				nn.printWeights();
				nn.updateWeights(observations);
				nn.printWeights();
			}
		}

	}
}

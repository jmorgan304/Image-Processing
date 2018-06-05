import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

public class NeuralNetworkTester {

	@Test
	public void testCreation() {
		int[] layerSizes = { 2, 4, 4, 1 };
		
		NeuralNetwork nn = new NeuralNetwork(layerSizes, -1, 1, -1, 5);
		Layer inputLayer = nn.getInputLayer();
		Layer[] hiddenLayers = nn.getHiddenLayers();
		Layer outputLayer = nn.getOutputLayer();
		
		nn.visualize();
		
		// Layer Sizes (Number of Nodes)
		assertEquals(layerSizes[0], inputLayer.getSize());
		for(int i = 0; i < hiddenLayers.length - 1; i++) {
			assertEquals(layerSizes[i + 1], hiddenLayers[i].getSize());
		}
		assertEquals(layerSizes[layerSizes.length - 1], outputLayer.getSize());
		
		// Edge Sizes (Connections to and from each layer)
		for(Edge[] outputEdges : inputLayer.getOutputEdges()) {
			assertEquals(layerSizes[1], outputEdges.length);
		}
		
		for(int i = 1; i < hiddenLayers.length - 1; i++) {
			for(Edge[] inputEdges : hiddenLayers[i].getInputEdges()) {
				assertEquals(layerSizes[i - 1], inputEdges.length);
			}
			for(Edge[] outputEdges : hiddenLayers[i].getOutputEdges()) {
				assertEquals(layerSizes[i + 1], outputEdges.length);
			}
		}
		
		for(Edge[] inputEdges : outputLayer.getInputEdges()) {
			assertEquals(layerSizes[layerSizes.length - 2], inputEdges.length);
		}
		
		// Input and Output Layer validation
		assertTrue(inputLayer.isInputLayer());
		assertTrue(outputLayer.isOutputLayer());
	}
	
	@Test
	public void testValues() {
		int[] layers = { 1, 2, 1 };
		NeuralNetwork simple = new NeuralNetwork(layers, 0, 0, 0, 0);
		simple.visualize();
		double[][] biases = { {100}, {10} };
		
		double[][][] weights = { { {1, 2} }, { {3}, {4} } };
		// One node with two weights, two nodes with one weight
		
		// { 2, 2, 1 }
		// { { {0, 0}, {0, 0} }, { {0}, {0} } }
		// { 3, 2, 1 }
		// { { {0, 0}, {0, 0}, {0, 0} }, { {0}, {0} } }
		
		
		int numberOfWeightSets = layers.length - 1;
		// Number of layers - 1
		double[][][] layerFromNodeWeightSets = new double[numberOfWeightSets][][];
		// [layerIndex][fromNodes][individualEdgeIndex]
		
		for(int layerIndex = 0; layerIndex < numberOfWeightSets; layerIndex++) {
			// For each layer starting at the 0th, not including the output layer, get fromNodes for that layer
			int numberOfFromNodes = layers[layerIndex];
			double[][] fromNodes = new double[numberOfFromNodes][];
			
			for(int fromNodeIndex = 0; fromNodeIndex < numberOfFromNodes; fromNodeIndex++) {
				// For each fromNode in the layer, find the number of toNodes in the next layer
				int numberOfToNodes = layers[layerIndex + 1];
				double[] fromNodeOutputWeights = new double[numberOfToNodes];
				
				for(int individualEdgeIndex = 0; individualEdgeIndex < numberOfToNodes; individualEdgeIndex++) {
					// For each toNode in the next layer, assign an individual weight from the from node to the toNode
					fromNodeOutputWeights[individualEdgeIndex] = (1 + layerIndex) * (1+ fromNodeIndex) * (1+ individualEdgeIndex);
				}
				fromNodes[fromNodeIndex] = fromNodeOutputWeights;
			}
			layerFromNodeWeightSets[layerIndex] = fromNodes;
		}

		
		simple.setNetworkBiases(biases);
		simple.setNetworkWeights(layerFromNodeWeightSets);
		simple.visualize();
		simple.setNetworkWeights(weights);
		simple.visualize();
		double[] input = {0};
		simple.inputValues(input);
		simple.visualize();
		
		
		
		/*
		int[] layerSizes = { 3, 3, 2 };

		NeuralNetwork nn = new NeuralNetwork(layerSizes, -1, 1, -10, 10);
		
		nn.visualize();
		
		double[][] biases = { { 5, -3, 4 }, { 5, 4 } };
		double[][][] weights = { { { -7, 2, 8 }, { 5, -7, 1 }, { 8, -7, 7 } }, { { -7, 0 }, { 7, 1 }, { -10, 3 } } };

		nn.setNetworkBiases(biases);
		nn.setNetworkWeights(weights);
		
		double[][] testInputs = { { 0, 0, 0 }, { 1, 1, 1 } };
		// { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 }, { 1, 1, 0 }, { 0, 1, 1 },
		// { 1, 0, 1 }, 
		
		double[][] testOutputs = { { 0.0000107423, 0.999656 }, { 0.00000614490, 0.999088 } };
		
		for(int i = 0; i < testInputs.length; i++) {
			nn.inputValues(testInputs[i]);
			nn.visualize();
			double[] output = nn.getOutputLayer().getActivationValues();
			for(int j = 0; j < testOutputs[i].length; j++) {
				assertEquals(output[j], testOutputs[i][j], Double.MIN_VALUE);
			}
		}
		
		*/
	}
}

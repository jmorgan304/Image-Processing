import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

public class NeuralNetworkTester {

	@Test
	public void testCreation() {
		int[] layerSizes = { 784, 16, 16, 10 };
		
		NeuralNetwork nn = new NeuralNetwork(layerSizes, -10, 10, -5, 5);
		Layer inputLayer = nn.getInputLayer();
		Layer[] hiddenLayers = nn.getHiddenLayers();
		Layer outputLayer = nn.getOutputLayer();

		// Layer Sizes (Number of Nodes)
		assertEquals(784, inputLayer.getSize());
		for(Layer layer : hiddenLayers) {
			assertEquals(16, layer.getSize());
		}
		assertEquals(10, outputLayer.getSize());
		
		// Edge Sizes (Connections to and from each layer)
		assertEquals(16, inputLayer.getOutputEdges().length);
		
		assertEquals(16, hiddenLayers[0].getInputEdges().length);
		assertEquals(16, hiddenLayers[0].getOutputEdges().length);
		
		assertEquals(16, hiddenLayers[1].getInputEdges().length);
		assertEquals(10, hiddenLayers[1].getOutputEdges().length);
		
		assertEquals(10, outputLayer.getInputEdges().length);
		
		// Input and Output Layer validation
		assertTrue(inputLayer.isInputLayer());
		assertTrue(outputLayer.isOutputLayer());
	}
}

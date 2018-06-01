import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

public class NeuralNetworkTester {

	@Test
	public void testCreation() {
		int[] layerSizes = { 1, 4, 4, 1 };
		
		NeuralNetwork nn = new NeuralNetwork(layerSizes, -10, 10, -5, 5);
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
		
		
		
		for(int i = 0; i < hiddenLayers.length - 1; i++) {
			for(Edge[] inputEdges : hiddenLayers[i].getInputEdges()) {
				assertEquals(layerSizes[i + 1], inputEdges.length);
			}
			for(Edge[] outputEdges : hiddenLayers[i].getOutputEdges()) {
				assertEquals(layerSizes[i + 1], outputEdges.length);
			}
		}
		
		for(Edge[] inputEdges : outputLayer.getInputEdges()) {
			assertEquals(layerSizes[layerSizes.length - 1], inputEdges.length);
		}
		
		// Input and Output Layer validation
		assertTrue(inputLayer.isInputLayer());
		assertTrue(outputLayer.isOutputLayer());
	}
}

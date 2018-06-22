
public class Model {
	private NeuralNetwork network;
	private double learningRate;
	private double regularizationRate;

	public Model() {

	}

	public NeuralNetwork getNetwork() {
		return network;
	}

	public void setNetwork(NeuralNetwork network) {
		this.network = network;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public double getRegularizationRate() {
		return regularizationRate;
	}

	public void setRegularizationRate(double regularizationRate) {
		this.regularizationRate = regularizationRate;
	}

}

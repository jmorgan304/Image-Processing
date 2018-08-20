
public class NNMath {

	public static void main(String[] args) {
		double[][] a = { { 1, 1, 1 }, { 2, 2, 2 }, { 3, 3, 3 } };
		double[][] b = { { 1, 1, 1 }, { 2, 2, 2 }, { 3, 3, 3 } };
		double[] c = { 2, 1, 1 };

		double[][] result = matMul(a, b);
		for (double[] row : result) {
			String line = "";
			for (double element : row) {
				line += " " + element;
			}
			System.out.println(line);
		}

		double[] result2 = matMul(c, b);
		String line = "";
		for (double element : result2) {
			line += " " + element;
		}
		System.out.println(line);

		double[] d = { 2, 3, 4 };
		double result3 = dot(c, d);
		System.out.println(result3);
	}

	public static double[][] matMul(double[][] matA, double[][] matB) {
		if (matA.length != matB[0].length) {
			// Columns of A must match rows of B
			throw new IllegalArgumentException("If A is NxM, B must be MxP");
		}
		double[][] result = new double[matA.length][matB[0].length];
		for (int aRow = 0; aRow < matA.length; aRow++) {
			for (int bRow = 0; bRow < matB.length; bRow++) {
				for (int bCol = 0; bCol < matB[0].length; bCol++) {
					result[aRow][bCol] += matA[aRow][bRow] * matB[bRow][bCol];
				}
			}
		}
		return result;
	}

	public static double[] matMul(double[] vecA, double[][] matB) {
		if (vecA.length != matB.length) {
			// Columns of A must match rows of B
			throw new IllegalArgumentException("If A is 1xM, B must be MxP");
		}
		double[] result = new double[vecA.length];
		for (int aCol = 0; aCol < vecA.length; aCol++) {
			for (int bRow = 0; bRow < matB.length; bRow++) {
				for (int bCol = 0; bCol < matB[0].length; bCol++) {
					result[aCol] += vecA[aCol] * matB[bRow][bCol];
				}
			}
		}
		return result;
	}

	public static double dot(double[] a, double[] b) {
		if (a.length != b.length) {
			throw new IllegalArgumentException("a and b must be the same length");
		}
		double dot = 0.0;
		for (int i = 0; i < a.length; i++) {
			dot += a[i] * b[i];
		}
		return dot;
	}
}

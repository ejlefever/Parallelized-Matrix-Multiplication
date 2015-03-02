
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.lang.Character;

  
public class MatrixMult {

	public static void main(String[] args) throws IOException {
		
		

		Path path = FileSystems.getDefault().getPath("a1.txt");
		Path path2 = FileSystems.getDefault().getPath("a1.txt");

		int[][] a1 = makeMatrix(path);
		int[][] b1 = makeMatrix(path2);

		int[][] c = multiply(a1, b1);

		int[][] d = add(a1, b1);

		printMatrix(d);
		
	
	}
	
	
    public static void printMatrix(int[][] m) {
	  for (int i = 0; i < m.length; i++) {
	      for (int j = 0; j < m[0].length; j++) {
		  System.out.print(m[i][j] + "\t");
	      }
	      System.out.println();
	  }
    } 

    public static boolean isNumber(char bob) {

    	if (bob == '0' || bob == '1' || bob == '2' || bob == '3' || bob == '4' || bob == '5'
					 || bob == '6' || bob == '7' || bob == '8' || bob == '9') {
    		return true;
    	}
    	return false;

    }

    public static int[][] makeMatrix(Path path) throws IOException {

    	int x = 0;
		int j = 0;
		int counterx = 0;
		int countery = 0;
		int is93 = 0;
		String thisLine, line;
		StringBuffer matrix = new StringBuffer();


		BufferedReader br =	Files.newBufferedReader(path);
		while ((thisLine = br.readLine()) != null) {
			counterx++;
			for (int i = 0; i < thisLine.length(); i++) {
				if (isNumber(thisLine.charAt(i))) {
					is93++;
					if (is93>93) {
						countery++;
						matrix.append(thisLine.charAt(i));
					}
				}
			}
		}

		counterx = counterx - 2;
		countery = countery/counterx;

		int[][] root = new int[counterx][countery];

		int counter = 0;
		for (int i = 0; i < counterx; i++) {
			for (int k = 0; k < countery; k++) {
				root[i][k] = Character.getNumericValue(matrix.charAt(counter));
				counter++;
			}
		}

		return root;

	}

	public static int[][] multiply(int[][] a, int[][] b) {

		int aRows = a.length;
        int aColumns = a[0].length;
        int bRows = b.length;
        int bColumns = b[0].length;

        int bigRow = Math.max(aRows, bRows);
        int bigColumn = Math.max(aColumns, bColumns);

        int[][] c = new int[bigRow][bigColumn];

        System.out.println("aRows = " + aRows + " aColumns = " + aColumns);
        System.out.println("bRows = " + bRows + " bColumns = " + bColumns);

        for (int i = 0; i < bigRow; i++) { // aRow
            for (int j = 0; j < bigColumn; j++) { // bColumn
                for (int k = 0; k < bigRow; k++) { // aColumn
                	System.out.println("i = " + i + " j = " + j + " k = " + k);
                    c[i][j] += a[i][k] * a[k][j];
                }
            }
        }

        return c;

	}

	public static int[][] add(int[][] a, int[][] b) {

		int[][] summer = new int[a.length][a[0].length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				summer[i][j] = a[i][j] + b[i][j];
			}
		}
		return summer;

	}
	
}













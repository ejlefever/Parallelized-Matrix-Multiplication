import mpi.* ;
import java.util.*;
import java.io.*;
import java.lang.Character;
import java.lang.Math;


public class Matrix {

    public static void main(String[] args) throws IOException, MPIException{

        MPI.Init(args);

        int[][] a1 = makeMatrix("small1.txt");
        int[][] b1 = makeMatrix("small1.txt");
        //Now we're going to scatter the tasks to different processes                                           

        //      int n = 8;                                                                                      
        int source;  // Rank of sender                                                                          
        int dest;    // Rank of receiver                                                                        
        int tag=50;  // Tag for messages                                                                        
        int myrank = MPI.COMM_WORLD.Rank() ; // Rank of process                                                 
        int      p = MPI.COMM_WORLD.Size() ; //Number of processes                                              
        int blockSize = a1.length/2;
        int[][][][] messageBuffer = new int[1][2][blockSize][blockSize]; //buffer in which to store received me\
ssage                                                                                                           
        int[][][][] messages = new int[p][2][blockSize][blockSize];

        if (myrank == 0) {
            int[][] a1block11 = new int[blockSize][blockSize];
            int[][] a1block12 = new int[blockSize][blockSize];
            int[][] a1block21 = new int[blockSize][blockSize];
            int[][] a1block22 = new int[blockSize][blockSize];
            int[][] b1block11 = new int[blockSize][blockSize];
            int[][] b1block12 = new int[blockSize][blockSize];
            int[][] b1block21 = new int[blockSize][blockSize];
            int[][] b1block22 = new int[blockSize][blockSize];


            for (int i = 0; i < blockSize; i++) {
                for (int j = 0; j < blockSize; j++) {
                    a1block11[i][j] = a1[i][j];
                    a1block12[i][j] = a1[i][blockSize+j];
                    a1block21[i][j] = a1[blockSize+i][j];
                    a1block22[i][j] = a1[blockSize+i][blockSize+j];
                }
            }

            for (int i = 0; i < blockSize; i++) {
        	for (int j = 0; j < blockSize; j++) {
                    a1block11[i][j] = a1[i][j];
                    a1block12[i][j] = a1[i][blockSize+j];
                    a1block21[i][j] = a1[blockSize+i][j];
                    a1block22[i][j] = a1[blockSize+i][blockSize+j];
                }
            }

            for (int i = 0; i < blockSize; i++) {
                for (int j = 0; j < blockSize; j++) {
                    b1block11[i][j] = b1[i][j];
                    b1block12[i][j] = b1[i][blockSize+j];
                    b1block21[i][j] = b1[blockSize+i][j];
                    b1block22[i][j] = b1[blockSize+i][blockSize+j];
                }
            }

            int[][][] temp = new int[2][blockSize][blockSize];
            //this int[][][] will contain the two matrices being sent to the process                            
            temp = set(temp, a1block11, b1block11); //returns temp populated with a1block11 and b1block11       
            messages[0] = temp; //process0 gets a11, b11                                                        
            temp = set(temp, a1block12, b1block21);
            messages[1] = temp; //process1 gets a12, b12                                                        
            temp = set(temp, a1block11, b1block12);
            messages[2] = temp; //process2 gets a11, b12                                                        
            temp = set(temp, a1block12, b1block22);
            messages[3] = temp; //process3 gets a12, b22                                                        
            temp = set(temp, a1block21, b1block11);
            messages[4] = temp; //process4 gets a21, b11                                                        
            temp = set(temp, a1block22, b1block21);
            messages[5] = temp; //process5 gets a22, b21                                                        
            temp = set(temp, a1block21, b1block12);
            messages[6] = temp; //process6 gets a21, b12  
            temp = set(temp, a1block22, b1block22);
            messages[7] = temp; //process7 gets a22, b22                                                        

            System.out.println("messages ready to be sent");

        } //myrank == 0;                                                                                        

        MPI.COMM_WORLD.Scatter(messages,0,1,MPI.OBJECT,messageBuffer,0,1,MPI.OBJECT,0);

        //This is where we're doing the dot product                                                            \
                                                                                                                
        int[][] product = new int[blockSize][blockSize];
        product = multiply(messageBuffer[0][0], messageBuffer[0][1]);
        //writeToFile(product);                                                                                 

        MPI.COMM_WORLD.Send(product, 0, 1, MPI.OBJECT, 0, 50);
        System.out.println("Sent" + myrank);

        if (myrank == 0) {

            int[][] from0 = new int[blockSize][blockSize];
            MPI.COMM_WORLD.Recv(from0, 0, 1, MPI.OBJECT, 0, 50);
            int[][] from1 = new int[blockSize][blockSize];
            MPI.COMM_WORLD.Recv(from1, 0, 1, MPI.OBJECT, 1, 50);
            int[][] from2 = new int[blockSize][blockSize];
            MPI.COMM_WORLD.Recv(from2, 0, 1, MPI.OBJECT, 2, 50);
            int[][] from3 = new int[blockSize][blockSize];
            MPI.COMM_WORLD.Recv(from3, 0, 1, MPI.OBJECT, 3, 50);
            int[][] from4 = new int[blockSize][blockSize];
            MPI.COMM_WORLD.Recv(from4, 0, 1, MPI.OBJECT, 4, 50);
            int[][] from5 = new int[blockSize][blockSize];
            MPI.COMM_WORLD.Recv(from5, 0, 1, MPI.OBJECT, 5, 50);
            int[][] from6 = new int[blockSize][blockSize];
            MPI.COMM_WORLD.Recv(from6, 0, 1, MPI.OBJECT, 6, 50);
            int[][] from7 = new int[blockSize][blockSize];
            MPI.COMM_WORLD.Recv(from7, 0, 1, MPI.OBJECT, 7, 50);

            System.out.println("Received sent messages");

            int[][] c11 = new int[blockSize][blockSize];
            c11 = add(from0, from1);
            int[][] c12 = new int[blockSize][blockSize];
            c12 = add(from2, from3);
            int[][] c21 = new int[blockSize][blockSize];
            c21 = add(from4, from5);
            int[][] c22 = new int[blockSize][blockSize];
            c22 = add(from6, from7);
        } //myrank==0                                                                                           

        /*MPI.COMM_WORLD.Scatter(messages,0,1,MPI.OBJECT,messageBuffer,0,1,MPI.OBJECT,0);                       
                                                                                                                
        //This is where we're doing the dot product                                                             
        //int[][] product = new int[blockSize][blockSize];                                                      
        product = multiply(messageBuffer[0][0], messageBuffer[0][1]);                                           
        //writeToFile(product);                                                                                 
        MPI.COMM_WORLD.Send(product, 0, 1, MPI.OBJECT, 0, 50); */

        MPI.Finalize();

    } //main                                                                                                    


    public static void printMatrix(int[][] m) {
        for (int i = 0; i < m[0].length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                System.out.print(m[i][j] + "\t");
            }
            System.out.println();
        }
    }  //printMatrix                                                                                            

    public static void writeToFile(int[][] m) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("results.txt");
        for (int i = 0; i < m[0].length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                writer.print(m[i][j] + "\t");
            }
            System.out.println();
        }
        writer.close();
    }

    public static boolean isNumber(char bob) {

        if (bob == '0' || bob == '1' || bob == '2' || bob == '3' || bob == '4' || bob == '5'
            || bob == '6' || bob == '7' || bob == '8' || bob == '9') {
            return true;
        }
        return false;

    } //isNumber                                                                                                

    public static int[][] makeMatrix(String file_name) throws IOException{

        int x = 0;
        int j = 0;
        int counterx = 0;
        int countery = 0;
  	String thisLine=null;
        StringBuffer matrix = new StringBuffer();


        BufferedReader br = new BufferedReader(new FileReader(new File(file_name)));
        while (br.ready()) {

            while ((thisLine = br.readLine()) != null) {
                counterx++;
                for (int i = 0; i < thisLine.length(); i++) {
                    if (isNumber(thisLine.charAt(i))) {
                            countery++;
                            matrix.append(thisLine.charAt(i));
                    }
                }
            }
        }
        br.close();
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

    } //makeMatrix                                                                                              

    public static int[][][] set(int[][][] temp, int[][] ablock, int[][] bblock) { //fills temp with ablock and \
bblock                                                                                                          
        int blockSize = ablock.length;
        for (int j = 0; j<blockSize; j++) {
            for (int k = 0; k<blockSize; k++) {
                temp[0][j][k] = ablock[j][k];
                }
            }
        for (int j = 0; j<blockSize; j++) {
            for (int k = 0; k<blockSize; k++) {
                temp[1][j][k] = bblock[j][k];
            }
        }
        return temp;
    } //set                                                                                                     

    public static int[][] multiply(int[][] a, int[][] b) {
    	int aRows = a.length;
        int aColumns = a[0].length;
        int bRows = b.length;
        int bColumns = b[0].length;

        int bigRow = Math.max(aRows, bRows);
        int bigColumn = Math.max(aColumns, bColumns);

        int[][] c = new int[bigRow][bigColumn];

        for (int i = 0; i < bigRow; i++) {
            for (int j = 0; j < bigColumn; j++) {
                for (int k = 0; k < bigRow; k++) {
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

} //Matrix class



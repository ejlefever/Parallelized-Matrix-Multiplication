import mpi.* ;
import java.util.*;
import java.io.*;
import java.lang.Character;


public class Matrix {

    public static void main(String[] args) throws IOException, MPIException{

        MPI.Init(args);

        int[][] a1 = makeMatrix("a1.txt");
        int[][] b1 = makeMatrix("a1.txt");
        printMatrix(a1);
        //Now we're going to scatter the tasks to different processes                                                

        int n = 4;
        int source;  // Rank of sender                                                                               
        int dest;    // Rank of receiver                                                                             
        int tag=50;  // Tag for messages                                                                             
        int myrank = MPI.COMM_WORLD.Rank() ; // Rank of process                                                      
        int      p = MPI.COMM_WORLD.Size() ; //Number of processes                                                   
        int blockSizeX = a1.length/2;
        int blockSizeY = a1[0].length/2;
        System.out.println("a1.length = " + a1.length);
        System.out.println("a1[0].length = " + a1[0].length);
        int[][][] messageBuffer = new int[1][blockSizeX][blockSizeY];
        int[][][] messages = new int[p][blockSizeX][blockSizeY];

        if (myrank == 0) {
            int[][] block11 = new int[blockSizeX][blockSizeY];
            int[][] block12 = new int[blockSizeX][blockSizeY];
            int[][] block21 = new int[blockSizeX][blockSizeY];
            int[][] block22 = new int[blockSizeX][blockSizeY];
            System.out.println("blockSizeX: " + blockSizeX);
            System.out.println("blockSizeY: " + blockSizeY);
             for (int i = 0; i < blockSizeX; i++) {
                for (int j = 0; j < blockSizeY; j++) {
                    block11[i][j] = a1[i][j];
                    block12[i][j] = a1[i][blockSizeY+j];
                    block21[i][j] = a1[blockSizeX+i][j];
                    block22[i][j] = a1[blockSizeX+i][blockSizeY+j];
                }
            }

            //      System.out.println("block 11 filled:");                                                          
            // printMatrix(block11);                                                                                 
            //System.out.println("block 12 filled:");                                                                
            //printMatrix(block12);                                                                                  
            messages[0] = block11;
            messages[1] = block12;
            messages[2] = block21;
            messages[3] = block22;
        }

        MPI.COMM_WORLD.Scatter(messages,0,1,MPI.OBJECT,messageBuffer,0,1,MPI.OBJECT,0);
        //      System.out.println("Process " + myrank + " received message:");
        //printMatrix(messageBuffer[0]);                                                                             

        MPI.Finalize();

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

        if (bob == '0' || bob == '1' || bob == '2' || bob == '3' || bob == '4' || bob == '5' || bob == '6' || bob == '7' || bob == '8' || bob == '9') {
 
 	return true;
        }
        return false;

    }

    public static int[][] makeMatrix(String file_name) throws IOException{

        int x = 0;
        int j = 0;
        int counterx = 0;
        int countery = 0;
        String thisLine=null;
        StringBuffer matrix = new StringBuffer();


        BufferedReader br = new BufferedReader(new FileReader(new File(file_name)));
        while (br.ready()) {
            // do something with the line                                                                            
            while ((thisLine = br.readLine()) != null) {
            	counterx++;
                for (int i = 0; i < thisLine.length(); i++) {
                    if (isNumber(thisLine.charAt(i))) {
                        //      is93++;                                                                              
                        //if (is93>93) {                                                                             
                            countery++;
                            matrix.append(thisLine.charAt(i));
                            //}                                                                                      
                    }
                }
            }
        }
        br.close();
        //counterx = counterx - 2;                                                                                   
        countery = countery/counterx;

        int[][] root = new int[counterx][countery];

        int counter = 0;
        for (int i = 0; i < counterx; i++) {
        	 for (int i = 0; i < thisLine.length(); i++) {
                    if (isNumber(thisLine.charAt(i))) {
                        //      is93++;                                                                              
                        //if (is93>93) {                                                                             
                            countery++;
                            matrix.append(thisLine.charAt(i));
                            //}                                                                                      
                    }
                }
            }
        }
        br.close();
        //counterx = counterx - 2;                                                                                   
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



}


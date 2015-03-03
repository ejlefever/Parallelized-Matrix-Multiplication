import mpi.* ;
import java.util.*;
import java.io.*;
import java.lang.Character;
import java.lang.Math;


public class Matrix {

    public static void main(String[] args) throws IOException, MPIException{

        MPI.Init(args);

        int[][] a1 = makeMatrix("a1.txt");
        int[][] b1 = makeMatrix("b1.txt");
        //Now we're going to scatter the tasks to different processes                                                                        

        int n = 4;
        int source;  // Rank of sender                                                                                                       
        int dest;    // Rank of receiver                                                                                                     
        int tag=50;  // Tag for messages                                                                                                     
        int myrank = MPI.COMM_WORLD.Rank() ; // Rank of process                                                                              
        int      p = MPI.COMM_WORLD.Size() ; //Number of processes                                                                           
        int a1blockSizeX = a1.length/2;
        int a1blockSizeY = a1[0].length/2;
        int b1blockSizeX = b1.length/2;
        int b1blockSizeY = b1[0].length/2;
        int bigblockSizeX = Math.max(a1.length, b1.length);
        int bigblockSizeY = Math.max(a1[0].length, b1[0].length);
        //System.out.println("a1.length = " + a1.length);                                                                                    
        //      System.out.println("a1[0].length = " + a1[0].length);                                                                        
        int[][][] messageBuffer = new int[1][bigblockSizeX][bigblockSizeY];
        int[][][] messages = new int[p][bigblockSizeX][bigblockSizeY];

        if (myrank == 0) {
            int[][] a1block11 = new int[a1blockSizeX][a1blockSizeY];
            int[][] a1block12 = new int[a1blockSizeX][a1blockSizeY];
            int[][] a1block21 = new int[a1blockSizeX][a1blockSizeY];
            int[][] a1block22 = new int[a1blockSizeX][a1blockSizeY];
            int[][] b1block11 = new int[b1blockSizeX][b1blockSizeY];
            int[][] b1block12 = new int[b1blockSizeX][b1blockSizeY];
            int[][] b1block21 = new int[b1blockSizeX][b1blockSizeY];
            int[][] b1block22 = new int[b1blockSizeX][b1blockSizeY];
            System.out.println("a1blockSizeX: " + a1blockSizeX);
            System.out.println("a1blockSizeY: " + a1blockSizeY);
            System.out.println("a1.length: " + a1.length);
            System.out.println("a1[0].length: " + a1[0].length);
            System.out.println("b1.length: " + b1.length);
            System.out.println("b1[0].length: " + b1[0].length);
            System.out.println("b1blockSizeX: " + b1blockSizeX);
            System.out.println("b1blockSizeY: " + b1blockSizeY);
            for (int i = 0; i < a1blockSizeX; i++) {
                for (int j = 0; j < a1blockSizeY; j++) {
                    a1block11[i][j] = a1[i][j];
                    a1block12[i][j] = a1[i][a1blockSizeY+j];
                    a1block21[i][j] = a1[a1blockSizeX+i][j];
                    a1block22[i][j] = a1[a1blockSizeX+i][a1blockSizeY+j];
                }
            }
            for (int i = 0; i < b1blockSizeX; i++) {
                for (int j = 0; j < b1blockSizeY; j++) {
                    b1block11[i][j] = b1[i][j];
                    b1block12[i][j] = b1[i][b1blockSizeY+j];
                    b1block21[i][j] = b1[b1blockSizeX+i][j];
                    b1block22[i][j] = b1[b1blockSizeX+i][b1blockSizeY+j];
                }
            }


            System.out.println("block 11 filled:");
            printMatrix(b1block11);
            System.out.println("block 12 filled:");
            printMatrix(b1block12);
            System.out.println("block 21 filled:");
            printMatrix(b1block21);
            System.out.println("block 22 filled:");
            printMatrix(b1block22);

            int[][][] temp = new int[1][][]
            messages[0] = new int[][][];a1block11;
            messages[1] = a1block12;
            messages[2] = a1block21;
            messages[3] = a1block22;
            messages[4] = b1block11;
            messages[5] = b1block12;
            messages[6] = b1block21;
            messages[7] = b1block22;
        }

        MPI.COMM_WORLD.Scatter(messages,0,1,MPI.OBJECT,messageBuffer,0,1,MPI.OBJECT,0);
        /*if (myrank == 7) {                                                                                                                 
            System.out.println("Process " + myrank + " received message:");                                                                  
            printMatrix(messageBuffer[0]);                                                                                                   
            }*/

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

        if (bob == '0' || bob == '1' || bob == '2' || bob == '3' || bob == '4' || bob == '5'
            || bob == '6' || bob == '7' || bob == '8' || bob == '9') {
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
        //      System.out.println("counterx: " + counterx + " countery: " + countery);                                                      
        return root;

    }



}

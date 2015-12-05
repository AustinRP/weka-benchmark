//package parallel.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import weka.core.matrix.Matrix;

/**
 * Benchmark.java
 * 
 * This class tests and runs matrix arithmetic algorithms with a variety
 * of input sizes and numbers of threads. The program reads in a 
 * "DenseA.txt", "DenseB.txt", "SparseA.txt", and "SparseB.txt" which
 * are then used for multiple trials. The time results of all of the trials
 * are printed to console. 
 * 
 * For the sake of simplicity generating a CSV, only the dense times are 
 * printed at the moment. This can currently be adjusted by changing the 
 * logic at the very end of main.
 * 
 * OUTPUT FORMAT
 *   Each row corresponds to a number of threads being used, starting with 1.
 *   Each column corresponds to an input size, where column n has input size
 *     2^n starting with n=1.
 *   The unit is nanoseconds.
 * 
 * The time to complete each algorithm at each input size and 
 * number of threads is stored in a CSV. 
 * 
 * Note: The tests that are used here should not be considered exhaustive.
 *       They are designed with the intent of generating data for the
 *       purpose of analyzing speedup and efficiency of parallel
 *       algorithms.
 *       
 *       Additionally, we assume that the weka.core.matrix.Matrix class
 *       properly generates submatrices with getMatrix(int, int, int, int).
 * 
 * @author austinpahl
 *
 */
public class Benchmark {

  // PATH corresponds to the location of the input files used below.
  public static final String PATH = "./";
  public static final String OUTPUT_DELIM = ",";
  // OUTPUT_FILENAME specifies the format of the output files
  // created in main. The name of the function that is tested
  // should be included.
  public static final String OUTPUT_FILENAME = "_times.csv";
  
  // Number of trials that should be run for each combination of
  // threads and matrices. The output time will be the average
  // time to completion for each trial
  public static final int NUM_TRIALS_PER_TEST = 10;
  
  // 2^(MAX_INPUT_POWER) is the maximum square matrix dimension that will be 
  // tested.
  public static final int MAX_INPUT_POWER = 7;
  public static final int MAX_NUM_THREADS = 4;
  
  
  /**
   * Main function to run through all test methods and 
   * handle the data that will be output to CSV.
   */
  public static void main(String[] args) {
    // TODO if(args.length != 
    int maxInputSize = (int) Math.pow(2, MAX_INPUT_POWER);
    long[][] denseTimes = new long[MAX_NUM_THREADS][MAX_INPUT_POWER];
    long[][] sparseTimes = new long[MAX_NUM_THREADS][MAX_INPUT_POWER];
    
    double[][] da = new double[maxInputSize][maxInputSize];
    double[][] db = new double[maxInputSize][maxInputSize];
    double[][] sa = new double[maxInputSize][maxInputSize];
    double[][] sb = new double[maxInputSize][maxInputSize];

    da = readFile(PATH + "DenseA.txt", maxInputSize);
    db = readFile(PATH + "DenseB.txt", maxInputSize);
    sa = readFile(PATH + "SparseA.txt", maxInputSize);
    sb = readFile(PATH + "SparseB.txt", maxInputSize);
    
    Matrix mda = new Matrix(da);
    Matrix mdb = new Matrix(db);
    Matrix msa = new Matrix(sa);
    Matrix msb = new Matrix(sb);
    
    // TODO Implement a completely separate loop for checking that the output
    // from the functions is what we expect.
    
    for(int i = 1; i <= MAX_NUM_THREADS; i++) {
      Matrix.num_threads = i;
      for(int j = 1; j <= MAX_INPUT_POWER; j++) {
        int dAvgTrial = 0;
        int sAvgTrial = 0;
        // Get the appropriate submatrices for the current
        // input size
        Matrix subDA = mda.getMatrix(0, 0, j - 1, j - 1);
        Matrix subDB = mdb.getMatrix(0, 0, j - 1, j - 1);
        Matrix subSA = msa.getMatrix(0, 0, j - 1, j - 1);
        Matrix subSB = msb.getMatrix(0, 0, j - 1, j - 1);
        
        // TODO Set the desired number of threads using value of i
        
        // Run the current combination of threads and input size
        // NUM_TRIALS_PER_TEST times and take the average of all
        // of the runs.
        for(int k = 0; k < NUM_TRIALS_PER_TEST; k++) {
          // Dense matrix trial
          long dStartTime = System.nanoTime();
          doOperations(subDA, subDB);
          long dEndTime = System.nanoTime();
          dAvgTrial += (dEndTime - dStartTime);
          
          // Sparse matrix trial
          long sStartTime = System.nanoTime();
          doOperations(subSA, subSB);
          long sEndTime = System.nanoTime();
          sAvgTrial += (sEndTime - sStartTime);
        }
        // Current unit: nanoseconds
        denseTimes[i - 1][j - 1] = dAvgTrial / NUM_TRIALS_PER_TEST;
        sparseTimes[i - 1][j - 1] = sAvgTrial / NUM_TRIALS_PER_TEST;
      }
    }
    printTimes(denseTimes);
    // printTimes(sparseTimes);
//    outputTimes(denseTimes, PATH + "dense" + OUTPUT_FILENAME);
//    outputTimes(sparseTimes, PATH + "sparse" + OUTPUT_FILENAME);
  }

  public static double[][] readFile(String filename, int matrixSize) {
    double[][] a = new double[matrixSize][matrixSize];
    Scanner as = null;
    try {
      as = new Scanner(new File(PATH + "DenseB.txt"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    
    for(int i = 0; as.hasNextLine(); i++) {
      Scanner asl = new Scanner(as.nextLine());
      for(int j = 0; asl.hasNextDouble(); j++) {
        a[i][j] = asl.nextDouble();
      }
      asl.close();
    }
    as.close();
    return a;
  }
  
  /**
   * Executes a series of methods using the specified matrices.
   * 
   * @param inputSize
   * @param numThreads
   * @param A matrix whose functions are called with b as input
   * @param B matrix acted upon by a
   */
  public static void doOperations(Matrix a, Matrix b) {
    a.plus(b);
    a.minus(b);
    a.times(b);
    //a.solve(b);
  }
  
  /**
   * Takes a matrix of times and a filename and outputs the
   * matrix to a file.
   * @param times
   */
  public static void outputTimes(long[][] times, String filename) {
    PrintWriter out = null;
    try {
      out = new PrintWriter(filename);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    for(long[] row : times) {
      for(int i = 0; i < row.length; i++) {
        long ele = row[i];
        out.print(ele);
        if(i != row.length - 1) {
          out.print(OUTPUT_DELIM);
        }
      }
      out.println();
    }
  }
  // Temporary function to send time array to cout
  public static void printTimes(long[][] times) {
    System.out.print("numThreads" + OUTPUT_DELIM);
    for(int i = 0; i < times[0].length; i++) {
      int n = (int) Math.pow(2,i+1);
      System.out.print(n + "x" + n );
      if(i != times[0].length - 1) {
        System.out.print(OUTPUT_DELIM);
      }
    }
    System.out.println();
     
    for(int i = 0; i < times.length; i++) {
      long[] row = times[i];
      int numThreads = i + 1;
      System.out.print(numThreads + OUTPUT_DELIM);
      for(int j = 0; j < row.length; j++) {
        long ele = row[j];
        System.out.print(ele);
        if(j != row.length - 1) {
          System.out.print(OUTPUT_DELIM);
        }
      }
      System.out.println();
    }
  }
}


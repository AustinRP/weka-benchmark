//package parallel.benchmark;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import weka.core.matrix.Matrix;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVPrinter;


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
 * @author philipconrad
 *
 */
public class Benchmark {

  public static final String USAGE = "Usage:\n" +
    "\tjava Benchmark MATRIX_A MATRIX_B NUM_THREADS\n\n" +
    "Options:\n" +
    "  MATRIX_A    : Matrix A for use in the benchmarks.\n" +
    "  MATRIX_B    : Matrix B for use in the benchmarks.\n" +
    "  NUM_TRIALS  : Number of trials to do for each method.\n" +
    "  NUM_THREADS : Number of threads to test with.\n";

  // Default number of threads to test with is 4.
  public static int NUM_THREADS = 4;

  /**
   * Main function to run through all test methods and 
   * handle the data that will be output to CSV.
   */
  public static void main(String[] args) {
    if (args.length < 4) {
      // Print usage if not enough arguments provided.
      System.out.println(USAGE);
      return;
    } else {
      // Otherwise, go ahead, and read in matrices.
      double[][] a = readCSVFile(args[0]);
      double[][] b = readCSVFile(args[1]);
      int k        = Integer.parseInt(args[2]);
      NUM_THREADS  = Integer.parseInt(args[2]);

      Matrix ma    = new Matrix(a);
      Matrix mb    = new Matrix(b);

      // Benchmark each matrix method.
      for (MatrixMethod m : MatrixMethod.values()) {
        long timing = benchMatrix(ma, mb, m, k);
        System.out.printf("%s : %d\n", m, timing);
      }
    }
  }

  public enum MatrixMethod {
    M_TRANSPOSE,
    M_UMINUS,
    M_PLUS,
    M_PLUS_EQUALS,
    M_MINUS,
    M_MINUS_EQUALS,
    M_ARRAY_TIMES,
    M_ARRAY_TIMES_EQUALS,
    M_ARRAY_RIGHT_DIVIDE,
    M_ARRAY_RIGHT_DIVIDE_EQUALS,
    M_ARRAY_LEFT_DIVIDE,
    M_ARRAY_LEFT_DIVIDE_EQUALS,
    // Constant-times-matrix case.
    M_TIMES_D,
    M_TIMES_EQUALS_D,
    // Matrix-times-matrix case.
    M_TIMES_M
  }

  /**
   * Executes a series of methods using the specified matrices.
   * 
   * @param inputSize
   * @param numThreads
   * @param a Matrix whose functions are called with b as input
   * @param b Matrix acted upon by a
   * @param target Method to test.
   * @param k Number of trials to average over.
   * @return Average number of nanoseconds spent in the routine under test.
   */
  public static long benchMatrix(Matrix a, Matrix b, MatrixMethod target, int k) {
    long out = 0;
    switch (target) {
      case M_TRANSPOSE:
        out = bench(() -> { a.transpose(); }, k);
        break;
      case M_UMINUS:
        out = bench(() -> { a.uminus(); }, k);
        break;
      case M_PLUS:
        out = bench(() -> { a.plus(b); }, k);
        break;
      case M_PLUS_EQUALS:
        out = bench(() -> { a.plusEquals(b); }, k);
        break;
      case M_MINUS:
        out = bench(() -> { a.minus(b); }, k);
        break;
      case M_MINUS_EQUALS:
        out = bench(() -> { a.minusEquals(b); }, k);
        break;
      case M_ARRAY_TIMES:
        out = bench(() -> { a.arrayTimes(b); }, k);
        break;
      case M_ARRAY_TIMES_EQUALS:
        out = bench(() -> { a.arrayTimesEquals(b); }, k);
        break;
      case M_ARRAY_RIGHT_DIVIDE:
        out = bench(() -> { a.arrayRightDivide(b); }, k);
        break;
      case M_ARRAY_RIGHT_DIVIDE_EQUALS:
        out = bench(() -> { a.arrayRightDivideEquals(b); }, k);
        break;
      case M_ARRAY_LEFT_DIVIDE:
        out = bench(() -> { a.arrayLeftDivide(b); }, k);
        break;
      case M_ARRAY_LEFT_DIVIDE_EQUALS:
        out = bench(() -> { a.arrayLeftDivideEquals(b); }, k);
        break;
      case M_TIMES_D:
        out = bench(() -> { a.times(1.0); }, k);
        break;
      case M_TIMES_EQUALS_D:
        out = bench(() -> { a.timesEquals(1.0); }, k);
        break;
      case M_TIMES_M:
        out = bench(() -> { a.times(b); }, k);
        break;
      default:
        break;
    }

    return out;
  }

  /**
   * Perform basic timing on anything that implements the Runnable interface.
   * The most common use case for this is timing lambda functions.
   *
   * @param f The function to time.
   * @param num_trials Number of trials to run.
   * @return Arithmetic average of all trials.
   */
  public static long bench(Runnable f, int num_trials) {
    long timing = 0;
    for(int k = 0; k < num_trials; k++) {
      long startTime = System.nanoTime();
      f.run();
      long endTime = System.nanoTime();
      timing += (endTime - startTime);
    }
    return timing / num_trials;
  }

  /**
   * Reads in a matrix CSV file and returns a double[][].
   *
   * @param filename CSV file to read in.
   * @return A double[][] 2D array, useful for constructing Matrix instances.
   */
  public static double[][] readCSVFile(String filename) {
    FileReader fileReader   = null;
    CSVParser csvFileParser = null;
    CSVFormat csvFileFormat = CSVFormat.DEFAULT;
    Double data[][]         = null;
    double out[][]          = null;

    try {
      // Initialize everything and read in the CSV records.
      fileReader      = new FileReader(filename);
      csvFileParser   = new CSVParser(fileReader, csvFileFormat);
      List<CSVRecord> csvRecords = csvFileParser.getRecords();

      data = new Double [csvRecords.size()][];

      // We assume no header, and read in everything by rows.
      for (int i = 0; i < csvRecords.size(); i++) {
        CSVRecord record      = csvRecords.get(i);
        ArrayList<Double> row = new ArrayList<Double>();

        // Build an arraylist for each row
        for (int j = 0; j < record.size(); j++) {
          double item = Double.parseDouble(record.get(j));
          row.add(item);
        }
        data[i] = row.toArray(new Double[row.size()]);
      }

    } catch (Exception e) {
      System.out.println("Error during CSV file reading.");
      e.printStackTrace();
    } finally {
      try {
        fileReader.close();
        csvFileParser.close();
      } catch (IOException e) {
        System.out.println("Error while closing fileReader/csvFileParser.");
        e.printStackTrace();
      }
    }

    // Double[][] -> double[][] conversion by copying.
    out = new double[data.length][data[0].length];
    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < data[0].length; j++) {
        out[i][j] = data[i][j];
      }
    }

    return out;
  }
}


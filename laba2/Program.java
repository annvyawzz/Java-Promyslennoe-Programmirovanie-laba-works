import java.io.*;
import java.util.*;

public class Program
{
    private static int[][] matrix;

    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        try(FileInputStream fin=new FileInputStream("matrix.txt"))
        {
            List<int[]> matrixList = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
            String line;

            while((line = reader.readLine()) != null)
            {
                String[] numbers = line.trim().split("\\s+");
                int[] row = new int[numbers.length];

                for(int i = 0; i < numbers.length; i++)
                {
                    row[i] = Integer.parseInt(numbers[i]);
                }

                matrixList.add(row);
            }

            matrix = new int[matrixList.size()][];
            for(int i = 0; i < matrixList.size(); i++)
            {
                matrix[i] = matrixList.get(i);
            }


        }
        catch(IOException ex)
        {
            System.out.println(ex.getMessage());
            return;
        }

        System.out.println("\n-------------4---------------");
        int resultRow = Matrix.findRowWithMaxSumOfOddElements(matrix);

        if (resultRow != -1)
        {
            int maxSum = Matrix.calculateSumOfAbsoluteValues(matrix[resultRow]);
            System.out.println("Summa moduley elementov: " + maxSum);

        } else
        {
            System.out.println("Net strok, soderjashih tolko nechetnye elementy.");
        }


        System.out.println("\n-------------18--------------");
        int uniqueColumnsCount = Matrix.countColumnsWithUniqueElements(matrix);
        System.out.println("Количество столбцов с попарно различными числами: " + uniqueColumnsCount);

        System.out.println("\n-------------32--------------");
        System.out.println("До сортировки:");
        Matrix.showMatrix(matrix);

        Matrix.sortRowsByMaxElement(matrix);

        System.out.println("\nПосле сортировки:");
        Matrix.showMatrix(matrix);
    }
}


class Matrix
{
    private int[][] data;
    private int rows;
    private int cols;


    public Matrix(int[][] data)
    {
        this.data = data;
        this.rows = data.length;
        this.cols = (rows > 0) ? data[0].length : 0;
    }


    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int[][] getData() { return data; }
    public int[] getRow(int index) { return data[index]; }


        public static void sortRowsByMaxElement(int[][] matrix)
        {
            int rowCount = matrix.length;
            int[] maxElements = new int[rowCount];

            for (int i = 0; i < rowCount; i++) {
                int max = matrix[i][0];
                for (int j = 1; j < matrix[i].length; j++) {
                    if (matrix[i][j] > max) {
                        max = matrix[i][j];
                    }
                }
                maxElements[i] = max;
            }

            for (int i = 0; i < rowCount - 1; i++) {
                for (int j = 0; j < rowCount - 1 - i; j++) {
                    if (maxElements[j] > maxElements[j + 1]) {
                        int[] tempRow = matrix[j];
                        matrix[j] = matrix[j + 1];
                        matrix[j + 1] = tempRow;

                        int tempMax = maxElements[j];
                        maxElements[j] = maxElements[j + 1];
                        maxElements[j + 1] = tempMax;
                    }
                }
            }
        }

        public static void showMatrix(int[][] matrix)
        {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    System.out.print(matrix[i][j] + " ");
                }
                System.out.println();
            }
        }




    public static void printMatrix(int[][] matrix)
    {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public static boolean hasOnlyOddElements(int[] row)
    {
        for (int element : row)
        {
            if (element % 2 == 0)
            {
                return false;
            }
        }
        return true;
    }

    public static int calculateSumOfAbsoluteValues(int[] row)
    {
        int sum = 0;
        for (int element : row) {
            sum += Math.abs(element);
        }
        return sum;
    }

    public static int findRowWithMaxSumOfOddElements(int[][] matrix)
    {
        int maxSum = -1;
        int resultRow = -1;

        for (int i = 0; i < matrix.length; i++)
        {
            if (hasOnlyOddElements(matrix[i]))
            {
                int currentSum = calculateSumOfAbsoluteValues(matrix[i]);
                System.out.println("Stroka " + (i + 1) + ": vse elementy nechetnye, summa moduley = " + currentSum);

                if (currentSum > maxSum)
                {
                    maxSum = currentSum;
                    resultRow = i;
                }
            }
        }

        return resultRow;
    }

    public static int countColumnsWithUniqueElements(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return 0;
        }

        int count = 0;

        for (int j = 0; j < matrix[0].length; j++) {
            if (isColumnUnique(matrix, j)) {
                count++;
            }
        }

        return count;
    }

    private static boolean isColumnUnique(int[][] matrix, int colIndex)
    {
        Set<Integer> seen = new TreeSet<>();

        for (int i = 0; i < matrix.length; i++) {
            int element = matrix[i][colIndex];
            if (!seen.add(element)) {
                return false;
            }
        }

        return true;
    }

}

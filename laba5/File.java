import java.io.*;

public class File
{
    private String inputFileName;
    private String outputFileName;

    public File(String inputFileName, String outputFileName)
    {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
    }

    public String readFromFile() throws IOException
    {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }


    public void writeToFile(String text) throws IOException
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName)))
        {
            writer.write(text);
        }
    }

    public String getInputFileName()
    {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName)
    {
        this.inputFileName = inputFileName;
    }

    public String getOutputFileName()
    {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName)
    {
        this.outputFileName = outputFileName;
    }
}
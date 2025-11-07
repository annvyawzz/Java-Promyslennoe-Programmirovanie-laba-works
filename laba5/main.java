import java.util.*;
import java.io.*;

public class main
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Введите ширину строки: ");
            int width = scanner.nextInt();

            if (width <= 0)
            {
                System.out.println("Ширина должна быть положительным числом!");
                return;
            }

            File fileProcessor = new File("input.txt", "output.txt");
            Vyravnivanie justifier = new Vyravnivanie(width);

            String inputText = fileProcessor.readFromFile();
            String justifiedText = justifier.justifyText(inputText);
            fileProcessor.writeToFile(justifiedText);

            System.out.println("Текст успешно выровнен и сохранен в файл output.txt");

        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: Файл input.txt не найден!");
        } catch (IOException e) {
            System.out.println("Ошибка ввода-вывода: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Произошла ошибка: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
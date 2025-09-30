//4.В тексте слова заданной длины заменить указанной подстрокой,
// длина которой может не совпадать с длиной слова.

import java.io.*;
import java.util.StringTokenizer;

public class laba3
{
    public static void main(String[] args)
    {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        System.out.println("Введите текст (пустая строка - конец ввода):");

        try {
            MyString myString = new MyString();
            String text = myString.readText(br);

            if (text.isEmpty()) {
                System.out.println("Текст не был введен");
                return;
            }

            System.out.print(" длинa слов для замены: ");
            int targetLength = Integer.parseInt(br.readLine());

            System.out.print("подстрокa для замены: ");
            String replacement = br.readLine();

            String stringResult = myString.replaceWords(text, targetLength, replacement);
            System.out.println("\nРезультат обработки MyString:");
            System.out.println(stringResult);


        }
        catch (IOException e)
        {
            System.out.println("Ошибка чтения с клавиатуры");
        }
    }
}

class MyString
{

    public MyString() {}

    public MyString(String initialText) {

    }


    public String readText(BufferedReader br) throws IOException {
        String result = "";
        while (true) {
            String line = br.readLine();
            if (line == null || line.length() == 0)
            {
                break;
            }

            if (result.length() == 0) {
                result = result.concat(line);
            } else {
                result = result.concat("\n").concat(line);
            }
        }
        return result;
    }

    public String replaceWords(String text, int targetLength, String replacement) {
        String result = "";
        int startPos = 0;

        while (startPos < text.length()) {
            int endPos = text.indexOf('\n', startPos);
            if (endPos == -1) {
                endPos = text.length();
            }

            String line = text.substring(startPos, endPos);
            String processedLine = processLine(line, targetLength, replacement);

            if (result.length() == 0) {
                result = result.concat(processedLine);
            } else {
                result = result.concat("\n").concat(processedLine);
            }

            startPos = endPos + 1;
            if (startPos >= text.length()) break;
        }

        return result;
    }

    private String processLine(String line, int targetLength, String replacement) {
        String result = "";
        int pos = 0;

        while (pos < line.length()) {

            while (pos < line.length() && !Character.isLetter(line.charAt(pos))) {
                result = result.concat(String.valueOf(line.charAt(pos)));
                pos++;
            }

            if (pos >= line.length()) break;

            int wordStart = pos;
            while (pos < line.length() && Character.isLetter(line.charAt(pos))) {
                pos++;
            }

            String word = line.substring(wordStart, pos);
            if (word.length() == targetLength) {
                result = result.concat(replacement);
            } else {
                result = result.concat(word);
            }
        }

        return result;
    }
}


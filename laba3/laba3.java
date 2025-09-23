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
            // Создаем объект класса MyString и читаем текст
            MyString myString = new MyString();
            String text = myString.readText(br);

            if (text.isEmpty()) {
                System.out.println("Текст не был введен.");
                return;
            }

            System.out.print(" длинa слов для замены: ");
            int targetLength = Integer.parseInt(br.readLine());

            System.out.print("подстрокa для замены: ");
            String replacement = br.readLine();

            String stringResult = myString.replaceWords(text, targetLength, replacement);
            System.out.println("\nРезультат обработки MyString:");
            System.out.println(stringResult);

            MyStringBuffer myBuffer = new MyStringBuffer(text);
            String bufferResult = myBuffer.replaceWords(targetLength, replacement);
            System.out.println("\nРезультат обработки MyStringBuffer:");
            System.out.println(bufferResult);

            MyStringTokenizer myTokenizer = new MyStringTokenizer(text);
            String tokenResult = myTokenizer.replaceWords(targetLength, replacement);
            System.out.println("\nРезультат обработки MyStringTokenizer:");
            System.out.println(tokenResult);

        } catch (IOException e) {
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

class MyStringBuffer
{
    private StringBuffer buffer;

    public MyStringBuffer() {
        buffer = new StringBuffer();
    }

    public MyStringBuffer(String initialText) {
        buffer = new StringBuffer(initialText);
    }

    public MyStringBuffer(StringBuffer initialBuffer) {
        buffer = new StringBuffer(initialBuffer);
    }

    public String replaceWords(int targetLength, String replacement) {
        StringBuffer result = new StringBuffer();
        int pos = 0;

        while (pos < buffer.length()) {
            // Пропускаем не-буквы
            while (pos < buffer.length() && !Character.isLetter(buffer.charAt(pos))) {
                result.append(buffer.charAt(pos));
                pos++;
            }

            if (pos >= buffer.length()) break;

            int wordStart = pos;
            while (pos < buffer.length() && Character.isLetter(buffer.charAt(pos))) {
                pos++;
            }

            String word = buffer.substring(wordStart, pos);
            if (word.length() == targetLength) {
                result.append(replacement);
            } else {
                result.append(word);
            }
        }

        return result.toString();
    }

    public void appendText(String text) {
        buffer.append(text);
    }

    public int getCapacity() {
        return buffer.capacity();
    }

    public int getLength() {
        return buffer.length();
    }
}

class MyStringTokenizer
{
    private String text;

    public MyStringTokenizer() {
        text = "";
    }

    public MyStringTokenizer(String initialText) {
        text = initialText;
    }

    public String replaceWords(int targetLength, String replacement) {
        StringBuffer result = new StringBuffer();

        // Разбиваем на строки -  java.util.StringTokenizer
        StringTokenizer lineTokenizer = new StringTokenizer(text, "\n", true);

        while (lineTokenizer.hasMoreTokens()) {
            String token = lineTokenizer.nextToken();

            if (token.equals("\n")) {
                result.append(token);
                continue;
            }

            processLine(result, token, targetLength, replacement);
        }

        return result.toString();
    }

    private void processLine(StringBuffer result, String line, int targetLength, String replacement) {
        // Используем java.util.StringTokenizer
        StringTokenizer wordTokenizer = new StringTokenizer(line, " \t,.:;!?()-", true);

        while (wordTokenizer.hasMoreTokens()) {
            String token = wordTokenizer.nextToken();

            if (isWord(token)) {
                if (token.length() == targetLength) {
                    result.append(replacement);
                } else {
                    result.append(token);
                }
            } else {
                result.append(token);
            }
        }
    }

    private boolean isWord(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public int countWords() {
        int count = 0;
        StringTokenizer tokenizer = new StringTokenizer(text, " \t\n\r,.:;!?()-");
        while (tokenizer.hasMoreTokens()) {
            tokenizer.nextToken();
            count++;
        }
        return count;
    }
}
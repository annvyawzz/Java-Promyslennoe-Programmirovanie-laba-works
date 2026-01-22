import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextAnalyzer
{
    public static void main(String[] args)
    {
        String fileName = "input.txt";
        try {
            String content = Files.readString(Paths.get(fileName));

            Pattern wordPattern = Pattern.compile("[a-zA-Zа-яА-ЯёЁ-]+");
            Matcher wordMatcher = wordPattern.matcher(content);

            List<String> allWords = new ArrayList<>();
            while (wordMatcher.find()) {
                allWords.add(wordMatcher.group().toLowerCase());
            }

            Pattern sentencePattern = Pattern.compile("[.!?]+");
            Matcher sentenceMatcher = sentencePattern.matcher(content);
            long sentenceCount = sentenceMatcher.results().count();

            Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
            List<String> emails = emailPattern.matcher(content)
                    .results()
                    .map(java.util.regex.MatchResult::group)
                    .collect(Collectors.toList());

            int totalWords = allWords.size();
            long uniqueWordsCount = allWords.stream().distinct().count();

            Map<String, Integer> frequencyMap = new HashMap<>();
            for (String word : allWords)
            {
                frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
            }

            List<Map.Entry<String, Integer>> top10 = frequencyMap.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .collect(Collectors.toList());

            System.out.println("      ");
            System.out.println("Общее количество слов: " + totalWords);
            System.out.println("Количество уникальных слов: " + uniqueWordsCount);
            System.out.println("Количество предложений: " + sentenceCount);

            System.out.println("\nТоп-10 самых частых слов:");
            top10.forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

            System.out.println("\nНайденные email-адреса:");
            if (emails.isEmpty())
            {
                System.out.println("Адреса не найдены:(");
            } else {
                emails.forEach(System.out::println);
            }

        } catch (IOException e)
        {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }
}
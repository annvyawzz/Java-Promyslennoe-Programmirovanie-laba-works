import java.util.*;

public class Vyravnivanie
{
    private int lineWidth;

    public Vyravnivanie(int lineWidth)
    {
        this.lineWidth = Math.max(lineWidth, 1);
    }

    public String justifyText(String text)
    {
        String[] paragraphs = text.split("\n", -1);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < paragraphs.length; i++)
        {
            String paragraph = paragraphs[i];

            if (!paragraph.trim().isEmpty())
            {
                String justifiedParagraph = justifyParagraph(paragraph, i > 0);
                result.append(justifiedParagraph);
            }

            if (i < paragraphs.length - 1)
            {
                result.append("\n\n");
            }
        }
        return result.toString();
    }

    private String justifyParagraph(String paragraph, boolean isNotFirstParagraph)
    {
        List<String> words = splitIntoWords(paragraph);
        List<String> lines = new ArrayList<>();
        List<String> currentLine = new ArrayList<>();
        int currentVisualLength = 0; // длина с учетом всех символов
        boolean isFirstLineOfParagraph = true;

        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            int indent = (isFirstLineOfParagraph && isNotFirstParagraph) ? 4 : 0;

            //длина текущей строки: отступ + слова + пробелы между ними
            int spacesBetweenWords = currentLine.isEmpty() ? 0 : currentLine.size();
            int wordVisualLength = word.length(); //длина слова (все символы)
            int totalVisualLength = currentVisualLength + spacesBetweenWords + wordVisualLength + indent;

            if (totalVisualLength <= lineWidth)
            {
                //слово помещается целиком
                currentLine.add(word);
                currentVisualLength += wordVisualLength;
            } else {
                //пробуем разбить на слоги
                if (!currentLine.isEmpty() && Word.getCleanLength(word) > 3)
                {
                    int availableSpace = lineWidth - currentVisualLength - spacesBetweenWords - indent;

                    if (availableSpace >= 3) { //нужно место для минимум 2 букв + дефис
                        int breakPos = findSyllableBreak(word, availableSpace);
                        if (breakPos != -1) {
                            //разбиваем слово по слогу
                            String firstPart = word.substring(0, breakPos) + "-";
                            currentLine.add(firstPart);
                            lines.add(formatLine(currentLine, false, indent > 0));

                            //вторая часть становится новым словом
                            String secondPart = word.substring(breakPos);
                            words.add(i + 1, secondPart);

                            currentLine.clear();
                            currentVisualLength = 0;
                            isFirstLineOfParagraph = false;
                            continue;
                        }
                    }
                }

                //если не удалось разбить - завершаем текущую строку
                if (!currentLine.isEmpty()) {
                    lines.add(formatLine(currentLine, false, isFirstLineOfParagraph && isNotFirstParagraph));
                    currentLine.clear();
                    currentVisualLength = 0;
                    isFirstLineOfParagraph = false;
                }

                //пробуем поместить слово в новую строку
                int newLineIndent = (isFirstLineOfParagraph && isNotFirstParagraph) ? 4 : 0;
                int wordVisualLengthForNewLine = word.length();

                if (wordVisualLengthForNewLine <= lineWidth - newLineIndent) {
                    currentLine.add(word);
                    currentVisualLength = wordVisualLengthForNewLine;
                } else {
                    //слово слишком длинное - разбиваем на слоги
                    List<String> syllables = splitIntoSyllables(word, lineWidth - newLineIndent);
                    for (int j = 0; j < syllables.size(); j++) {
                        String syllable = syllables.get(j);
                        int syllableVisualLength = syllable.length();

                        if (syllableVisualLength <= lineWidth - newLineIndent) {
                            if (j == 0) {
                                currentLine.add(syllable);
                                currentVisualLength = syllableVisualLength;
                            } else {
                                lines.add(formatSingleLine(syllable, newLineIndent > 0));
                            }
                        } else {
                            //слог слишком длинный - разбиваем принудительно
                            List<String> forcedParts = forceSplitWord(syllable, lineWidth - newLineIndent);
                            for (String part : forcedParts)
                            {
                                lines.add(formatSingleLine(part, newLineIndent > 0));
                            }
                        }
                        newLineIndent = 0; //только первая строка абзаца имеет отступ
                    }
                    isFirstLineOfParagraph = false;
                }
            }
        }

        //последняя строка абзаца
        if (!currentLine.isEmpty()) {
            lines.add(formatLine(currentLine, true, isFirstLineOfParagraph && isNotFirstParagraph));
        }

        return String.join("\n", lines);
    }

    //граница слога для переноса (учитывает визуальную длину)
    private int findSyllableBreak(String word, int maxVisualLength)
    {
        //ищем с конца к началу
        for (int pos = Math.min(maxVisualLength + 2, word.length() - 2); pos >= 2; pos--) {
            //проверяем визуальную длину первой части + дефис
            String firstPart = word.substring(0, pos) + "-";
            if (firstPart.length() <= maxVisualLength && isGoodSyllableBreak(word, pos)) {
                return pos;
            }
        }
        return -1;
    }

    //проверяет, хорошая ли это граница слога (только лингвистические правила)
    private boolean isGoodSyllableBreak(String word, int pos)
    {
        //используем чистую длину для лингвистических проверок
        String firstPartClean = removePunctuation(word.substring(0, pos));
        String secondPartClean = removePunctuation(word.substring(pos));

        if (firstPartClean.length() < 2 || secondPartClean.length() < 2) {
            return false;
        }

        char before = word.charAt(pos - 1);
        char after = word.charAt(pos);

        //пропуск знаки препинания при лингвистическом анализе
        if (Word.isPunctuation(before) || Word.isPunctuation(after))
        {
            return false;
        }

        //ХОРОШО:перенос после гласной
        if (isVowel(before) && isConsonant(after)) {
            return true;
        }

        //ХОРОШО: перенос между согласными, если перед ними гласная
        if (isConsonant(before) && isConsonant(after) && pos >= 2) {
            char before2 = word.charAt(pos - 2);
            if (!Word.isPunctuation(before2) && isVowel(before2)) {
                return true;
            }
        }

        return false;
    }

    //разбивает слово на слоги с учетом максимальной длины
    private List<String> splitIntoSyllables(String word, int maxLength)
    {
        List<String> syllables = new ArrayList<>();
        String remaining = word;

        while (remaining.length() > 0) {
            if (remaining.length() <= maxLength) {
                syllables.add(remaining);
                break;
            }

            //ищем границу слога
            int breakPos = -1;
            for (int i = Math.min(maxLength, remaining.length() - 2); i >= 2; i--) {
                if (isGoodSyllableBreak(remaining, i)) {
                    breakPos = i;
                    break;
                }
            }

            if (breakPos == -1) {
                //не нашли хорошую границу - разбиваем по максимальной длине
                breakPos = Math.min(maxLength - 1, remaining.length() - 2);
                if (breakPos < 2) breakPos = Math.min(maxLength, remaining.length());
            }

            String syllable = remaining.substring(0, breakPos);
            if (breakPos < remaining.length()) {
                syllable += "-";
            }
            syllables.add(syllable);
            remaining = remaining.substring(breakPos);
        }

        return syllables;
    }

    //удаляет знаки препинания (только для лингвистического анализа)
    private String removePunctuation(String word) {
        StringBuilder clean = new StringBuilder();
        for (char c : word.toCharArray()) {
            if (!Word.isPunctuation(c)) {
                clean.append(c);
            }
        }
        return clean.toString();
    }

    //принудительное разбиение слишком длинного слова
    private List<String> forceSplitWord(String word, int maxLength)
    {
        List<String> parts = new ArrayList<>();
        String remaining = word;

        while (remaining.length() > maxLength)
        {
            //находим позицию разрыва
            int breakPos = Math.min(maxLength - 1, remaining.length() - 2);
            if (breakPos < 2) {
                breakPos = Math.min(maxLength, remaining.length());
            }

            String part = remaining.substring(0, breakPos);
            if (breakPos < remaining.length()) {
                part += "-";
            }
            parts.add(part);
            remaining = remaining.substring(breakPos);
        }

        if (!remaining.isEmpty()) {
            parts.add(remaining);
        }

        return parts;
    }

    private String formatSingleLine(String text, boolean hasIndent)
    {
        if (hasIndent) {
            return "    " + text;
        }
        return text;
    }

    private List<String> splitIntoWords(String text) {
        List<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
        boolean hasLeadingSpaces = true; // Флаг для начальных пробелов

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (Character.isWhitespace(c)) {
                if (currentWord.length() > 0) {
                    words.add(currentWord.toString());
                    currentWord.setLength(0);
                    hasLeadingSpaces = false; // После первого слова начальные пробелы закончились
                } else if (hasLeadingSpaces) {
                    // Сохраняем информацию о начальных пробелах как отдельный "пробельный токен"
                    if (words.isEmpty() || !words.get(words.size() - 1).equals(" ")) {
                        words.add(" "); // Добавляем пробел как отдельный элемент
                    }
                }
            } else if (Word.isPunctuation(c)) {
                // Знаки препинания добавляются к предыдущему слову
                if (currentWord.length() > 0) {
                    currentWord.append(c);
                    words.add(currentWord.toString());
                    currentWord.setLength(0);
                } else if (!words.isEmpty()) {
                    // Присоединяем к последнему слову
                    String lastWord = words.get(words.size() - 1);
                    words.set(words.size() - 1, lastWord + c);
                } else {
                    // Одиночный знак препинания в начале строки
                    words.add(String.valueOf(c));
                }
                hasLeadingSpaces = false;
            } else {
                currentWord.append(c);
                hasLeadingSpaces = false;
            }
        }

        if (currentWord.length() > 0) {
            words.add(currentWord.toString());
        }

        return words;
    }

    private String formatLine(List<String> words, boolean isLastLine, boolean hasIndent) {
        if (words.isEmpty()) {
            return "";
        }

        StringBuilder line = new StringBuilder();

        //отступ для первой строки абзаца
        if (hasIndent) {
            line.append("    ");
        }

        if (isLastLine || words.size() == 1 || lineWidth <= 15) {
            //для последней строки или узких строк - просто соединяем
            line.append(String.join(" ", words));
            return line.toString();
        }

        //считаем визуальную длину всех слов (все символы)
        int totalWordsLength = 0;
        for (String word : words) {
            totalWordsLength += word.length();
        }

        int availableWidth = lineWidth - (hasIndent ? 4 : 0);
        int totalSpacesNeeded = availableWidth - totalWordsLength;

        if (totalSpacesNeeded <= 0 || words.size() == 1)
        {
            //если не хватает места или только одно слово
            line.append(String.join(" ", words));
            return line.toString();
        }

        int baseSpaces = totalSpacesNeeded / (words.size() - 1);
        int extraSpaces = totalSpacesNeeded % (words.size() - 1);

        for (int i = 0; i < words.size(); i++) {
            line.append(words.get(i));
            if (i < words.size() - 1) {
                int spacesToAdd = baseSpaces + (i < extraSpaces ? 1 : 0);
                line.append(" ".repeat(spacesToAdd));
            }
        }

        return line.toString();
    }

    private boolean isVowel(char c) {
        return "аеёиоуыэюя".indexOf(Character.toLowerCase(c)) != -1;
    }

    private boolean isConsonant(char c) {
        return "бвгджзклмнпрстфхцчшщ".indexOf(Character.toLowerCase(c)) != -1;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = Math.max(lineWidth, 1);
    }
}
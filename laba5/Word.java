import java.util.*;

public class Word
{
    private static final String VOWELS = "аеёиоуыэюя";
    private static final String CONSONANTS = "бвгджзклмнпрстфхцчшщ";
    private static final String SPECIAL1 = "й";
    private static final String SPECIAL2 = "ьъ";
    private static final String PUNCTUATION = ".,!?;:-";

    public static List<Integer> getAllBreakPositions(String word)
    {
        List<Integer> positions = new ArrayList<>();
        String cleanWord = removePunctuation(word.toLowerCase());

        if (word.length() <= 4 /*|| cleanWord.length() <= 3*/) {
            return positions;
        }

        for (int i = 2; i <= cleanWord.length() - 2; i++)
        {
            if (isValidBreakPosition(cleanWord, i))
            {
                //коррект позиции с учетом знаков препинания
                int actualPos = adjustPositionForPunctuation(word, i);
                if (actualPos != -1)
                {
                    positions.add(actualPos);
                }
            }
        }

        return positions;
    }

    //удаляем знаки препинания для анализа
    private static String removePunctuation(String word)
    {
        StringBuilder clean = new StringBuilder();
        for (char c : word.toCharArray()) {
            if (PUNCTUATION.indexOf(c) == -1) {
                clean.append(c);
            }
        }
        return clean.toString();
    }

    //корректирует позицию разрыва с учетом знаков препинания
    private static int adjustPositionForPunctuation(String word, int cleanPos)
    {
        int actualPos = 0;
        int cleanCount = 0;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (PUNCTUATION.indexOf(c) == -1) {
                cleanCount++;
            }

            if (cleanCount == cleanPos)
            {
                //не разрываем перед знаком препинания
                if (i + 1 < word.length() && PUNCTUATION.indexOf(word.charAt(i + 1)) != -1) {
                    return -1; //нельзя разрывать здесь
                }
                return i + 1; //позиция после текущего символа
            }
        }
        return -1;
    }

    //проверка, является ли символ знаком препинания
    public static boolean isPunctuation(char c)
    {
        return PUNCTUATION.indexOf(c) != -1;
    }

    //"чистая" длина слова без знаков препинания
    public static int getCleanLength(String word)
    {
        return removePunctuation(word).length();
    }

    private static boolean isValidBreakPosition(String word, int pos)
    {
        //каждая часть должна быть минимум 2 буквы
        if (pos < 2 || (word.length() - pos) < 2) {
            return false;
        }

        //в каждой части есть хотя бы одна гласная
        String firstPart = word.substring(0, pos);
        String secondPart = word.substring(pos);

        if (!hasVowel(firstPart) || !hasVowel(secondPart)) {
            return false;
        }

        //получаем контекст вокруг позиции переноса
        char charBefore2 = (pos >= 2) ? word.charAt(pos - 2) : ' ';
        char charBefore = word.charAt(pos - 1);
        char charAfter = word.charAt(pos);
        char charAfter2 = (pos < word.length() - 1) ? word.charAt(pos + 1) : ' ';

        // Определяем типы символов
        boolean b2_isVowel = isVowel(charBefore2);
        boolean b2_isConsonant = isConsonant(charBefore2);

        boolean b_isVowel = isVowel(charBefore);
        boolean b_isConsonant = isConsonant(charBefore);
        boolean b_isSpecial1 = isSpecial1(charBefore);
        boolean b_isSpecial2 = isSpecial2(charBefore);

        boolean a_isVowel = isVowel(charAfter);
        boolean a_isConsonant = isConsonant(charAfter);
        boolean a_isSpecial1 = isSpecial1(charAfter);
        boolean a_isSpecial2 = isSpecial2(charAfter);

        //после разрыва есть полноценный слог (не только S, O1 или O2)
        boolean hasValidRemainingSyllable = hasValidSyllable(secondPart);

        //SG (Согласная + Гласная) - НЕЛЬЗЯ разрывать
        if (b_isConsonant && a_isVowel) {
            return false;
        }

        //GS (Гласная + Согласная) - МОЖНО разрывать
        if (b_isVowel && a_isConsonant && hasValidRemainingSyllable) {
            return true;
        }

        //SS (Согласная + Согласная) - МОЖНО разрывать, только если после есть гласная
        if (b_isConsonant && a_isConsonant && hasValidRemainingSyllable) {
            return true;
        }

        //SGG (Согласная + Гласная + Гласная) - НЕЛЬЗЯ разрывать между гласными
        if (b2_isConsonant && b_isVowel && a_isVowel) {
            return false;
        }

        //GGS (Гласная + Гласная + Согласная) - МОЖНО разрывать после второй гласной
        if (b2_isVowel && b_isVowel && a_isConsonant && hasValidRemainingSyllable) {
            return true;
        }

        //SGS (Согласная + Гласная + Согласная) - МОЖНО разрывать после гласной
        if (b2_isConsonant && b_isVowel && a_isConsonant && hasValidRemainingSyllable) {
            return true;
        }

        //Особые символы
        // G+O1 (Гласная + Й) - НЕЛЬЗЯ разрывать
        if (b_isVowel && a_isSpecial1)
        {
            return false;
        }

        // O1+G (Й + Гласная) - МОЖНО разрывать, только если после есть полноценный слог
        if (b_isSpecial1 && a_isVowel && hasValidRemainingSyllable) {
            return true;
        }

        // G+O2 (Гласная + Ь/Ъ) - МОЖНО разрывать, только если после есть полноценный слог
        if (b_isVowel && a_isSpecial2 && hasValidRemainingSyllable) {
            return true;
        }

        // O2+G (Ь/Ъ + Гласная) - МОЖНО разрывать, только если после есть полноценный слог
        if (b_isSpecial2 && a_isVowel && hasValidRemainingSyllable) {
            return true;
        }

        // O2+S (Ь/Ъ + Согласная) - МОЖНО разрывать, только если после есть полноценный слог
        if (b_isSpecial2 && a_isConsonant && hasValidRemainingSyllable) {
            return true;
        }

        return false;
    }

    //что в оставшейся части есть хотя бы одна гласная (основа слога)
    private static boolean hasValidSyllable(String text) {
        if (text.length() < 2) return false; // Минимум 2 буквы для слога

        //проверяка, есть ли в оставшейся части хотя бы одна гласная
        for (char c : text.toCharArray()) {
            if (isVowel(c)) {
                return true;
            }
        }
        return false;
    }

    public static int findBestBreakPosition(String word, int maxLength) {
        List<Integer> positions = getAllBreakPositions(word);

        if (positions.isEmpty()) {
            return -1;
        }

        // Предпочитаем позиции ближе к середине слова
        int bestPos = -1;
        int optimalLength = word.length() / 2;

        for (int pos : positions)
        {
            if (pos <= maxLength) {
                // Дополнительная проверка: обе части должны быть минимум 2 буквы
                if (pos >= 2 && (word.length() - pos) >= 2) {
                    // Ищем позицию, наиболее близкую к оптимальной
                    if (bestPos == -1 || Math.abs(pos - optimalLength) < Math.abs(bestPos - optimalLength)) {
                        bestPos = pos;
                    }
                }
            }
        }

        return bestPos;
    }

    public static boolean canSplit(String word) {
        String cleanWord = removePunctuation(word.toLowerCase());
        if (cleanWord.length() <= 3) {
            return false;
        }
        return !getAllBreakPositions(word).isEmpty();
    }

    private static boolean hasVowel(String text) {
        for (char c : text.toCharArray()) {
            if (isVowel(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isVowel(char c) {
        return VOWELS.indexOf(Character.toLowerCase(c)) != -1;
    }

    private static boolean isConsonant(char c) {
        return CONSONANTS.indexOf(Character.toLowerCase(c)) != -1;
    }

    private static boolean isSpecial1(char c) {
        return SPECIAL1.indexOf(Character.toLowerCase(c)) != -1;
    }

    private static boolean isSpecial2(char c) {
        return SPECIAL2.indexOf(Character.toLowerCase(c)) != -1;
    }
}
package com.psyche.potokisort;

public class ArrayUtils {
    public static int[] parseArray(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new int[0];
        }

        try {
            // Удаляем все лишние пробелы и разделяем по запятым ИЛИ пробелам
            String cleanedInput = input.trim().replaceAll("\\s+", " ");
            String[] parts = cleanedInput.split("[,\\s]+");

            // Проверяем, что есть хотя бы 2 числа
            if (parts.length < 2) {
                throw new IllegalArgumentException("Массив должен содержать минимум 2 числа");
            }

            int[] array = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                array[i] = Integer.parseInt(parts[i].trim());
            }
            return array;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректный формат чисел. Используйте только целые числа через запятую или пробел");
        }
    }
}
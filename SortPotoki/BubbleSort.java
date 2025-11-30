package com.psyche.potokisort;

public class BubbleSort implements SortAlgorithm {
    @Override
    public void sort(int[] array, VisualizationCallback callback) {
        int n = array.length;
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    swap(array, j, j + 1);
                    swapped = true;
                }
            }
            // Обновляем визуализацию только после каждого полного прохода
            callback.update(array, "Пузырьковая", "Проход " + (i + 1));

            if (!swapped) break; // Оптимизация: если не было обменов, массив отсортирован
        }
        callback.update(array, "Пузырьковая", "Завершено");
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
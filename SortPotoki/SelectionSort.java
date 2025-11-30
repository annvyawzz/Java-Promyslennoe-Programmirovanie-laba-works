package com.psyche.potokisort;

public class SelectionSort implements SortAlgorithm {
    @Override
    public void sort(int[] array, VisualizationCallback callback) {
        int n = array.length;

        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (array[j] < array[minIdx]) {
                    minIdx = j;
                }
            }
            swap(array, minIdx, i);
            // Обновляем визуализацию только после каждого выбора
            callback.update(array, "Выбором", "Шаг " + (i + 1));
        }
        callback.update(array, "Выбором", "Завершено");
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
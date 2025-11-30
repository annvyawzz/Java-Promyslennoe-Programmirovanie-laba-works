package com.psyche.potokisort;

import java.util.Random;

public class QuickSort implements SortAlgorithm {
    private Random random = new Random();
    private int updateCount = 0;

    @Override
    public void sort(int[] array, VisualizationCallback callback) {
        updateCount = 0;
        quickSort(array, 0, array.length - 1, callback);
        callback.update(array, "Быстрая", "Завершено");
    }

    private void quickSort(int[] array, int low, int high, VisualizationCallback callback) {
        if (low < high) {
            int pi = randomizedPartition(array, low, high, callback);
            quickSort(array, low, pi - 1, callback);
            quickSort(array, pi + 1, high, callback);

            // Обновляем только каждые 3 разделения
            updateCount++;
            if (updateCount % 3 == 0) {
                callback.update(array, "Быстрая", "Процесс...");
            }
        }
    }

    private int randomizedPartition(int[] array, int low, int high, VisualizationCallback callback) {
        int randomIndex = low + random.nextInt(high - low + 1);
        swap(array, randomIndex, high);
        return partition(array, low, high, callback);
    }

    private int partition(int[] array, int low, int high, VisualizationCallback callback) {
        int pivot = array[high];
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (array[j] <= pivot) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, high);
        return i + 1;
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
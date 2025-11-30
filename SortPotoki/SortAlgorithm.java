package com.psyche.potokisort;

public interface SortAlgorithm {
    void sort(int[] array, VisualizationCallback callback);

    interface VisualizationCallback {
        void update(int[] array, String algorithmName, String stepInfo);
    }


}
package com.psyche.potokisort;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.chart.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.animation.AnimationTimer;
import javafx.beans.property.*;
import java.util.Arrays;

public class SortingController {

    @FXML private TextField arrayInput;
    @FXML private Button startButton;
    @FXML private HBox chartArea;
    @FXML private Label statusLabel;

    private Service<Void>[] sortingServices;
    private StringProperty[] currentArrayProps;
    private BarChart<String, Number>[] barCharts;
    private LongProperty[] completionTimes;
    private Label[] timeLabels;

    @FXML
    public void initialize() {
        startButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        sortingServices = new Service[3];
        currentArrayProps = new StringProperty[3];
        barCharts = new BarChart[3];
        completionTimes = new LongProperty[3];
        timeLabels = new Label[3];

        for (int i = 0; i < 3; i++) {
            currentArrayProps[i] = new SimpleStringProperty("");
            completionTimes[i] = new SimpleLongProperty(0);
        }

        setupCharts();
    }

    private void setupCharts() {
        chartArea.getChildren().clear();
        String[] algorithmNames = {"Пузырьковая", "Быстрая", "Выбором"};
        String[] colors = {"#2196F3", "#4CAF50", "#FF5722"};

        for (int i = 0; i < 3; i++) {
            VBox chartContainer = new VBox(5);
            chartContainer.setStyle("-fx-border-color: #ccc; -fx-border-radius: 10; -fx-padding: 15; -fx-background-color: #f8f9fa;");
            chartContainer.setPrefWidth(350);

            HBox titleBox = new HBox(10);
            titleBox.setAlignment(Pos.CENTER_LEFT);

            Label titleLabel = new Label(algorithmNames[i]);
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + colors[i] + ";");

            timeLabels[i] = new Label("...");
            timeLabels[i].setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

            titleBox.getChildren().addAll(titleLabel, timeLabels[i]);

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();

            xAxis.setTickLabelsVisible(false);
            xAxis.setOpacity(0);
            yAxis.setTickLabelFont(javafx.scene.text.Font.font(12));

            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setLegendVisible(false);
            barChart.setPrefHeight(300);
            barChart.setAnimated(true);
            barChart.setStyle("-fx-background-color: transparent;");
            barChart.setCategoryGap(1);
            barChart.setBarGap(3);

            barCharts[i] = barChart;
            chartContainer.getChildren().addAll(titleBox, barChart);
            chartArea.getChildren().add(chartContainer);
        }
    }

    @FXML
    private void startSorting() {
        try {
            int[] array = ArrayUtils.parseArray(arrayInput.getText());

            if (array.length < 5) {
                showAlert("Ошибка: массив должен содержать минимум 5 чисел!");
                return;
            }

            startButton.setDisable(true);
            statusLabel.setText("Сортировка выполняется...");
            statusLabel.setStyle("-fx-text-fill: #FF9800;");

            clearResults();
            initializeCharts(array);
            startSortingServices(array);

        } catch (Exception e) {
            showAlert("Ошибка ввода: " + e.getMessage());
        }
    }

    private void clearResults() {
        for (int i = 0; i < 3; i++) {
            timeLabels[i].setText("...");
            completionTimes[i].set(0);
        }
    }

    private void initializeCharts(int[] array) {
        String[] colors = {"#2196F3", "#4CAF50", "#FF5722"};

        for (int i = 0; i < 3; i++) {
            BarChart<String, Number> chart = barCharts[i];
            chart.getData().clear();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (int j = 0; j < array.length; j++) {
                XYChart.Data<String, Number> data = new XYChart.Data<>(String.valueOf(j+1), array[j]);
                series.getData().add(data);
            }
            chart.getData().add(series);

            final String color = colors[i];

            for (XYChart.Data<String, Number> data : series.getData()) {
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle(
                                "-fx-bar-fill: " + color + ";" +
                                        "-fx-background-radius: 5;" +
                                        "-fx-border-color: " + darkenColor(color) + ";" +
                                        "-fx-border-width: 1;" +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);"
                        );
                        newNode.setScaleX(1.5);
                    }
                });
            }
        }
    }

    private String darkenColor(String color) {
        switch (color) {
            case "#2196F3": return "#1976D2";
            case "#4CAF50": return "#388E3C";
            case "#FF5722": return "#D84315";
            default: return color;
        }
    }

    private void startSortingServices(int[] array) {
        sortingServices[0] = createSortingService(array, new BubbleSort(), 0);
        sortingServices[1] = createSortingService(array, new QuickSort(), 1);
        sortingServices[2] = createSortingService(array, new SelectionSort(), 2);

        for (int i = 0; i < 3; i++) {
            sortingServices[i].restart();
        }

        final Service<Void>[] services = sortingServices;
        final Button button = startButton;

        AnimationTimer visualizationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateCharts();

                boolean allDone = true;
                for (Service<?> service : services) {
                    if (service != null && service.isRunning()) {
                        allDone = false;
                        break;
                    }
                }

                if (allDone) {
                    this.stop();
                    button.setDisable(false);
                    statusLabel.setText("Готово");
                    statusLabel.setStyle("-fx-text-fill: #4CAF50;");
                }
            }
        };
        visualizationTimer.start();
    }

    private Service<Void> createSortingService(int[] array, SortAlgorithm algorithm, int index) {
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new SortingTask(array, algorithm, index);
            }
        };

        service.setOnSucceeded(e -> {
            long time = completionTimes[index].get();
            timeLabels[index].setText(time + " мс");
        });

        return service;
    }

    private void updateCharts() {
        for (int i = 0; i < 3; i++) {
            String currentArray = currentArrayProps[i].get();
            if (!currentArray.isEmpty()) {
                updateChart(i, currentArray);
            }
        }
    }

    private void updateChart(int chartIndex, String arrayStr) {
        int[] array = ArrayUtils.parseArray(arrayStr.replace("[", "").replace("]", ""));
        BarChart<String, Number> chart = barCharts[chartIndex];

        if (chart.getData().isEmpty()) return;

        XYChart.Series<String, Number> series = chart.getData().get(0);
        for (int j = 0; j < array.length && j < series.getData().size(); j++) {
            XYChart.Data<String, Number> data = series.getData().get(j);
            data.setYValue(array[j]);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private class SortingTask extends Task<Void> {
        private final int[] array;
        private final SortAlgorithm algorithm;
        private final int index;

        public SortingTask(int[] array, SortAlgorithm algorithm, int index) {
            this.array = array;
            this.algorithm = algorithm;
            this.index = index;
        }

        @Override
        protected Void call() throws Exception {
            int[] arrCopy = array.clone();
            currentArrayProps[index].set(Arrays.toString(arrCopy));

            final int taskIndex = this.index;

            long startTime = System.currentTimeMillis();

            algorithm.sort(arrCopy, (arr, algo, step) -> {
                currentArrayProps[taskIndex].set(Arrays.toString(arr));
                try {
                    Thread.sleep(10); // Уменьшена задержка до 10мс
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            long endTime = System.currentTimeMillis();
            completionTimes[taskIndex].set(endTime - startTime);

            currentArrayProps[index].set(Arrays.toString(arrCopy));
            return null;
        }
    }
}
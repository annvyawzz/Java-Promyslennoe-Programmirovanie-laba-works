package com.psyche.xml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception
  {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/psyche/xml/view.fxml")
            );

            javafx.scene.Parent root = loader.load();

            primaryStage.setTitle("Библиотека - XML");
            primaryStage.setScene(new Scene(root, 900, 600));
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Ошибка запуска приложения: " + e.getMessage());
            e.printStackTrace();

            javafx.scene.control.Label label = new javafx.scene.control.Label(
                    "Ошибка загрузки интерфейса: " + e.getMessage() +
                            "\n\nУбедитесь, что view.fxml находится в:\n" +
                            "src/main/resources/com/psyche/xml/view.fxml"
            );
            javafx.scene.layout.StackPane stackPane = new javafx.scene.layout.StackPane(label);
            primaryStage.setScene(new Scene(stackPane, 500, 200));
            primaryStage.setTitle("Ошибка");
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        System.out.println("Запуск приложения Библиотека XML...");
        launch(args);
    }
}

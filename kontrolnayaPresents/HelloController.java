package com.psyche.nagrajdenie;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HelloController {
    @FXML
    private Label totalCostLabel;

    @FXML
    private TextArea orderDetailsTextArea;

    @FXML
    private RadioButton teacherRadio;

    @FXML
    private RadioButton directorRadio;

    @FXML
    private RadioButton mayorRadio;

    @FXML
    private CheckBox concertCheckBox;

    @FXML
    private CheckBox regularCustomerCheckBox;

    @FXML
    private ComboBox<String> giftComboBox;

    @FXML
    private ImageView backgroundImage;

    // Стоимости подарков для разных поздравителей
    private final double[] teacherGifts = {500, 800, 1200, 2000};
    private final double[] directorGifts = {1000, 1500, 2500, 4000};
    private final double[] mayorGifts = {2000, 3000, 5000, 8000};

    private final String[] giftNames = {"Книга", "Грамота в рамке", "Планшет", "Денежный приз"};
    private final double concertPrice = 5000;

    private ToggleGroup congratulatorGroup;

    @FXML
    public void initialize()
    {
        congratulatorGroup = new ToggleGroup();
        teacherRadio.setToggleGroup(congratulatorGroup);
        directorRadio.setToggleGroup(congratulatorGroup);
        mayorRadio.setToggleGroup(congratulatorGroup);
        teacherRadio.setSelected(true);

        updateGiftComboBox();

        congratulatorGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> updateCalculation());
        concertCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> updateCalculation());
        regularCustomerCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> updateCalculation());
        giftComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateCalculation());

        try {
            Image image = new Image("file:D:/фото/Picsart_25-11-16_09-32-36-529.jpg");
            backgroundImage.setImage(image);
            backgroundImage.setPreserveRatio(false);
        } catch (Exception e) {
            System.out.println("Не удалось загрузить фоновое изображение: " + e.getMessage());
        }

        updateCalculation();
    }

    private void updateCalculation() {
        double totalCost = 0;
        StringBuilder orderDetails = new StringBuilder("🎉 Состав заказа:\n\n");

        RadioButton selectedCongratulator = (RadioButton) congratulatorGroup.getSelectedToggle();
        if (selectedCongratulator != null) {
            String congratulator = selectedCongratulator.getText();
            orderDetails.append("🏆 Поздравитель: ").append(congratulator).append("\n");
        }

        int selectedGiftIndex = giftComboBox.getSelectionModel().getSelectedIndex();
        if (selectedGiftIndex >= 0) {
            double giftPrice = getGiftPrice(selectedGiftIndex);
            String giftName = giftNames[selectedGiftIndex];
            orderDetails.append("🎁 Подарок: ").append(giftName).append(" - ").append(giftPrice).append(" руб.\n");
            totalCost += giftPrice;
        }

        if (concertCheckBox.isSelected()) {
            orderDetails.append("🎵 Концерт: ").append(concertPrice).append(" руб.\n");
            totalCost += concertPrice;
        } else {
            orderDetails.append("🎵 Концерт: Нет\n");
        }

        boolean isRegularCustomer = regularCustomerCheckBox.isSelected();
        orderDetails.append("⭐ Постоянный клиент: ").append(isRegularCustomer ? "Да" : "Нет").append("\n");

        double discount = 0;
        if (isRegularCustomer) {
            discount = totalCost * 0.1;
            orderDetails.append("💝 Скидка: ").append(String.format("%.2f", discount)).append(" руб.\n");
            totalCost -= discount;
        }

        orderDetails.append("\n────────────────────────\n");
        orderDetails.append("💰 ИТОГО: ").append(String.format("%.2f", totalCost)).append(" руб.");

        // Обновляем интерфейс
        totalCostLabel.setText(String.format("💰 %.2f руб.", totalCost));
        orderDetailsTextArea.setText(orderDetails.toString());
    }

    private double getGiftPrice(int giftIndex) {
        RadioButton selectedCongratulator = (RadioButton) congratulatorGroup.getSelectedToggle();
        if (selectedCongratulator != null) {
            String congratulator = selectedCongratulator.getText();
            switch (congratulator) {
                case "Директор школы":
                    return directorGifts[giftIndex];
                case "Мэр города":
                    return mayorGifts[giftIndex];
                case "Учитель":
                default:
                    return teacherGifts[giftIndex];
            }
        }
        return teacherGifts[giftIndex];
    }

    @FXML
    private void onCongratulatorChanged() {
        updateGiftComboBox();
        updateCalculation();
    }

    private void updateGiftComboBox() {
        RadioButton selectedCongratulator = (RadioButton) congratulatorGroup.getSelectedToggle();
        if (selectedCongratulator != null) {
            String congratulator = selectedCongratulator.getText();
            double[] currentGifts;

            switch (congratulator) {
                case "Директор школы":
                    currentGifts = directorGifts;
                    break;
                case "Мэр города":
                    currentGifts = mayorGifts;
                    break;
                case "Учитель":
                default:
                    currentGifts = teacherGifts;
                    break;
            }

            ObservableList<String> gifts = FXCollections.observableArrayList(
                    giftNames[0] + " - " + currentGifts[0] + " руб.",
                    giftNames[1] + " - " + currentGifts[1] + " руб.",
                    giftNames[2] + " - " + currentGifts[2] + " руб.",
                    giftNames[3] + " - " + currentGifts[3] + " руб."
            );
            giftComboBox.setItems(gifts);
            giftComboBox.getSelectionModel().selectFirst();
        }
    }
}
package com.psyche.xml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.stream.Collectors;

public class LibraryController {
    @FXML private TableView<Book> tableView;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, Integer> colYear;
    @FXML private TableColumn<Book, Double> colPrice;
    @FXML private TableColumn<Book, String> colCategory;
    @FXML private TableColumn<Book, Integer> colCopies;
    @FXML private TableColumn<Book, Integer> colAvailable;

    @FXML private TextField tfTitle, tfAuthor, tfYear, tfPrice, tfCategory, tfCopies, tfSearch;
    @FXML private ComboBox<String> searchTypeCombo;

    private ObservableList<Book> bookList;

    @FXML
    public void initialize() {
        try {
            System.out.println("Инициализация контроллера...");

            // Настройка поиска
            searchTypeCombo.getItems().addAll("Автор", "Год", "Категория", "Название");
            searchTypeCombo.setValue("Автор");

            // Загрузка данных
            loadData();

            System.out.println("Контроллер успешно инициализирован");

        } catch (Exception e) {
            showAlert("Ошибка загрузки", "Не удалось загрузить данные: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadData() {
        System.out.println("Загрузка данных из XML...");
        bookList = FXCollections.observableArrayList(XMLHelper.loadBooks());
        System.out.println("Загружено книг: " + bookList.size());

        // Настройка таблицы
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCopies.setCellValueFactory(new PropertyValueFactory<>("copies"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));

        tableView.setItems(bookList);

        // Добавляем слушатель для выбора строки
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        System.out.println("Выбрана книга: " + newSelection.getTitle());
                    }
                }
        );
    }

    @FXML
    private void addBook() {
        try {
            System.out.println("Добавление новой книги...");

            // Проверка заполненности полей
            if (tfTitle.getText().isEmpty() || tfAuthor.getText().isEmpty() ||
                    tfYear.getText().isEmpty() || tfPrice.getText().isEmpty() ||
                    tfCategory.getText().isEmpty() || tfCopies.getText().isEmpty()) {
                showAlert("Ошибка", "Все поля должны быть заполнены!");
                return;
            }

            Book book = new Book();
            // Находим максимальный ID
            int maxId = bookList.stream()
                    .mapToInt(Book::getId)
                    .max()
                    .orElse(0);
            book.setId(maxId + 1);

            book.setTitle(tfTitle.getText());
            book.setAuthor(tfAuthor.getText());
            book.setYear(Integer.parseInt(tfYear.getText()));
            book.setPrice(Double.parseDouble(tfPrice.getText()));
            book.setCategory(tfCategory.getText());
            int copies = Integer.parseInt(tfCopies.getText());
            book.setCopies(copies);
            book.setAvailable(copies); // Все доступны изначально

            bookList.add(book);
            XMLHelper.saveBooks(bookList);
            clearFields();

            // Обновляем таблицу
            tableView.refresh();
            showAlert("Успех", "Книга '" + book.getTitle() + "' успешно добавлена!");

            System.out.println("Книга добавлена: " + book);

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Некорректные числовые данные в полях Год, Цена или Количество");
        } catch (Exception e) {
            showAlert("Ошибка", "Не удалось добавить книгу: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void searchBooks() {
        String query = tfSearch.getText().toLowerCase();
        String type = searchTypeCombo.getValue();

        if (query.isEmpty()) {
            tableView.setItems(bookList);
            return;
        }

        List<Book> filtered = bookList.stream().filter(book -> {
            try {
                switch (type) {
                    case "Автор":
                        return book.getAuthor().toLowerCase().contains(query);
                    case "Год":
                        return String.valueOf(book.getYear()).contains(query);
                    case "Категория":
                        return book.getCategory().toLowerCase().contains(query);
                    case "Название":
                        return book.getTitle().toLowerCase().contains(query);
                    default:
                        return true;
                }
            } catch (Exception e) {
                return false;
            }
        }).collect(Collectors.toList());

        tableView.setItems(FXCollections.observableArrayList(filtered));

        if (filtered.isEmpty()) {
            showAlert("Результаты поиска", "По запросу '" + query + "' ничего не найдено");
        } else {
            System.out.println("Найдено книг: " + filtered.size());
        }
    }

    @FXML
    private void showAllBooks() {
        System.out.println("Показать все книги...");
        tableView.setItems(bookList);
        tfSearch.clear();
    }

    @FXML
    private void updatePrice() {
        Book selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getPrice()));
            dialog.setTitle("Изменение цены");
            dialog.setHeaderText("Книга: " + selected.getTitle());
            dialog.setContentText("Введите новую цену:");

            dialog.showAndWait().ifPresent(newPrice -> {
                try {
                    double price = Double.parseDouble(newPrice);
                    if (price <= 0) {
                        showAlert("Ошибка", "Цена должна быть положительным числом");
                        return;
                    }
                    selected.setPrice(price);
                    tableView.refresh();
                    XMLHelper.saveBooks(bookList);
                    showAlert("Успех", "Цена книги обновлена!");
                    System.out.println("Цена обновлена для книги: " + selected.getTitle() + " = " + price);
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Введите корректное число для цены");
                } catch (Exception e) {
                    showAlert("Ошибка", "Не удалось сохранить изменения");
                }
            });
        } else {
            showAlert("Ошибка", "Выберите книгу из таблицы");
        }
    }

    @FXML
    private void borrowBook() {
        Book selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getAvailable() > 0) {
                selected.setAvailable(selected.getAvailable() - 1);
                tableView.refresh();
                try {
                    XMLHelper.saveBooks(bookList);
                    showAlert("Успех", "Книга '" + selected.getTitle() + "' выдана читателю.\n" +
                            "Осталось доступных: " + selected.getAvailable());
                    System.out.println("Книга выдана: " + selected.getTitle() +
                            ", доступно: " + selected.getAvailable());
                } catch (Exception e) {
                    showAlert("Ошибка", "Не удалось сохранить изменения");
                }
            } else {
                showAlert("Ошибка", "Нет доступных экземпляров этой книги");
            }
        } else {
            showAlert("Ошибка", "Выберите книгу из таблицы");
        }
    }

    @FXML
    private void returnBook() {
        Book selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getAvailable() < selected.getCopies()) {
                selected.setAvailable(selected.getAvailable() + 1);
                tableView.refresh();
                try {
                    XMLHelper.saveBooks(bookList);
                    showAlert("Успех", "Книга '" + selected.getTitle() + "' возвращена в библиотеку.\n" +
                            "Теперь доступно: " + selected.getAvailable());
                    System.out.println("Книга возвращена: " + selected.getTitle() +
                            ", доступно: " + selected.getAvailable());
                } catch (Exception e) {
                    showAlert("Ошибка", "Не удалось сохранить изменения");
                }
            } else {
                showAlert("Ошибка", "Все экземпляры уже в библиотеке");
            }
        } else {
            showAlert("Ошибка", "Выберите книгу из таблицы");
        }
    }

    private void clearFields() {
        tfTitle.clear();
        tfAuthor.clear();
        tfYear.clear();
        tfPrice.clear();
        tfCategory.clear();
        tfCopies.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
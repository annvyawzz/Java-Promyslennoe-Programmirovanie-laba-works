package com.example.demograph;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import java.util.Stack;
import java.util.EmptyStackException;
import javafx.scene.control.Label;

public class HelloController
{
        @FXML
        private TextArea inputTextArea;
        @FXML
        private TextArea outputTextArea;
        @FXML
        private TextField resultTextField;
        @FXML
        private Button convertButton;
        @FXML
        private Button calculateButton;
        @FXML
        private Button clearButton;
        @FXML
        private Label statusLabel;

        @FXML
        public void initialize()
        {
            inputTextArea.setPromptText("Введите математическое выражение");
            outputTextArea.setPromptText(" ");
            resultTextField.setPromptText("Результат вычисления");

            outputTextArea.setEditable(false);
            resultTextField.setEditable(false);
        }

        @FXML
        private void handleConvertButton()
        {
            try {
                String infixExpression = inputTextArea.getText().trim();

                if (infixExpression.isEmpty()) {
                    showAlert("Ошибка", "Введите математическое выражение!");
                    return;
                }

                String postfixExpression = infixToPostfix(infixExpression);
                outputTextArea.setText(postfixExpression);
                statusLabel.setText("Выражение преобразовано в ОПЗ");

            } catch (Exception e) {
                showAlert("Ошибка преобразования", e.getMessage());
                statusLabel.setText("Ошибка при преобразовании");
            }
        }

        @FXML
        private void handleCalculateButton()
        {
            try {
                String postfixExpression = outputTextArea.getText().trim();

                if (postfixExpression.isEmpty()) {
                    showAlert("Ошибка", "Сначала преобразуйте выражение в ОПЗ!");
                    return;
                }

                double result = evaluatePostfix(postfixExpression);
                resultTextField.setText(String.format("%.2f", result));
                statusLabel.setText("Вычисление завершено");

            } catch (Exception e)
            {
                showAlert("Ошибка вычисления", e.getMessage());
                statusLabel.setText("Ошибка при вычислении");
            }
        }

        @FXML
        private void handleClearButton()
        {
            inputTextArea.clear();
            outputTextArea.clear();
            resultTextField.clear();
            statusLabel.setText("Очищено");
        }

        @FXML
        private void handleKeyPressed(KeyEvent event)
        {
            if (event.getCode().toString().equals("ENTER")) {
                handleConvertButton();
            }
        }

        private String infixToPostfix(String infix) {
            StringBuilder postfix = new StringBuilder();
            Stack<Character> stack = new Stack<>();
            boolean expectingOperand = true;

            for (int i = 0; i < infix.length(); i++) {
                char c = infix.charAt(i);

                if (Character.isWhitespace(c)) {
                    continue;
                }

                if (Character.isDigit(c) || c == '.')
                {
                    StringBuilder number = new StringBuilder();
                    while (i < infix.length() &&
                            (Character.isDigit(infix.charAt(i)) || infix.charAt(i) == '.')) {
                        number.append(infix.charAt(i));
                        i++;
                    }
                    i--;

                    postfix.append(number).append(" ");
                    expectingOperand = false;

                } else if (c == '(') {
                    stack.push(c);
                    expectingOperand = true;

                } else if (c == ')') {
                    while (!stack.isEmpty() && stack.peek() != '(') {
                        postfix.append(stack.pop()).append(" ");
                    }
                    if (stack.isEmpty()) {
                        throw new IllegalArgumentException("Несбалансированные скобки");
                    }
                    stack.pop(); // Убираем '('
                    expectingOperand = false;

                } else if (isOperator(c)) {
                    if (expectingOperand) {
                        throw new IllegalArgumentException("Неправильное расположение оператора: " + c);
                    }

                    while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(c)) {
                        postfix.append(stack.pop()).append(" ");
                    }
                    stack.push(c);
                    expectingOperand = true;

                } else {
                    throw new IllegalArgumentException("Недопустимый символ: " + c);
                }
            }

            while (!stack.isEmpty()) {
                if (stack.peek() == '(') {
                    throw new IllegalArgumentException("Несбалансированные скобки");
                }
                postfix.append(stack.pop()).append(" ");
            }

            return postfix.toString().trim();
        }

        private double evaluatePostfix(String postfix)
        {
            Stack<Double> stack = new Stack<>();
            String[] tokens = postfix.split("\\s+");

            for (String token : tokens)
            {
                if (token.isEmpty()) continue;

                if (isNumber(token)) {
                    stack.push(Double.parseDouble(token));
                } else if (isOperator(token.charAt(0))) {
                    if (stack.size() < 2) {
                        throw new IllegalArgumentException("Недостаточно операндов для оператора: " + token);
                    }
                    double b = stack.pop();
                    double a = stack.pop();
                    double result = applyOperation(a, b, token.charAt(0));
                    stack.push(result);
                } else {
                    throw new IllegalArgumentException("Неизвестный токен: " + token);
                }
            }

            if (stack.size() != 1) {
                throw new IllegalArgumentException("Некорректное выражение");
            }

            return stack.pop();
        }

        private boolean isOperator(char c) {
            return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
        }

        private boolean isNumber(String str)
        {
            try {
                Double.parseDouble(str);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private int precedence(char op)
        {
            switch (op) {
                case '+':
                case '-':
                    return 1;
                case '*':
                case '/':
                    return 2;
                case '^':
                    return 3;
                default:
                    return 0;
            }
        }

        private double applyOperation(double a, double b, char op)
        {
            switch (op) {
                case '+':
                    return a + b;
                case '-':
                    return a - b;
                case '*':
                    return a * b;
                case '/':
                    if (b == 0) {
                        throw new ArithmeticException("Деление на ноль");
                    }
                    return a / b;
                case '^':
                    return Math.pow(a, b);
                default:
                    throw new IllegalArgumentException("Неизвестный оператор: " + op);
            }
        }

        private void showAlert(String title, String message)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

    }

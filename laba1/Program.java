import java.util.Scanner;
import java.text.NumberFormat;
import java.text.*;

public class Program {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println(" для функции e^x");
        System.out.print("Введите значение x для e^x: ");
        double xExp = scanner.nextDouble();
        System.out.print("Введите точность k для e^x: ");
        int kExp = scanner.nextInt();

        System.out.println(" для функции cos(x)");
        System.out.print("Введите значение x для cos(x): ");
        double xCos = scanner.nextDouble();
        System.out.print("Введите точность k для cos(x): ");
        int kCos = scanner.nextInt();

        Tailor tailor = new Tailor(xExp, kExp, xCos, kCos);

        System.out.println("\nРезультаты:");
        tailor.printResults();

        scanner.close();
    }
}

class Tailor {
    double xExp;
    int kExp;
    double exponExp;
    double sumExp;
    double terminExp;
    int nExp;
    double exactValueExp;

    double xCos;
    int kCos;
    double exponCos;
    double sumCos;
    double terminCos;
    int nCos;
    double exactValueCos;

    Tailor() {
        xExp = 0.5;
        kExp = 3;
        xCos = 0.5;
        kCos = 3;
        calculate();
    }

    Tailor(double xExp, int kExp, double xCos, int kCos) {
        this.xExp = xExp;
        this.kExp = kExp;
        this.xCos = xCos;
        this.kCos = kCos;
        calculate();
    }

    void calculate()
    {
        exponExp = 1.0;
        for (int i = 0; i < kExp; i++) {
            exponExp /= 10;
        }

        sumExp = 0.0;
        terminExp = 1.0;
        nExp = 0;
        while (Math.abs(terminExp) >= exponExp) {
            sumExp += terminExp;
            nExp++;
            terminExp = terminExp * xExp / nExp;
        }

        exactValueExp = Math.exp(xExp);


        exponCos = 1.0;
        for (int i = 0; i < kCos; i++) {
            exponCos /= 10;
        }

        sumCos = 1.0;
        terminCos = 1.0;
        nCos = 0;

        double xNormalized = xCos;
        while (xNormalized > 2 * Math.PI) {
            xNormalized -= 2 * Math.PI;
        }
        while (xNormalized < 0) {
            xNormalized += 2 * Math.PI;
        }

        int sign = -1;

        do {
            nCos += 2;
            terminCos = terminCos * xNormalized * xNormalized / ((nCos - 1) * nCos);
            sumCos += sign * terminCos;
            sign *= -1;
        } while (Math.abs(terminCos) >= exponCos);
       exactValueCos = Math.cos(xCos);
    }

    void printResults()
    {
        NumberFormat formatterExp = NumberFormat.getNumberInstance();
        formatterExp.setMaximumFractionDigits(kExp);

        NumberFormat formatterCos = NumberFormat.getNumberInstance();
        formatterCos.setMaximumFractionDigits(kCos);

        double actualErrorExp = Math.abs(exactValueExp - sumExp);
        double actualErrorCos = Math.abs(exactValueCos - sumCos);
        System.out.println("РЕЗУЛЬТАТЫ ВЫЧИСЛЕНИЙ");
        // Результаты для e^x
        System.out.println(" ФУНКЦИЯ e^x");
        System.out.printf("Параметры: x = %.3f, точность k = %d%n", xExp, kExp);
        System.out.println("Приближенное значение: " + formatterExp.format(sumExp));
        System.out.println("Точное значение (Math.exp()): " + formatterExp.format(exactValueExp));
        // Результаты для cos(x)
        System.out.println(" ФУНКЦИЯ cos(x) ");
        System.out.printf("Параметры: x = %.3f, точность k = %d%n", xCos, kCos);
        System.out.printf("Приближенное значение: "+ formatterCos.format(sumCos));
        System.out.printf("   Точное значение (Math.cos()):"+ formatterCos.format(exactValueCos));


    }
}
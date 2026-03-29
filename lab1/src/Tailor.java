import java.io.*;
public class Tailor
{
    public static void main(String[] args)
    {
        double x = 0.5;
        int k  = 3;
        double expon = 1.0;
        for (int i =0;i < k ;i++)
        {
            expon /= 10;
        }
        double sum = 0.0;
        double termin = 1.0;
        int n = 0;

        while(Math.abs(termin) >= expon | termin <= -expon)
        {
          sum += termin;
          n++;
          termin = termin * x / n ;
        }
        double exactValue = Math.exp(x);

        System.out.printf("x = %.1f,k = %d%n",x,k);
        System.out.printf("%.3f%n", sum);
        System.out.printf("Точное значение: %.3f%n", exactValue);
        System.out.printf("%d%n", n);
        System.out.printf("Погрешность: %.10f%n", expon);
    }
}


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Добро пожаловать в игру!");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите имя героя: ");
        String name = scanner.nextLine();

        System.out.println("Привет, " + name + "!");
    }
}

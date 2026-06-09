package br.com.echovita.console;

import java.util.Scanner;

public final class ConsoleInput {

    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return this.scanner.nextLine().trim();
    }

    public String readRequiredLine(String prompt) {
        while (true) {
            String value = this.readLine(prompt);
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("Valor obrigatório. Tente novamente.");
        }
    }

    public long readLong(String prompt) {
        while (true) {
            String value = this.readLine(prompt);
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                System.out.println("Número inválido. Tente novamente.");
            }
        }
    }

}

package br.com.echovita;

import br.com.echovita.application.EchoVitaLog;
import br.com.echovita.console.MenuConsole;
import br.com.echovita.gui.EchoVitaFrame;

import javax.swing.*;
import java.util.Scanner;

public final class Main {

    private enum ModoExecucao {
        GUI,
        CONSOLE
    }

    public static void main(String[] args) {
        EchoVitaLog.inicializar();
        Scanner scanner = new Scanner(System.in);
        ModoExecucao modo = perguntarModo(scanner);
        if (modo == ModoExecucao.GUI) {
            EchoVitaLog.acao("Modo de execução selecionado | Interface Gráfica");
            scanner.close();
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
                // Mantém o look-and-feel padrão se a troca falhar.
            }
            SwingUtilities.invokeLater(() -> new EchoVitaFrame().setVisible(true));
        } else {
            EchoVitaLog.acao("Modo de execução selecionado | Terminal");
            new MenuConsole(scanner).executar();
            scanner.close();
        }
    }

    private static ModoExecucao perguntarModo(Scanner scanner) {
        System.out.println("EchoVita — escolha o modo de execução");
        System.out.println("1 — Interface Gráfica");
        System.out.println("2 — Terminal");
        System.out.print("Escolha: ");

        while (scanner.hasNextLine()) {
            String opcao = scanner.nextLine().trim();
            if ("1".equals(opcao) || "gui".equalsIgnoreCase(opcao)) {
                return ModoExecucao.GUI;
            }
            if ("2".equals(opcao) || "console".equalsIgnoreCase(opcao) || "terminal".equalsIgnoreCase(opcao)) {
                return ModoExecucao.CONSOLE;
            }
            System.out.print("Opção inválida. Escolha 1 ou 2: ");
        }

        return ModoExecucao.CONSOLE;
    }

}

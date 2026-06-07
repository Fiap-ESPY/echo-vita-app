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
        ModoExecucao modo = perguntarModoTerminal();
        if (modo == ModoExecucao.GUI) {
            EchoVitaLog.acao("Modo de execução selecionado | Interface Gráfica");
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
                // Mantém o look-and-feel padrão se a troca falhar.
            }
            SwingUtilities.invokeLater(() -> new EchoVitaFrame().setVisible(true));
        } else {
            EchoVitaLog.acao("Modo de execução selecionado | Terminal");
            new MenuConsole().executar();
        }
    }

    private static ModoExecucao perguntarModoTerminal() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("EchoVita — escolha o modo de execução");
        System.out.println("1 — Interface Gráfica");
        System.out.println("2 — Terminal");
        System.out.print("Escolha: ");

        while (scanner.hasNextLine()) {
            String opcao = scanner.nextLine().trim();
            if ("1".equals(opcao) || "gui".equalsIgnoreCase(opcao)) {
                scanner.close();
                return ModoExecucao.GUI;
            }
            if ("2".equals(opcao) || "console".equalsIgnoreCase(opcao) || "terminal".equalsIgnoreCase(opcao)) {
                scanner.close();
                return ModoExecucao.CONSOLE;
            }
            System.out.print("Opção inválida. Escolha 1 ou 2: ");
        }

        scanner.close();
        return ModoExecucao.CONSOLE;
    }

}

package br.com.echovita.console;

import br.com.echovita.application.EchoVitaLog;
import br.com.echovita.application.EchoVitaSystem;

import java.util.Scanner;

public final class MenuConsole {

    private final ConsoleInput input;
    private final ConsoleMenuHandler handler;

    public MenuConsole(Scanner scanner) {
        this.input = new ConsoleInput(scanner);
        this.handler = new ConsoleMenuHandler(this.input, new EchoVitaSystem());
    }

    public void executar() {
        System.out.println("EchoVita — Monitoramento Acústico Preventivo");
        EchoVitaLog.acao("Terminal iniciado");
        boolean continuar = true;
        while (continuar) {
            this.exibirOpcoes();
            String opcao = this.input.readLine("Escolha: ");
            continuar = this.processar(opcao);
        }
    }

    private void exibirOpcoes() {
        System.out.println();
        System.out.println("1 — Exibir locais monitorados");
        System.out.println("2 — Exibir sensores de um local");
        System.out.println("3 — Executar monitoramento");
        System.out.println("4 — Exibir alertas de um local");
        System.out.println("5 — Notificar usuário com alertas");
        System.out.println("6 — Gerar relatório de um local");
        System.out.println("7 — Exibir perfil de usuário");
        System.out.println("8 — Configurar limiar de alerta de usuário");
        System.out.println("0 — Sair");
    }

    private boolean processar(String opcao) {
        EchoVitaLog.acao("Opção do menu selecionada | opcao=" + opcao);
        return switch (opcao) {
            case "1" -> {
                this.handler.exibirLocais();
                yield true;
            }
            case "2" -> {
                this.handler.exibirSensores();
                yield true;
            }
            case "3" -> {
                this.handler.executarMonitoramento();
                yield true;
            }
            case "4" -> {
                this.handler.exibirAlertas();
                yield true;
            }
            case "5" -> {
                this.handler.notificarUsuario();
                yield true;
            }
            case "6" -> {
                this.handler.gerarRelatorio();
                yield true;
            }
            case "7" -> {
                this.handler.exibirPerfilUsuario();
                yield true;
            }
            case "8" -> {
                this.handler.configurarLimiarAlerta();
                yield true;
            }
            case "0" -> {
                EchoVitaLog.acao("Encerrando aplicação");
                System.out.println("Encerrando EchoVita.");
                yield false;
            }
            default -> {
                EchoVitaLog.erro("Opção inválida | opcao=" + opcao);
                System.out.println("Opção inválida.");
                yield true;
            }
        };
    }

}

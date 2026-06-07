package br.com.echovita.console;

import br.com.echovita.application.EchoVitaLog;
import br.com.echovita.application.EchoVitaSystem;
import br.com.echovita.application.PreviaNotificacao;
import br.com.echovita.domain.alerta.Alerta;
import br.com.echovita.domain.enums.NivelCriticidade;
import br.com.echovita.domain.exception.EchoVitaException;
import br.com.echovita.domain.local.LocalMonitorado;
import br.com.echovita.domain.sensor.Sensor;
import br.com.echovita.domain.usuario.Usuario;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ConsoleMenuHandler {

    private final ConsoleInput input;
    private final EchoVitaSystem sistema;

    public ConsoleMenuHandler(ConsoleInput input, EchoVitaSystem sistema) {
        this.input = input;
        this.sistema = sistema;
    }

    public void exibirLocais() {
        EchoVitaLog.acao("Ação iniciada | Exibir locais monitorados");
        List<LocalMonitorado> locais = this.sistema.listarLocais();
        System.out.println("--- Locais Monitorados ---");
        for (int i = 0; i < locais.size(); i++) {
            System.out.println("[" + (i + 1) + "]");
            System.out.println(locais.get(i));
        }
        EchoVitaLog.acao("Ação concluída | Exibir locais monitorados | total=" + locais.size());
    }

    public void exibirSensores() {
        EchoVitaLog.acao("Ação iniciada | Exibir sensores");
        try {
            this.exibirListaLocais();
            int indice = (int) this.input.readLong("Selecione o local: ");
            LocalMonitorado local = this.sistema.buscarLocal(indice);
            List<Sensor> sensores = local.getSensores();

            System.out.println("--- Sensores de " + local.getNome() + " ---");
            if (sensores.isEmpty()) {
                System.out.println("Nenhum sensor cadastrado.");
                EchoVitaLog.acao("Ação concluída | Exibir sensores | local=" + local.getNome() + " | total=0");
                return;
            }

            for (Sensor sensor : sensores) {
                System.out.println(sensor);
                System.out.println();
            }
            EchoVitaLog.acao("Ação concluída | Exibir sensores | local=" + local.getNome()
                    + " | total=" + sensores.size());
        } catch (EchoVitaException e) {
            EchoVitaLog.erro("Falha ao exibir sensores | " + e.getMessage());
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void executarMonitoramento() {
        EchoVitaLog.acao("Ação iniciada | Executar monitoramento");
        try {
            this.exibirListaLocais();
            int indice = (int) this.input.readLong("Selecione o local: ");
            this.sistema.monitorar(indice);
            LocalMonitorado local = this.sistema.buscarLocal(indice);
            System.out.println("Monitoramento executado em " + local.getNome() + ".");
            System.out.println("Total de alertas: " + local.getAlertas().size());
        } catch (EchoVitaException e) {
            EchoVitaLog.erro("Falha no monitoramento | " + e.getMessage());
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void exibirAlertas() {
        EchoVitaLog.acao("Ação iniciada | Exibir alertas");
        try {
            this.exibirListaLocais();
            int indice = (int) this.input.readLong("Selecione o local: ");
            LocalMonitorado local = this.sistema.buscarLocal(indice);
            List<Alerta> alertas = local.getAlertas();

            System.out.println("--- Alertas de " + local.getNome() + " ---");
            if (alertas.isEmpty()) {
                System.out.println("Nenhum alerta registrado. Execute o monitoramento primeiro.");
                EchoVitaLog.acao("Ação concluída | Exibir alertas | local=" + local.getNome() + " | total=0");
                return;
            }

            for (Alerta alerta : alertas) {
                System.out.println(alerta);
                System.out.println();
            }
            EchoVitaLog.acao("Ação concluída | Exibir alertas | local=" + local.getNome()
                    + " | total=" + alertas.size());
        } catch (EchoVitaException e) {
            EchoVitaLog.erro("Falha ao exibir alertas | " + e.getMessage());
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void notificarUsuario() {
        EchoVitaLog.acao("Ação iniciada | Notificar usuário");
        try {
            this.exibirListaLocais();
            int indiceLocal = (int) this.input.readLong("Selecione o local: ");
            this.exibirListaUsuarios();
            int indiceUsuario = (int) this.input.readLong("Selecione o usuário: ");

            PreviaNotificacao previa = this.sistema.calcularPreviaNotificacao(indiceLocal, indiceUsuario);
            System.out.println();
            System.out.print(previa.formatarMensagemConfirmacao());

            String confirmacao = this.input.readLine("Confirmar? (s/n): ");
            if (!this.confirmou(confirmacao)) {
                System.out.println("Notificação cancelada.");
                return;
            }

            int total = this.sistema.notificarUsuario(indiceLocal, indiceUsuario);
            String resumo = previa.formatarResumoPosNotificacao(total);
            System.out.println();
            System.out.println(resumo);

            Usuario usuario = previa.getUsuario();
            this.ofereceWhatsApp(usuario, resumo);
            this.ofereceEmail(usuario, resumo);
        } catch (EchoVitaException e) {
            EchoVitaLog.erro("Falha na notificação | " + e.getMessage());
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void gerarRelatorio() {
        EchoVitaLog.acao("Ação iniciada | Gerar relatório");
        try {
            this.exibirListaLocais();
            int indiceLocal = (int) this.input.readLong("Selecione o local: ");
            System.out.println(this.sistema.gerarRelatorio(indiceLocal));

            String exportar = this.input.readLine("Exportar CSV? (s/n): ");
            if (this.confirmou(exportar)) {
                this.exportarCsv(indiceLocal);
            }
        } catch (EchoVitaException e) {
            EchoVitaLog.erro("Falha ao gerar relatório | " + e.getMessage());
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void exibirPerfilUsuario() {
        EchoVitaLog.acao("Ação iniciada | Exibir perfil de usuário");
        try {
            this.exibirListaUsuarios();
            int indice = (int) this.input.readLong("Selecione o usuário: ");
            Usuario usuario = this.sistema.buscarUsuario(indice);
            System.out.println(usuario.exibirPerfil());
            EchoVitaLog.acao("Ação concluída | Exibir perfil | usuario=" + usuario.getNome());
        } catch (EchoVitaException e) {
            EchoVitaLog.erro("Falha ao exibir perfil | " + e.getMessage());
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public void configurarLimiarAlerta() {
        EchoVitaLog.acao("Ação iniciada | Configurar limiar de alerta");
        try {
            this.exibirListaUsuarios();
            int indice = (int) this.input.readLong("Selecione o usuário: ");

            System.out.println("Nível de criticidade mínimo para receber alertas:");
            NivelCriticidade[] niveis = NivelCriticidade.values();
            for (int i = 0; i < niveis.length; i++) {
                System.out.println((i + 1) + " — " + niveis[i].formatarLimiar());
            }
            int limiar = (int) this.input.readLong("Escolha o nível: ");

            this.sistema.configurarLimiar(indice, limiar);
            Usuario usuario = this.sistema.buscarUsuario(indice);
            System.out.println("Limiar atualizado para " + usuario.getNome()
                    + ": " + usuario.getLimiarAlerta().formatarLimiar());
        } catch (EchoVitaException e) {
            EchoVitaLog.erro("Falha ao configurar limiar | " + e.getMessage());
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void exibirListaLocais() {
        List<LocalMonitorado> locais = this.sistema.listarLocais();
        for (int i = 0; i < locais.size(); i++) {
            LocalMonitorado local = locais.get(i);
            System.out.println((i + 1) + " — " + local.getNome() + " (" + local.getTipo() + ")");
        }
    }

    private void exibirListaUsuarios() {
        List<Usuario> usuarios = this.sistema.listarUsuarios();
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario usuario = usuarios.get(i);
            System.out.println((i + 1) + " — " + usuario.getNome() + " - " + usuario.getCargo());
        }
    }

    private boolean confirmou(String resposta) {
        return "s".equalsIgnoreCase(resposta != null ? resposta.trim() : "");
    }

    private void ofereceWhatsApp(Usuario usuario, String mensagem) {
        String telefone = usuario.getTelefone();
        if (telefone == null || telefone.isBlank()) {
            return;
        }

        String resposta = this.input.readLine("Enviar alerta por WhatsApp para " + telefone + "? (s/n): ");
        if (!this.confirmou(resposta)) {
            return;
        }

        try {
            String telefoneNormalizado = telefone.replaceAll("\\D", "");
            String textoCodificado = URLEncoder.encode(mensagem, StandardCharsets.UTF_8);
            URI uri = new URI("https://wa.me/" + telefoneNormalizado + "?text=" + textoCodificado);

            if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                System.out.println("Erro: não foi possível abrir o WhatsApp neste sistema.");
                return;
            }

            Desktop.getDesktop().browse(uri);
            System.out.println("WhatsApp aberto com a mensagem preparada.");
        } catch (Exception ex) {
            EchoVitaLog.erro("Falha ao abrir WhatsApp | " + ex.getMessage());
            System.out.println("Erro ao abrir WhatsApp: " + ex.getMessage());
        }
    }

    private void ofereceEmail(Usuario usuario, String mensagem) {
        String email = usuario.getEmail();
        if (email == null || email.isBlank()) {
            return;
        }

        String resposta = this.input.readLine("Enviar alerta por e-mail para " + email + "? (s/n): ");
        if (!this.confirmou(resposta)) {
            return;
        }

        try {
            String assunto = "Alerta EchoVita";
            String assuntoCodificado = URLEncoder.encode(assunto, StandardCharsets.UTF_8);
            String corpoCodificado = URLEncoder.encode(mensagem, StandardCharsets.UTF_8);
            URI uri = new URI("mailto:" + email + "?subject=" + assuntoCodificado + "&body=" + corpoCodificado);

            if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
                System.out.println("Erro: não foi possível abrir o cliente de e-mail neste sistema.");
                return;
            }

            Desktop.getDesktop().mail(uri);
            System.out.println("Cliente de e-mail aberto com a mensagem preparada.");
        } catch (Exception ex) {
            EchoVitaLog.erro("Falha ao abrir e-mail | " + ex.getMessage());
            System.out.println("Erro ao abrir e-mail: " + ex.getMessage());
        }
    }

    private void exportarCsv(int indiceLocal) {
        try {
            String caminho = this.input.readRequiredLine("Caminho do arquivo (ex: relatorio_echovita.csv): ");
            if (!caminho.toLowerCase().endsWith(".csv")) {
                caminho = caminho + ".csv";
            }

            String conteudoCsv = this.sistema.gerarRelatorioCsv(indiceLocal);
            Path arquivo = Path.of(caminho);
            Files.writeString(arquivo, conteudoCsv, StandardCharsets.UTF_8);
            System.out.println("CSV salvo em: " + arquivo.toAbsolutePath());
        } catch (EchoVitaException e) {
            EchoVitaLog.erro("Falha ao gerar CSV | " + e.getMessage());
            System.out.println("Erro: " + e.getMessage());
        } catch (IOException e) {
            EchoVitaLog.erro("Falha ao salvar CSV | " + e.getMessage());
            System.out.println("Erro ao salvar CSV: " + e.getMessage());
        }
    }

}

package br.com.echovita.gui;

import br.com.echovita.application.EchoVitaLog;
import br.com.echovita.application.EchoVitaSystem;
import br.com.echovita.application.PreviaNotificacao;
import br.com.echovita.domain.alerta.Alerta;
import br.com.echovita.domain.enums.NivelCriticidade;
import br.com.echovita.domain.exception.EchoVitaException;
import br.com.echovita.domain.local.LocalMonitorado;
import br.com.echovita.domain.usuario.Usuario;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public final class EchoVitaFrame extends JFrame {

    private enum CanalEnvio {
        NENHUM,
        WHATSAPP,
        EMAIL
    }

    private static final Color COR_FUNDO = new Color(234, 244, 251);
    private static final Color COR_PRIMARIA = new Color(208, 80, 122);
    private static final Color COR_AZUL = new Color(100, 181, 246);
    private static final Color COR_MARINHO = new Color(26, 35, 126);
    private static final Color COR_BRANCO = Color.WHITE;
    private static final int LARGURA_JANELA = 760;
    private static final int ALTURA_JANELA = 590;
    private static final int MARGEM = 15;
    private static final int COL_DIREITA_X = 375;
    private static final int IMAGEM_LARGURA = 370;
    private static final int IMAGEM_LOCAL_ALTURA = 250;
    private static final int IMAGEM_USUARIO_ALTURA = 250;

    private final EchoVitaSystem sistema = new EchoVitaSystem();

    private final JComboBox<String> comboLocais = new JComboBox<>();
    private final JComboBox<String> comboUsuarios = new JComboBox<>();
    private final JComboBox<String> comboLimiar = new JComboBox<>();
    private final JLabel lblLimiarInfo = new JLabel();
    private final JTextArea txtResultado = new JTextArea();
    private final JLabel lblImagemLocal = new JLabel();
    private final JLabel lblImagemUsuario = new JLabel();
    private final List<LocalMonitorado> locaisExibidos = new ArrayList<>();

    public EchoVitaFrame() {
        this.configurarJanela();
        this.criarComponentes();
        this.preencherCombos();
        this.aplicarTema();
        EchoVitaLog.acao("Interface gráfica iniciada");
    }

    private void configurarJanela() {
        this.setTitle("EchoVita — Monitoramento Acústico Preventivo");
        this.setSize(LARGURA_JANELA, ALTURA_JANELA);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    private void criarComponentes() {
        JLabel lblLocal = new JLabel("Local:");
        lblLocal.setBounds(MARGEM, 15, 70, 22);
        this.add(lblLocal);

        this.comboLocais.setBounds(85, 15, 240, 22);
        this.comboLocais.addActionListener(e -> this.atualizarImagemLocal());
        this.add(this.comboLocais);

        JLabel lblUsuario = new JLabel("Usuário:");
        lblUsuario.setBounds(MARGEM, 45, 70, 22);
        this.add(lblUsuario);

        this.comboUsuarios.setBounds(85, 45, 240, 22);
        this.comboUsuarios.addActionListener(e -> {
            this.atualizarImagemUsuario();
            this.atualizarComboLocais();
            this.sincronizarLimiarDoUsuario();
        });
        this.add(this.comboUsuarios);

        JLabel lblLimiar = new JLabel("Limiar:");
        lblLimiar.setBounds(MARGEM, 75, 70, 22);
        this.add(lblLimiar);

        this.comboLimiar.setBounds(85, 75, 240, 22);
        this.comboLimiar.addActionListener(e -> {
            this.atualizarInfoLimiar();
            this.configurarLimiar();
        });
        this.add(this.comboLimiar);

        this.lblLimiarInfo.setBounds(85, 99, 290, 22);
        this.add(this.lblLimiarInfo);

        JButton btnMonitorar = new JButton("Monitorar");
        btnMonitorar.setBounds(MARGEM, 130, 140, 28);
        this.add(btnMonitorar);
        btnMonitorar.addActionListener(e -> this.executarMonitoramento());

        JButton btnExibirAlertas = new JButton("Exibir Alertas");
        btnExibirAlertas.setBounds(165, 130, 140, 28);
        this.add(btnExibirAlertas);
        btnExibirAlertas.addActionListener(e -> this.exibirAlertas());

        JButton btnNotificar = new JButton("Notificar");
        btnNotificar.setBounds(MARGEM, 165, 140, 28);
        this.add(btnNotificar);
        btnNotificar.addActionListener(e -> this.notificarUsuario());

        JButton btnRelatorio = new JButton("Relatório");
        btnRelatorio.setBounds(165, 165, 140, 28);
        this.add(btnRelatorio);
        btnRelatorio.addActionListener(e -> this.gerarRelatorio());

        JButton btnExibirPerfil = new JButton("Exibir Perfil");
        btnExibirPerfil.setBounds(MARGEM, 200, 290, 28);
        this.add(btnExibirPerfil);
        btnExibirPerfil.addActionListener(e -> this.exibirPerfil());

        JLabel lblResultado = new JLabel("Resultado:");
        lblResultado.setBounds(MARGEM, 240, 100, 22);
        this.add(lblResultado);

        this.txtResultado.setEditable(false);
        this.txtResultado.setLineWrap(true);
        this.txtResultado.setWrapStyleWord(true);
        this.txtResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollResultado = new JScrollPane(this.txtResultado);
        scrollResultado.setBounds(MARGEM, 260, 330, 315);
        this.add(scrollResultado);

        JLabel lblLocalImagem = new JLabel("Local monitorado:");
        lblLocalImagem.setBounds(COL_DIREITA_X, 15, 180, 22);
        this.add(lblLocalImagem);

        this.lblImagemLocal.setBounds(COL_DIREITA_X, 40, IMAGEM_LARGURA, IMAGEM_LOCAL_ALTURA);
        this.lblImagemLocal.setHorizontalAlignment(JLabel.CENTER);
        this.lblImagemLocal.setVerticalAlignment(JLabel.CENTER);
        this.add(this.lblImagemLocal);

        JLabel lblUsuarioImagem = new JLabel("Usuário:");
        lblUsuarioImagem.setBounds(COL_DIREITA_X, 300, 180, 22);
        this.add(lblUsuarioImagem);

        this.lblImagemUsuario.setBounds(COL_DIREITA_X, 325, IMAGEM_LARGURA, IMAGEM_USUARIO_ALTURA);
        this.lblImagemUsuario.setHorizontalAlignment(JLabel.CENTER);
        this.lblImagemUsuario.setVerticalAlignment(JLabel.CENTER);
        this.add(this.lblImagemUsuario);
    }

    private void aplicarTema() {
        this.getContentPane().setBackground(COR_FUNDO);

        for (Component componente : this.getContentPane().getComponents()) {
            if (componente instanceof JLabel label) {
                label.setForeground(COR_MARINHO);
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            } else if (componente instanceof JButton botao) {
                botao.setBackground(COR_PRIMARIA);
                botao.setForeground(COR_BRANCO);
                botao.setFocusPainted(false);
                botao.setOpaque(true);
                botao.setBorderPainted(false);
            } else if (componente instanceof JTextField campo) {
                campo.setBackground(COR_BRANCO);
                campo.setForeground(COR_MARINHO);
                campo.setOpaque(true);
            } else if (componente instanceof JComboBox<?> combo) {
                combo.setBackground(COR_BRANCO);
                combo.setForeground(COR_MARINHO);
                combo.setOpaque(true);
            } else if (componente instanceof JScrollPane scroll) {
                scroll.getViewport().setBackground(COR_BRANCO);
                scroll.setBorder(null);
            }
        }

        this.lblImagemLocal.setBackground(COR_AZUL);
        this.lblImagemLocal.setOpaque(true);
        this.lblImagemUsuario.setBackground(COR_AZUL);
        this.lblImagemUsuario.setOpaque(true);
        this.lblLimiarInfo.setForeground(COR_MARINHO);
        this.lblLimiarInfo.setFont(this.lblLimiarInfo.getFont().deriveFont(Font.PLAIN));
        this.txtResultado.setBackground(COR_BRANCO);
        this.txtResultado.setForeground(COR_MARINHO);
    }

    private void preencherCombos() {
        for (NivelCriticidade nivel : NivelCriticidade.values()) {
            this.comboLimiar.addItem(nivel.getDescricao());
        }

        for (Usuario usuario : this.sistema.listarUsuarios()) {
            this.comboUsuarios.addItem(usuario.getNome() + " - " + usuario.getCargo());
        }

        this.atualizarComboLocais();
        this.atualizarImagemUsuario();
        this.sincronizarLimiarDoUsuario();
    }

    private void atualizarComboLocais() {
        this.comboLocais.removeAllItems();
        this.locaisExibidos.clear();

        if (this.comboUsuarios.getSelectedIndex() < 0) {
            return;
        }

        try {
            Usuario usuario = this.sistema.buscarUsuario(this.indiceUsuarioSelecionado());
            for (LocalMonitorado local : usuario.getLocaisVinculados()) {
                this.comboLocais.addItem(local.getNome() + " (" + local.getTipo() + ")");
                this.locaisExibidos.add(local);
            }

            if (!this.locaisExibidos.isEmpty()) {
                this.atualizarImagemLocal();
            } else {
                this.exibirImagemIndisponivel(this.lblImagemLocal);
            }
        } catch (EchoVitaException ex) {
            this.exibirErro(ex.getMessage());
        }
    }

    private void atualizarInfoLimiar() {
        int indice = this.comboLimiar.getSelectedIndex();
        if (indice < 0) {
            this.lblLimiarInfo.setText("");
            return;
        }
        this.lblLimiarInfo.setText(NivelCriticidade.values()[indice].getComportamento());
    }

    private void sincronizarLimiarDoUsuario() {
        if (this.comboUsuarios.getSelectedIndex() < 0) {
            return;
        }

        try {
            Usuario usuario = this.sistema.buscarUsuario(this.indiceUsuarioSelecionado());
            int indiceLimiar = usuario.getLimiarAlerta().ordinal();

            if (this.comboLimiar.getSelectedIndex() == indiceLimiar) {
                this.atualizarInfoLimiar();
                this.configurarLimiar();
                return;
            }

            this.comboLimiar.setSelectedIndex(indiceLimiar);
        } catch (EchoVitaException ex) {
            this.exibirErro(ex.getMessage());
        }
    }

    private void atualizarImagemLocal() {
        try {
            int indice = this.indiceLocalSelecionado();
            LocalMonitorado local = this.sistema.buscarLocal(indice);
            this.exibirImagem(this.lblImagemLocal, local.getImagemUrl(), IMAGEM_LARGURA, IMAGEM_LOCAL_ALTURA);
        } catch (EchoVitaException ex) {
            this.exibirImagemIndisponivel(this.lblImagemLocal);
        }
    }

    private void atualizarImagemUsuario() {
        try {
            int indice = this.indiceUsuarioSelecionado();
            Usuario usuario = this.sistema.buscarUsuario(indice);
            this.exibirImagem(this.lblImagemUsuario, usuario.getImagemUrl(), IMAGEM_LARGURA, IMAGEM_USUARIO_ALTURA);
        } catch (EchoVitaException ex) {
            this.exibirImagemIndisponivel(this.lblImagemUsuario);
        }
    }

    private void exibirImagem(JLabel label, String imagemUrl, int largura, int altura) {
        ImageIcon icone = this.carregarImagem(imagemUrl, largura, altura);

        if (icone == null) {
            this.exibirImagemIndisponivel(label);
            return;
        }

        label.setIcon(icone);
        label.setText(null);
    }

    private void exibirImagemIndisponivel(JLabel label) {
        label.setIcon(null);
        label.setText("Imagem indisponível");
    }

    private ImageIcon carregarImagem(String imagemUrl, int largura, int altura) {
        if (imagemUrl == null || imagemUrl.isBlank()) {
            return null;
        }

        try (InputStream stream = EchoVitaFrame.class.getResourceAsStream(imagemUrl)) {
            if (stream == null) {
                return null;
            }

            Image imagem = ImageIO.read(stream);
            if (imagem == null) {
                return null;
            }

            Image imagemRedimensionada = imagem.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
            return new ImageIcon(imagemRedimensionada);
        } catch (IOException ex) {
            return null;
        }
    }

    private int indiceLocalSelecionado() {
        int posicao = this.comboLocais.getSelectedIndex();
        if (posicao < 0 || posicao >= this.locaisExibidos.size()) {
            throw new EchoVitaException("Selecione um local vinculado ao usuário.");
        }
        return this.sistema.buscarIndiceLocal(this.locaisExibidos.get(posicao));
    }

    private int indiceUsuarioSelecionado() {
        return this.comboUsuarios.getSelectedIndex() + 1;
    }

    private int limiarSelecionado() {
        return this.comboLimiar.getSelectedIndex() + 1;
    }

    private void exibirResultado(String conteudo) {
        this.txtResultado.setText(conteudo);
        this.txtResultado.setCaretPosition(0);
    }

    private void executarMonitoramento() {
        try {
            int indice = this.indiceLocalSelecionado();
            this.sistema.monitorar(indice);
            LocalMonitorado local = this.sistema.buscarLocal(indice);
            this.exibirResultado(
                    "Monitoramento executado em " + local.getNome() + ".\n"
                            + "Total de alertas: " + local.getAlertas().size());
        } catch (EchoVitaException ex) {
            this.exibirErro(ex.getMessage());
        }
    }

    private void exibirAlertas() {
        try {
            int indice = this.indiceLocalSelecionado();
            LocalMonitorado local = this.sistema.buscarLocal(indice);
            List<Alerta> alertas = local.getAlertas();

            if (alertas.isEmpty()) {
                this.exibirResultado("Nenhum alerta registrado. Execute o monitoramento primeiro.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("--- Alertas de ").append(local.getNome()).append(" ---\n\n");
            for (Alerta alerta : alertas) {
                sb.append(alerta).append("\n\n");
            }
            this.exibirResultado(sb.toString());
        } catch (EchoVitaException ex) {
            this.exibirErro(ex.getMessage());
        }
    }

    private void notificarUsuario() {
        try {
            int indiceLocal = this.indiceLocalSelecionado();
            int indiceUsuario = this.indiceUsuarioSelecionado();
            PreviaNotificacao previa = this.sistema.calcularPreviaNotificacao(indiceLocal, indiceUsuario);

            CanalEnvio canal = this.exibirDialogoNotificacao(previa);
            if (canal == null) {
                return;
            }

            int total = this.sistema.notificarUsuario(indiceLocal, indiceUsuario);
            String resumo = previa.formatarResumoPosNotificacao(total);
            this.exibirResultado(resumo);

            Usuario usuario = previa.getUsuario();
            if (canal == CanalEnvio.WHATSAPP) {
                this.enviarWhatsApp(usuario, resumo);
            } else if (canal == CanalEnvio.EMAIL) {
                this.enviarEmail(usuario, "Alerta EchoVita", resumo);
            }
        } catch (EchoVitaException ex) {
            this.exibirErro(ex.getMessage());
        }
    }

    private CanalEnvio exibirDialogoNotificacao(PreviaNotificacao previa) {
        Usuario usuario = previa.getUsuario();

        JTextArea txtPrevia = new JTextArea(previa.formatarMensagemConfirmacao());
        txtPrevia.setEditable(false);
        txtPrevia.setLineWrap(true);
        txtPrevia.setWrapStyleWord(true);
        txtPrevia.setBackground(COR_BRANCO);
        txtPrevia.setForeground(COR_MARINHO);
        txtPrevia.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtPrevia.setBorder(null);

        JComboBox<String> comboCanal = new JComboBox<>();
        comboCanal.addItem("Somente no sistema");

        boolean temWhatsApp = usuario.getTelefone() != null && !usuario.getTelefone().isBlank();
        boolean temEmail = usuario.getEmail() != null && !usuario.getEmail().isBlank();

        if (temWhatsApp) {
            comboCanal.addItem("WhatsApp — " + usuario.getTelefone());
        }
        if (temEmail) {
            comboCanal.addItem("E-mail — " + usuario.getEmail());
        }

        JPanel painelCanal = new JPanel(new BorderLayout(8, 0));
        JLabel lblCanal = new JLabel("Canal externo:");
        lblCanal.setForeground(COR_MARINHO);
        painelCanal.add(lblCanal, BorderLayout.WEST);
        painelCanal.add(comboCanal, BorderLayout.CENTER);

        JPanel painel = new JPanel(new BorderLayout(0, 12));
        painel.add(new JScrollPane(txtPrevia), BorderLayout.CENTER);
        painel.add(painelCanal, BorderLayout.SOUTH);

        int opcao = JOptionPane.showConfirmDialog(
                this,
                painel,
                "Confirmar notificação",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcao != JOptionPane.OK_OPTION) {
            return null;
        }

        String selecionado = (String) comboCanal.getSelectedItem();
        if (selecionado != null && selecionado.startsWith("WhatsApp")) {
            return CanalEnvio.WHATSAPP;
        }
        if (selecionado != null && selecionado.startsWith("E-mail")) {
            return CanalEnvio.EMAIL;
        }
        return CanalEnvio.NENHUM;
    }

    private void enviarWhatsApp(Usuario usuario, String mensagem) {
        String telefone = usuario.getTelefone();
        if (telefone == null || telefone.isBlank()) {
            return;
        }

        try {
            String telefoneNormalizado = telefone.replaceAll("\\D", "");
            String textoCodificado = URLEncoder.encode(mensagem, StandardCharsets.UTF_8);
            URI uri = new URI("https://wa.me/" + telefoneNormalizado + "?text=" + textoCodificado);

            if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                this.exibirErro("Não foi possível abrir o WhatsApp neste sistema.");
                return;
            }

            Desktop.getDesktop().browse(uri);
        } catch (Exception ex) {
            this.exibirErro("Erro ao abrir WhatsApp: " + ex.getMessage());
        }
    }

    private void enviarEmail(Usuario usuario, String assunto, String corpo) {
        String email = usuario.getEmail();
        if (email == null || email.isBlank()) {
            return;
        }

        try {
            String assuntoCodificado = URLEncoder.encode(assunto, StandardCharsets.UTF_8);
            String corpoCodificado = URLEncoder.encode(corpo, StandardCharsets.UTF_8);
            URI uri = new URI("mailto:" + email + "?subject=" + assuntoCodificado + "&body=" + corpoCodificado);

            if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
                this.exibirErro("Não foi possível abrir o cliente de e-mail neste sistema.");
                return;
            }

            Desktop.getDesktop().mail(uri);
        } catch (Exception ex) {
            this.exibirErro("Erro ao abrir e-mail: " + ex.getMessage());
        }
    }

    private void gerarRelatorio() {
        try {
            int indiceLocal = this.indiceLocalSelecionado();
            this.exibirResultado(this.sistema.gerarRelatorio(indiceLocal));
            this.exportarCsv(indiceLocal);
        } catch (EchoVitaException ex) {
            this.exibirErro(ex.getMessage());
        }
    }

    private void exportarCsv(int indiceLocal) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar relatório CSV");
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivo CSV (*.csv)", "csv"));
        chooser.setSelectedFile(new File("relatorio_echovita.csv"));

        int opcao = chooser.showSaveDialog(this);
        if (opcao != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File arquivo = chooser.getSelectedFile();
        if (!arquivo.getName().toLowerCase().endsWith(".csv")) {
            arquivo = new File(arquivo.getAbsolutePath() + ".csv");
        }

        try {
            String conteudoCsv = this.sistema.gerarRelatorioCsv(indiceLocal);
            Files.writeString(arquivo.toPath(), conteudoCsv, StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(
                    this,
                    "CSV salvo em:\n" + arquivo.getAbsolutePath(),
                    "Exportação concluída",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            this.exibirErro("Erro ao salvar CSV: " + ex.getMessage());
        } catch (EchoVitaException ex) {
            this.exibirErro(ex.getMessage());
        }
    }

    private void configurarLimiar() {
        if (this.comboUsuarios.getSelectedIndex() < 0 || this.comboLimiar.getSelectedIndex() < 0) {
            return;
        }

        try {
            int indiceUsuario = this.indiceUsuarioSelecionado();
            int limiar = this.limiarSelecionado();
            this.sistema.configurarLimiar(indiceUsuario, limiar);
        } catch (EchoVitaException ex) {
            this.exibirErro(ex.getMessage());
        }
    }

    private void exibirPerfil() {
        try {
            int indice = this.indiceUsuarioSelecionado();
            Usuario usuario = this.sistema.buscarUsuario(indice);
            this.exibirResultado(usuario.exibirPerfil());
        } catch (EchoVitaException ex) {
            this.exibirErro(ex.getMessage());
        }
    }

    private void exibirErro(String mensagem) {
        EchoVitaLog.erro("Erro na interface | " + mensagem);
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

}

package br.com.echovita.application;

import br.com.echovita.domain.alerta.Alerta;
import br.com.echovita.domain.enums.NivelCriticidade;
import br.com.echovita.domain.exception.EchoVitaException;
import br.com.echovita.domain.interfaces.GeradorRelatorio;
import br.com.echovita.domain.local.Fazenda;
import br.com.echovita.domain.local.LocalMonitorado;
import br.com.echovita.domain.local.UnidadeSaude;
import br.com.echovita.domain.sensor.SensorComportamental;
import br.com.echovita.domain.sensor.SensorRespiratorio;
import br.com.echovita.domain.usuario.GestorOperacional;
import br.com.echovita.domain.usuario.Medico;
import br.com.echovita.domain.usuario.ProprietarioRural;
import br.com.echovita.domain.usuario.Usuario;
import br.com.echovita.domain.usuario.Veterinario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EchoVitaSystem {

    private final List<LocalMonitorado> locais = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();

    public EchoVitaSystem() {
        this.inicializarAmbienteDemo();
    }

    private void inicializarAmbienteDemo() {
        Fazenda fazenda = new Fazenda("Fazenda Boa Vista", "João Silva", 120);
        fazenda.setImagemUrl("/br/com/echovita/resources/images/fazenda.png");
        fazenda.adicionarSensor(new SensorRespiratorio("SR-001", "Curral A", 7));
        fazenda.adicionarSensor(new SensorComportamental("SC-001", "Pasto Norte", 15.0));

        UnidadeSaude unidade = new UnidadeSaude("UBS Centro", "Dra. Maria", 80);
        unidade.setImagemUrl("/br/com/echovita/resources/images/unidade_saude.png");
        unidade.adicionarSensor(new SensorRespiratorio("SR-002", "Sala de Espera", 8));

        this.locais.add(fazenda);
        this.locais.add(unidade);

        Veterinario veterinario = new Veterinario("Carlos Mendes", "RM12345", "CRMV-SP 98765", "Bovinos");
        veterinario.setImagemUrl("/br/com/echovita/resources/images/usuarios/carlos_mendes.png");
        veterinario.setEmail("carlos.mendes@echovita.com");
        veterinario.setTelefone("5511999903847");

        Medico medico = new Medico("Ana Paula", "RM54321", "CRM-SP 11223", "Clínica Geral");
        medico.setImagemUrl("/br/com/echovita/resources/images/usuarios/ana_paula.png");
        medico.setEmail("ana.paula@echovita.com");
        medico.setTelefone("5511999907261");

        ProprietarioRural proprietario = new ProprietarioRural("Roberto Costa", "RM99001", "Fazenda Boa Vista", "Sul de MG");
        proprietario.setImagemUrl("/br/com/echovita/resources/images/usuarios/roberto_costa.png");
        proprietario.setEmail("roberto.costa@echovita.com");
        proprietario.setTelefone("5535999905183");

        GestorOperacional gestor = new GestorOperacional("Fernanda Lima", "RM88002", "Monitoramento Rural");
        gestor.setImagemUrl("/br/com/echovita/resources/images/usuarios/fernanda_lima.png");
        gestor.setEmail("fernanda.lima@echovita.com");
        gestor.setTelefone("5511999909426");

        veterinario.vincularLocal(fazenda);
        medico.vincularLocal(unidade);
        proprietario.vincularLocal(fazenda);
        gestor.vincularLocal(fazenda);
        gestor.vincularLocal(unidade);

        this.usuarios.add(veterinario);
        this.usuarios.add(medico);
        this.usuarios.add(proprietario);
        this.usuarios.add(gestor);

        EchoVitaLog.acao("Ambiente demo inicializado | locais=" + this.locais.size()
                + " | usuarios=" + this.usuarios.size());
    }

    public List<LocalMonitorado> listarLocais() {
        return List.copyOf(this.locais);
    }

    public List<Usuario> listarUsuarios() {
        return List.copyOf(this.usuarios);
    }

    public LocalMonitorado buscarLocal(int indice) {
        int posicao = indice - 1;
        if (posicao < 0 || posicao >= this.locais.size()) {
            EchoVitaLog.erro("Local inválido | indice=" + indice);
            throw new EchoVitaException("Local inválido.");
        }
        return this.locais.get(posicao);
    }

    public Usuario buscarUsuario(int indice) {
        int posicao = indice - 1;
        if (posicao < 0 || posicao >= this.usuarios.size()) {
            EchoVitaLog.erro("Usuário inválido | indice=" + indice);
            throw new EchoVitaException("Usuário inválido.");
        }
        return this.usuarios.get(posicao);
    }

    public void monitorar(int indiceLocal) {
        LocalMonitorado local = this.buscarLocal(indiceLocal);
        int alertasAntes = local.getAlertas().size();
        local.monitorar();
        int alertasDepois = local.getAlertas().size();
        EchoVitaLog.acao("Monitoramento executado | local=" + local.getNome()
                + " | alertasGerados=" + (alertasDepois - alertasAntes)
                + " | totalAlertas=" + alertasDepois);
    }

    public void configurarLimiar(int indiceUsuario, int limiar) {
        Usuario usuario = this.buscarUsuario(indiceUsuario);
        usuario.configurarLimiarAlerta(limiar);
        EchoVitaLog.acao("Limiar configurado | usuario=" + usuario.getNome()
                + " | limiar=" + usuario.getLimiarAlerta().formatarLimiar());
    }

    public PreviaNotificacao calcularPreviaNotificacao(int indiceLocal, int indiceUsuario) {
        LocalMonitorado local = this.buscarLocal(indiceLocal);
        List<Alerta> alertas = local.getAlertas();

        if (alertas.isEmpty()) {
            EchoVitaLog.erro("Prévia bloqueada | local=" + local.getNome()
                    + " | motivo=sem alertas");
            throw new EchoVitaException("Nenhum alerta disponível. Execute o monitoramento primeiro.");
        }

        Usuario usuario = this.buscarUsuario(indiceUsuario);

        if (!usuario.isVinculadoAo(local)) {
            EchoVitaLog.erro("Prévia bloqueada | usuario=" + usuario.getNome()
                    + " | local=" + local.getNome()
                    + " | motivo=sem vinculo");
            throw new EchoVitaException(
                    "Usuário " + usuario.getNome() + " não está vinculado ao local " + local.getNome() + ".");
        }

        Map<NivelCriticidade, Long> aceitos = PreviaNotificacao.criarMapaContagem();
        Map<NivelCriticidade, Long> ignorados = PreviaNotificacao.criarMapaContagem();
        int limiar = usuario.getLimiarAlerta().ordinal();

        for (Alerta alerta : alertas) {
            NivelCriticidade nivel = alerta.getNivelCriticidade();
            Map<NivelCriticidade, Long> destino = nivel.ordinal() >= limiar ? aceitos : ignorados;
            destino.merge(nivel, 1L, Long::sum);
        }

        EchoVitaLog.acao("Prévia de notificação | local=" + local.getNome()
                + " | usuario=" + usuario.getNome()
                + " | aceitos=" + aceitos.values().stream().mapToLong(Long::longValue).sum()
                + " | ignorados=" + ignorados.values().stream().mapToLong(Long::longValue).sum());

        return new PreviaNotificacao(usuario, alertas.size(), aceitos, ignorados);
    }

    public int notificarUsuario(int indiceLocal, int indiceUsuario) {
        LocalMonitorado local = this.buscarLocal(indiceLocal);
        List<Alerta> alertas = local.getAlertas();

        if (alertas.isEmpty()) {
            EchoVitaLog.erro("Notificação bloqueada | local=" + local.getNome()
                    + " | motivo=sem alertas");
            throw new EchoVitaException("Nenhum alerta disponível. Execute o monitoramento primeiro.");
        }

        Usuario usuario = this.buscarUsuario(indiceUsuario);

        if (!usuario.isVinculadoAo(local)) {
            EchoVitaLog.erro("Notificação bloqueada | usuario=" + usuario.getNome()
                    + " | local=" + local.getNome()
                    + " | motivo=sem vinculo");
            throw new EchoVitaException(
                    "Usuário " + usuario.getNome() + " não está vinculado ao local " + local.getNome() + ".");
        }

        int notificacoesAntes = usuario.getNotificacoesRecebidas().size();
        for (Alerta alerta : alertas) {
            usuario.notificar(alerta);
        }
        int total = usuario.getNotificacoesRecebidas().size();
        int recebidas = total - notificacoesAntes;

        EchoVitaLog.acao("Notificação enviada | local=" + local.getNome()
                + " | usuario=" + usuario.getNome()
                + " | alertasProcessados=" + alertas.size()
                + " | notificacoesRecebidas=" + recebidas
                + " | totalAcumulado=" + total);
        return total;
    }

    public int buscarIndiceLocal(LocalMonitorado local) {
        for (int i = 0; i < this.locais.size(); i++) {
            if (this.locais.get(i) == local) {
                return i + 1;
            }
        }
        EchoVitaLog.erro("Local inválido | nome=" + local.getNome());
        throw new EchoVitaException("Local inválido.");
    }

    public String gerarRelatorio(int indiceLocal) {
        LocalMonitorado local = this.buscarLocal(indiceLocal);

        if (!(local instanceof GeradorRelatorio gerador)) {
            EchoVitaLog.erro("Relatório indisponível | local=" + local.getNome());
            throw new EchoVitaException("Este local não gera relatório.");
        }

        EchoVitaLog.acao("Relatório gerado | local=" + local.getNome()
                + " | alertas=" + local.getAlertas().size());
        return gerador.gerarRelatorio();
    }

    public String gerarRelatorioCsv(int indiceLocal) {
        LocalMonitorado local = this.buscarLocal(indiceLocal);

        if (!(local instanceof GeradorRelatorio)) {
            EchoVitaLog.erro("CSV indisponível | local=" + local.getNome());
            throw new EchoVitaException("Este local não gera relatório.");
        }

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Sensor,Local,Tipo de Anomalia,Nível de Criticidade,Data/Hora\n");

        java.time.format.DateTimeFormatter fmt =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        for (Alerta alerta : local.getAlertas()) {
            csv.append(alerta.getId()).append(",")
                    .append(escaparCsv(alerta.getSensorId())).append(",")
                    .append(escaparCsv(alerta.getLocalizacao())).append(",")
                    .append(escaparCsv(alerta.getTipoAnomalia().getDescricao())).append(",")
                    .append(escaparCsv(alerta.getNivelCriticidade().getDescricao())).append(",")
                    .append(escaparCsv(alerta.getDataHora().format(fmt))).append("\n");
        }

        EchoVitaLog.acao("CSV gerado | local=" + local.getNome()
                + " | alertas=" + local.getAlertas().size());
        return csv.toString();
    }

    private static String escaparCsv(String valor) {
        if (valor == null) {
            return "";
        }
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

}

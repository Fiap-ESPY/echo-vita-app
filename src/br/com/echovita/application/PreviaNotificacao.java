package br.com.echovita.application;

import br.com.echovita.domain.enums.NivelCriticidade;
import br.com.echovita.domain.usuario.Usuario;

import java.util.EnumMap;
import java.util.Map;

public final class PreviaNotificacao {

    private final Usuario usuario;
    private final int totalAlertas;
    private final Map<NivelCriticidade, Long> alertasAceitos;
    private final Map<NivelCriticidade, Long> alertasIgnorados;

    public PreviaNotificacao(
            Usuario usuario,
            int totalAlertas,
            Map<NivelCriticidade, Long> alertasAceitos,
            Map<NivelCriticidade, Long> alertasIgnorados
    ) {
        this.usuario = usuario;
        this.totalAlertas = totalAlertas;
        this.alertasAceitos = Map.copyOf(alertasAceitos);
        this.alertasIgnorados = Map.copyOf(alertasIgnorados);
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public int getTotalAlertas() {
        return this.totalAlertas;
    }

    public Map<NivelCriticidade, Long> getAlertasAceitos() {
        return this.alertasAceitos;
    }

    public Map<NivelCriticidade, Long> getAlertasIgnorados() {
        return this.alertasIgnorados;
    }

    public long getTotalAceitos() {
        return this.alertasAceitos.values().stream().mapToLong(Long::longValue).sum();
    }

    public long getTotalIgnorados() {
        return this.alertasIgnorados.values().stream().mapToLong(Long::longValue).sum();
    }

    public String formatarMensagemConfirmacao() {
        StringBuilder sb = new StringBuilder();
        sb.append("Enviar notificação para ").append(this.usuario.getNome())
                .append(" (").append(this.usuario.getCargo()).append(")?\n\n");
        sb.append("Limiar: ").append(this.usuario.getLimiarAlerta().formatarLimiar()).append("\n\n");

        long totalAceitos = this.getTotalAceitos();
        if (totalAceitos == 0) {
            sb.append("Nenhum alerta será enviado com o limiar atual.\n\n");
        } else {
            sb.append(totalAceitos).append(" de ").append(this.totalAlertas)
                    .append(" alertas serão enviados:\n");
            this.adicionarDetalhePorNivel(sb, this.alertasAceitos);
            sb.append("\n");
        }

        return sb.toString();
    }

    public String formatarResumoPosNotificacao(int totalAcumulado) {
        StringBuilder sb = new StringBuilder();
        sb.append("Notificação concluída\n");
        sb.append("Destinatário: ").append(this.usuario.getNome())
                .append(" (").append(this.usuario.getCargo()).append(")\n\n");

        long totalAceitos = this.getTotalAceitos();
        if (totalAceitos == 0) {
            sb.append("Nenhum alerta foi enviado (limiar atual não inclui os alertas disponíveis).\n");
        } else {
            sb.append("Alertas enviados nesta operação: ").append(totalAceitos).append("\n");
            this.adicionarDetalhePorNivel(sb, this.alertasAceitos);
        }

        sb.append("\nTotal acumulado do usuário: ").append(totalAcumulado).append(" notificações");
        return sb.toString();
    }

    private void adicionarDetalhePorNivel(StringBuilder sb, Map<NivelCriticidade, Long> contagens) {
        for (NivelCriticidade nivel : NivelCriticidade.values()) {
            Long quantidade = contagens.get(nivel);
            if (quantidade != null && quantidade > 0) {
                sb.append("  ").append(nivel.getDescricao()).append(":  ").append(quantidade).append("\n");
            }
        }
    }

    public static Map<NivelCriticidade, Long> criarMapaContagem() {
        return new EnumMap<>(NivelCriticidade.class);
    }
}

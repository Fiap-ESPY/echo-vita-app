package br.com.echovita.domain.local;

import br.com.echovita.domain.alerta.Alerta;
import br.com.echovita.domain.interfaces.GeradorRelatorio;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Ambiente monitorado do tipo fazenda, com foco em saúde animal.
 */
public class Fazenda extends LocalMonitorado implements GeradorRelatorio {

    private final String proprietario;
    private final int numeroCabecas;

    public Fazenda(String nome, String proprietario, int numeroCabecas) {
        super(nome, "Fazenda");
        if (proprietario == null || proprietario.isBlank()) {
            throw new IllegalArgumentException("Proprietário é obrigatório.");
        }
        if (numeroCabecas < 0) {
            throw new IllegalArgumentException("Número de cabeças não pode ser negativo.");
        }
        this.proprietario = proprietario.trim();
        this.numeroCabecas = numeroCabecas;
    }

    @Override
    public String getDescricao() {
        return String.format(
                "Fazenda de %s | %d cabeça(s) | Proprietário: %s",
                this.getNome(),
                this.numeroCabecas,
                this.proprietario);
    }

    @Override
    public String gerarRelatorio() {
        return this.formatarRelatorio(this.getAlertas());
    }

    @Override
    public String gerarRelatorio(String filtro) {
        return this.formatarRelatorio(this.filtrarAlertas(filtro));
    }

    @Override
    public String gerarRelatorio(String filtro, int limite) {
        this.validarLimite(limite);
        List<Alerta> alertas = this.filtrarAlertas(filtro).stream()
                .limit(limite)
                .collect(Collectors.toList());
        return this.formatarRelatorio(alertas);
    }

    private List<Alerta> filtrarAlertas(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return this.getAlertas();
        }
        String filtroNormalizado = this.normalizarFiltro(filtro);
        return this.getAlertas().stream()
                .filter(alerta -> this.alertaContemFiltro(alerta, filtroNormalizado))
                .collect(Collectors.toList());
    }

    private boolean alertaContemFiltro(Alerta alerta, String filtroNormalizado) {
        return alerta.getTipoAnomalia().getDescricao().toLowerCase().contains(filtroNormalizado)
                || alerta.getLocalizacao().toLowerCase().contains(filtroNormalizado)
                || alerta.getNivelCriticidade().getDescricao().toLowerCase().contains(filtroNormalizado);
    }

    private String normalizarFiltro(String filtro) {
        return filtro.trim().toLowerCase();
    }

    private void validarLimite(int limite) {
        if (limite <= 0) {
            throw new IllegalArgumentException("Limite deve ser maior que zero.");
        }
    }

    private String formatarRelatorio(List<Alerta> alertas) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO - FAZENDA ===\n");
        sb.append(this.getDescricao()).append("\n");
        sb.append("Sensores: ").append(this.getSensores().size()).append("\n");
        sb.append("Total de alertas: ").append(alertas.size()).append("\n");
        this.adicionarAlertasAoRelatorio(sb, alertas);
        return sb.toString();
    }

    private void adicionarAlertasAoRelatorio(StringBuilder sb, List<Alerta> alertas) {
        if (alertas.isEmpty()) {
            sb.append("Nenhum alerta registrado.\n");
            return;
        }
        for (Alerta alerta : alertas) {
            sb.append("- ").append(alerta.getTipoAnomalia().getDescricao())
                    .append(" | ").append(alerta.getNivelCriticidade().getDescricao())
                    .append(" | ").append(alerta.getSensorId()).append("\n");
        }
    }

    @Override
    public String toString() {
        String formato =
                "================================%n"
                        + "   FAZENDA%n"
                        + "================================%n"
                        + "Tipo:                    %s%n"
                        + "Nome:                    %s%n"
                        + "Descrição:               %s%n"
                        + "Status:                  %s%n"
                        + "Proprietário:            %s%n"
                        + "Número de cabeças:       %d%n";

        return String.format(
                formato,
                this.getTipo(),
                this.getNome(),
                this.getDescricao(),
                this.getStatus(),
                this.proprietario,
                this.numeroCabecas);
    }

    public String getProprietario() {
        return this.proprietario;
    }

    public int getNumeroCabecas() {
        return this.numeroCabecas;
    }
}

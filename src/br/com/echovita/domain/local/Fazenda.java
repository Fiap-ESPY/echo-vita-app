package br.com.echovita.domain.local;

import br.com.echovita.domain.alerta.Alerta;
import br.com.echovita.domain.exception.EchoVitaException;
import br.com.echovita.domain.interfaces.GeradorRelatorio;

import java.util.List;

/**
 * Ambiente monitorado do tipo fazenda, com foco em saúde animal.
 */
public class Fazenda extends LocalMonitorado implements GeradorRelatorio {

    private final String proprietario;
    private final int numeroCabecas;

    public Fazenda(String nome, String proprietario, int numeroCabecas) {
        super(nome, "Fazenda");
        if (proprietario == null || proprietario.isBlank()) {
            throw new EchoVitaException("Proprietário é obrigatório.");
        }
        if (numeroCabecas < 0) {
            throw new EchoVitaException("Número de cabeças não pode ser negativo.");
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
        List<Alerta> filtrados = this.getAlertas().stream()
                .filter(alerta -> alerta.getTipoAnomalia().getDescricao()
                        .toLowerCase()
                        .contains(filtro.toLowerCase()))
                .toList();
        return this.formatarRelatorio(filtrados);
    }

    @Override
    public String gerarRelatorio(String filtro, int limite) {
        if (limite < 0) {
            throw new EchoVitaException("Limite do relatório não pode ser negativo.");
        }
        List<Alerta> filtrados = this.getAlertas().stream()
                .filter(alerta -> alerta.getTipoAnomalia().getDescricao()
                        .toLowerCase()
                        .contains(filtro.toLowerCase()))
                .limit(limite)
                .toList();
        return this.formatarRelatorio(filtrados);
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

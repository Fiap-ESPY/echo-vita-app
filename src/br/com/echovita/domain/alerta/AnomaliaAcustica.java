package br.com.echovita.domain.alerta;

import br.com.echovita.domain.enums.TipoAnomalia;
import br.com.echovita.domain.exception.EchoVitaException;

/**
 * Representa uma anomalia sonora detectada após análise de sinal acústico.
 */
public class AnomaliaAcustica {

    private final TipoAnomalia tipo;
    private final int intensidade;
    private final int frequencia;

    public AnomaliaAcustica(TipoAnomalia tipo, int intensidade, int frequencia) {
        if (tipo == null) {
            throw new EchoVitaException("Tipo de anomalia é obrigatório.");
        }
        if (intensidade < 0 || intensidade > 100) {
            throw new EchoVitaException("Intensidade deve estar entre 0 e 100.");
        }
        if (frequencia < 0) {
            throw new EchoVitaException("Frequência não pode ser negativa.");
        }
        this.tipo = tipo;
        this.intensidade = intensidade;
        this.frequencia = frequencia;
    }

    public TipoAnomalia getTipo() {
        return this.tipo;
    }

    public int getIntensidade() {
        return this.intensidade;
    }

    public int getFrequencia() {
        return this.frequencia;
    }

    @Override
    public String toString() {
        String formato =
                "================================%n"
                        + "   ANOMALIA ACUSTICA%n"
                        + "================================%n"
                        + "Tipo:                    %s%n"
                        + "Intensidade:             %d%n"
                        + "Frequência:              %d%n";

        return String.format(
                formato,
                this.tipo.getDescricao(),
                this.intensidade,
                this.frequencia);
    }
}

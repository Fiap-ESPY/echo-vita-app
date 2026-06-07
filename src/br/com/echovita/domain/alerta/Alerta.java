package br.com.echovita.domain.alerta;

import br.com.echovita.domain.enums.NivelCriticidade;
import br.com.echovita.domain.enums.TipoAnomalia;
import br.com.echovita.domain.exception.EchoVitaException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Representa um alerta preventivo gerado após detecção de anomalia acústica.
 */
public class Alerta {

    private final Long id = Math.abs(new Random().nextLong());
    private final String sensorId;
    private final String localizacao;
    private final LocalDateTime dataHora;
    private final TipoAnomalia tipoAnomalia;
    private final NivelCriticidade nivelCriticidade;

    public Alerta(
            String sensorId,
            String localizacao,
            TipoAnomalia tipoAnomalia,
            NivelCriticidade nivelCriticidade
    ) {
        if (sensorId == null || sensorId.isBlank()) {
            throw new EchoVitaException("ID do sensor é obrigatório.");
        }
        if (localizacao == null || localizacao.isBlank()) {
            throw new EchoVitaException("Localização é obrigatória.");
        }
        if (tipoAnomalia == null) {
            throw new EchoVitaException("Tipo de anomalia é obrigatório.");
        }
        if (nivelCriticidade == null) {
            throw new EchoVitaException("Nível de criticidade é obrigatório.");
        }
        this.sensorId = sensorId.trim();
        this.localizacao = localizacao.trim();
        this.dataHora = LocalDateTime.now();
        this.tipoAnomalia = tipoAnomalia;
        this.nivelCriticidade = nivelCriticidade;
    }

    public Long getId() {
        return this.id;
    }

    public String getSensorId() {
        return this.sensorId;
    }

    public String getLocalizacao() {
        return this.localizacao;
    }

    public LocalDateTime getDataHora() {
        return this.dataHora;
    }

    public TipoAnomalia getTipoAnomalia() {
        return this.tipoAnomalia;
    }

    public NivelCriticidade getNivelCriticidade() {
        return this.nivelCriticidade;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String resumoFormatado =
                "================================%n"
                        + "   ALERTA%n"
                        + "================================%n"
                        + "ID:                      %d%n"
                        + "Sensor:                  %s%n"
                        + "Local:                   %s%n"
                        + "Tipo:                    %s%n"
                        + "Criticidade:             %s%n"
                        + "Data/Hora:               %s%n";

        return String.format(
                resumoFormatado,
                this.getId(),
                this.getSensorId(),
                this.getLocalizacao(),
                this.getTipoAnomalia().getDescricao(),
                this.getNivelCriticidade().getDescricao(),
                this.dataHora.format(fmt)
        );
    }
}

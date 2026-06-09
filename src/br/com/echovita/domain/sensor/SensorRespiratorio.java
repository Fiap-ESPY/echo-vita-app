package br.com.echovita.domain.sensor;

import br.com.echovita.domain.alerta.AnomaliaAcustica;
import br.com.echovita.domain.enums.TipoAnomalia;
import br.com.echovita.domain.exception.EchoVitaException;

import java.util.Random;

/**
 * Sensor especializado na detecção de padrões sonoros respiratórios.
 */
public class SensorRespiratorio extends Sensor {

    private static final TipoAnomalia[] TIPOS = {
            TipoAnomalia.TOSSE, TipoAnomalia.ESPIRRO, TipoAnomalia.CHORO
    };
    private static final Random RANDOM = new Random();

    private final int sensibilidade;

    public SensorRespiratorio(String id, String localizacao, int sensibilidade) {
        super(id, localizacao);
        if (sensibilidade < 1 || sensibilidade > 10) {
            throw new EchoVitaException("Sensibilidade deve estar entre 1 e 10.");
        }
        this.sensibilidade = sensibilidade;
    }

    @Override
    public String getTipoSensor() {
        return "Respiratorio";
    }

    @Override
    public AnomaliaAcustica analisarSinal(String som) {
        if (som == null || som.isBlank()) {
            throw new EchoVitaException("Sinal sonoro é obrigatório.");
        }
        TipoAnomalia tipo = TIPOS[RANDOM.nextInt(TIPOS.length)];
        int base = Math.min(100, 40 + this.sensibilidade * 6);
        int intensidade = Math.max(0, Math.min(100, base + RANDOM.nextInt(41) - 20));
        int frequencia = RANDOM.nextInt(50) + 10;
        return new AnomaliaAcustica(tipo, intensidade, frequencia);
    }

    public int getSensibilidade() {
        return this.sensibilidade;
    }

    @Override
    public String toString() {
        String formato =
                "================================%n"
                        + "   SENSOR RESPIRATORIO%n"
                        + "================================%n"
                        + "Tipo:                    %s%n"
                        + "ID:                      %s%n"
                        + "Localização:             %s%n"
                        + "Status:                  %s%n"
                        + "Sensibilidade:           %d%n";

        return String.format(
                formato,
                this.getTipoSensor(),
                this.getId(),
                this.getLocalizacao(),
                this.isAtivo() ? "ATIVO" : "INATIVO",
                this.sensibilidade);
    }
}

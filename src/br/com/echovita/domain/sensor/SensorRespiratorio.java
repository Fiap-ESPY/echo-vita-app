package br.com.echovita.domain.sensor;

import br.com.echovita.domain.alerta.AnomaliaAcustica;
import br.com.echovita.domain.enums.TipoAnomalia;

/**
 * Sensor especializado na detecção de padrões sonoros respiratórios.
 */
public class SensorRespiratorio extends Sensor {

    private final int sensibilidade;

    public SensorRespiratorio(String id, String localizacao, int sensibilidade) {
        super(id, localizacao);
        if (sensibilidade < 1 || sensibilidade > 10) {
            throw new IllegalArgumentException("Sensibilidade deve estar entre 1 e 10.");
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
            throw new IllegalArgumentException("Sinal sonoro é obrigatório.");
        }
        String somNormalizado = som.trim().toLowerCase();
        TipoAnomalia tipo = somNormalizado.contains("tosse")
                ? TipoAnomalia.TOSSE
                : TipoAnomalia.ESPIRRO;
        int intensidade = Math.min(100, 40 + this.sensibilidade * 6);
        int frequencia = somNormalizado.length() % 50 + 10;
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

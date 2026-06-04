package br.com.echovita.domain.sensor;

import br.com.echovita.domain.alerta.AnomaliaAcustica;
import br.com.echovita.domain.enums.TipoAnomalia;

/**
 * Sensor especializado na detecção de padrões comportamentais e estresse.
 */
public class SensorComportamental extends Sensor {

    private final double raioCobertura;

    public SensorComportamental(String id, String localizacao, double raioCobertura) {
        super(id, localizacao);
        if (raioCobertura <= 0) {
            throw new IllegalArgumentException("Raio de cobertura deve ser maior que zero.");
        }
        this.raioCobertura = raioCobertura;
    }

    @Override
    public String getTipoSensor() {
        return "Comportamental";
    }

    @Override
    public AnomaliaAcustica analisarSinal(String som) {
        if (som == null || som.isBlank()) {
            throw new IllegalArgumentException("Sinal sonoro é obrigatório.");
        }
        String somNormalizado = som.trim().toLowerCase();
        TipoAnomalia tipo;
        if (somNormalizado.contains("estresse")) {
            tipo = TipoAnomalia.ESTRESSE;
        } else if (somNormalizado.contains("agitacao")) {
            tipo = TipoAnomalia.AGITACAO;
        } else {
            tipo = TipoAnomalia.RUIDO_ANOMALO;
        }
        int intensidade = (int) Math.min(100, 30 + this.raioCobertura * 5);
        int frequencia = (int) (this.raioCobertura * 2) + 5;
        return new AnomaliaAcustica(tipo, intensidade, frequencia);
    }

    public double getRaioCobertura() {
        return this.raioCobertura;
    }

    @Override
    public String toString() {
        String formato =
                "================================%n"
                        + "   SENSOR COMPORTAMENTAL%n"
                        + "================================%n"
                        + "Tipo:                    %s%n"
                        + "ID:                      %s%n"
                        + "Localização:             %s%n"
                        + "Status:                  %s%n"
                        + "Raio de cobertura:       %.1f m%n";

        return String.format(
                formato,
                this.getTipoSensor(),
                this.getId(),
                this.getLocalizacao(),
                this.isAtivo() ? "ATIVO" : "INATIVO",
                this.raioCobertura);
    }
}

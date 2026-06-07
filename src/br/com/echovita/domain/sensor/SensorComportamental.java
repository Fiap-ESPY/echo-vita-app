package br.com.echovita.domain.sensor;

import br.com.echovita.domain.alerta.AnomaliaAcustica;
import br.com.echovita.domain.enums.TipoAnomalia;
import br.com.echovita.domain.exception.EchoVitaException;

import java.util.Random;

/**
 * Sensor especializado na detecção de padrões comportamentais e estresse.
 */
public class SensorComportamental extends Sensor {

    private static final TipoAnomalia[] TIPOS = {
            TipoAnomalia.ESTRESSE, TipoAnomalia.AGITACAO, TipoAnomalia.RUIDO_ANOMALO
    };
    private static final Random RANDOM = new Random();

    private final double raioCobertura;

    public SensorComportamental(String id, String localizacao, double raioCobertura) {
        super(id, localizacao);
        if (raioCobertura <= 0) {
            throw new EchoVitaException("Raio de cobertura deve ser maior que zero.");
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
            throw new EchoVitaException("Sinal sonoro é obrigatório.");
        }
        TipoAnomalia tipo = TIPOS[RANDOM.nextInt(TIPOS.length)];
        int base = (int) Math.min(100, 30 + this.raioCobertura * 5);
        int intensidade = Math.max(0, Math.min(100, base + RANDOM.nextInt(41) - 20));
        int frequencia = RANDOM.nextInt(50) + 10;
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

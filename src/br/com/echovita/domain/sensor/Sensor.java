package br.com.echovita.domain.sensor;

import br.com.echovita.domain.alerta.AnomaliaAcustica;
import br.com.echovita.domain.exception.EchoVitaException;

/**
 * Classe abstrata que representa um sensor de captação instalado em ambiente monitorado.
 * Subclasses especializam o tipo de sensor e a análise de sinais acústicos.
 */
public abstract class Sensor {

    private final String id;
    private final String localizacao;
    private boolean ativo;

    protected Sensor(String id, String localizacao) {
        if (id == null || id.isBlank()) {
            throw new EchoVitaException("ID do sensor é obrigatório.");
        }
        if (localizacao == null || localizacao.isBlank()) {
            throw new EchoVitaException("Localização do sensor é obrigatória.");
        }
        this.id = id.trim();
        this.localizacao = localizacao.trim();
        this.ativo = true;
    }

    public abstract AnomaliaAcustica analisarSinal(String som);

    public abstract String getTipoSensor();

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    public String getId() {
        return this.id;
    }

    public String getLocalizacao() {
        return this.localizacao;
    }

    public boolean isAtivo() {
        return this.ativo;
    }

    @Override
    public String toString() {
        String formato =
                "================================%n"
                        + "   SENSOR%n"
                        + "================================%n"
                        + "Tipo:                    %s%n"
                        + "ID:                      %s%n"
                        + "Localização:             %s%n"
                        + "Status:                  %s%n";

        return String.format(
                formato,
                this.getTipoSensor(),
                this.id,
                this.localizacao,
                this.ativo ? "ATIVO" : "INATIVO");
    }
}

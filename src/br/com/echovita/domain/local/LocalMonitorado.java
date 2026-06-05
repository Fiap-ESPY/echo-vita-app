package br.com.echovita.domain.local;

import br.com.echovita.domain.alerta.Alerta;
import br.com.echovita.domain.interfaces.MonitorAmbiente;
import br.com.echovita.domain.sensor.Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata que representa um ambiente monitorado pela EchoVita.
 * Subclasses especializam o tipo de local e a descrição operacional.
 */
public abstract class LocalMonitorado implements MonitorAmbiente {

    private final String nome;
    private final String tipo;
    private final List<Sensor> sensores;
    private final List<Alerta> alertas;

    protected LocalMonitorado(String nome, String tipo) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do local é obrigatório.");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Tipo do local é obrigatório.");
        }
        this.nome = nome.trim();
        this.tipo = tipo.trim();
        this.sensores = new ArrayList<>();
        this.alertas = new ArrayList<>();
    }

    public abstract String getDescricao();

    public void adicionarSensor(Sensor sensor) {
        if (sensor == null) {
            throw new IllegalArgumentException("Sensor não pode ser nulo.");
        }
        this.sensores.add(sensor);
    }

    @Override
    public void monitorar() {
        // Implementado pelas subclasses nas próximas entregas
    }

    @Override
    public void receberAlerta(Alerta alerta) {
        if (alerta == null) {
            throw new IllegalArgumentException("Alerta não pode ser nulo.");
        }
        this.alertas.add(alerta);
    }

    @Override
    public String getStatus() {
        return this.sensores.isEmpty()
                ? "Sem sensores cadastrados"
                : "Monitorando com " + this.sensores.size() + " sensor(es)";
    }

    public String getNome() {
        return this.nome;
    }

    public String getTipo() {
        return this.tipo;
    }

    public List<Sensor> getSensores() {
        return List.copyOf(this.sensores);
    }

    public List<Alerta> getAlertas() {
        return List.copyOf(this.alertas);
    }

    @Override
    public String toString() {
        String formato =
                "================================%n"
                        + "   LOCAL MONITORADO%n"
                        + "================================%n"
                        + "Tipo:                    %s%n"
                        + "Nome:                    %s%n"
                        + "Descrição:               %s%n"
                        + "Status:                  %s%n";

        return String.format(
                formato,
                this.tipo,
                this.nome,
                this.getDescricao(),
                this.getStatus());
    }
}

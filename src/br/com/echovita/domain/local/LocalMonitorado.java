package br.com.echovita.domain.local;

import br.com.echovita.application.EchoVitaLog;
import br.com.echovita.domain.alerta.Alerta;
import br.com.echovita.domain.alerta.AnomaliaAcustica;
import br.com.echovita.domain.enums.NivelCriticidade;
import br.com.echovita.domain.exception.EchoVitaException;
import br.com.echovita.domain.interfaces.MonitorAmbiente;
import br.com.echovita.domain.sensor.Sensor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe abstrata que representa um ambiente monitorado pela EchoVita.
 * Subclasses especializam o tipo de local e a descrição operacional.
 */
public abstract class LocalMonitorado implements MonitorAmbiente {

    private static final Random RANDOM = new Random();

    private final String nome;
    private final String tipo;
    private String imagemUrl;
    private final List<Sensor> sensores;
    private final List<Alerta> alertas;

    protected LocalMonitorado(String nome, String tipo) {
        if (nome == null || nome.isBlank()) {
            throw new EchoVitaException("Nome do local é obrigatório.");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new EchoVitaException("Tipo do local é obrigatório.");
        }
        this.nome = nome.trim();
        this.tipo = tipo.trim();
        this.sensores = new ArrayList<>();
        this.alertas = new ArrayList<>();
    }

    public abstract String getDescricao();

    public void adicionarSensor(Sensor sensor) {
        if (sensor == null) {
            throw new EchoVitaException("Sensor não pode ser nulo.");
        }
        this.sensores.add(sensor);
    }

    @Override
    public void monitorar() {
        List<Sensor> sensoresAtivos = this.sensores.stream()
                .filter(Sensor::isAtivo)
                .toList();

        int alertasAntes = this.alertas.size();

        for (int i = 0; i < sensoresAtivos.size(); i++) {
            Sensor sensor = sensoresAtivos.get(RANDOM.nextInt(sensoresAtivos.size()));
            AnomaliaAcustica anomalia = sensor.analisarSinal("monitoramento automático");
            NivelCriticidade nivel = NivelCriticidade.values()[
                    RANDOM.nextInt(NivelCriticidade.values().length)];
            Alerta alerta = new Alerta(
                    sensor.getId(),
                    this.getNome(),
                    anomalia.getTipo(),
                    nivel);
            this.receberAlerta(alerta);
        }

        EchoVitaLog.acao("Ciclo de monitoramento | local=" + this.getNome()
                + " | sensoresAtivos=" + sensoresAtivos.size()
                + " | alertasGerados=" + (this.alertas.size() - alertasAntes)
                + " | totalAlertas=" + this.alertas.size());
    }

    @Override
    public void receberAlerta(Alerta alerta) {
        if (alerta == null) {
            throw new EchoVitaException("Alerta não pode ser nulo.");
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

    public String getImagemUrl() {
        return this.imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
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

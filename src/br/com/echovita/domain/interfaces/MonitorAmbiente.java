package br.com.echovita.domain.interfaces;

import br.com.echovita.domain.alerta.Alerta;

public interface MonitorAmbiente {

    void monitorar();

    String getStatus();

    void receberAlerta(Alerta alerta);
}

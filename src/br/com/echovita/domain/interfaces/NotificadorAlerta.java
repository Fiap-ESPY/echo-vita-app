package br.com.echovita.domain.interfaces;

import br.com.echovita.domain.alerta.Alerta;

public interface NotificadorAlerta {

    void notificar(Alerta alerta);

    void configurarLimiarAlerta(int limiar);
}

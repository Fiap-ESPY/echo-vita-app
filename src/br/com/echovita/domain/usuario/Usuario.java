package br.com.echovita.domain.usuario;

import br.com.echovita.domain.alerta.Alerta;
import br.com.echovita.domain.interfaces.NotificadorAlerta;
import br.com.echovita.domain.enums.NivelCriticidade;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata que representa um usuário autorizado a receber alertas da EchoVita.
 * Subclasses especializam o perfil e o cargo do usuário.
 */
public abstract class Usuario implements NotificadorAlerta {

    private final String nome;
    private final String rm;
    private NivelCriticidade limiarAlerta;
    private final List<Alerta> notificacoesRecebidas;

    protected Usuario(String nome, String rm) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do usuário é obrigatório.");
        }
        if (rm == null || rm.isBlank()) {
            throw new IllegalArgumentException("RM do usuário é obrigatório.");
        }
        this.nome = nome.trim();
        this.rm = rm.trim();
        this.limiarAlerta = NivelCriticidade.MEDIO;
        this.notificacoesRecebidas = new ArrayList<>();
    }

    public abstract String exibirPerfil();

    public abstract String getCargo();

    @Override
    public void notificar(Alerta alerta) {
        if (alerta == null) {
            throw new IllegalArgumentException("Alerta não pode ser nulo.");
        }
        if (deveNotificar(alerta.getNivelCriticidade())) {
            this.notificacoesRecebidas.add(alerta);
        }
    }

    @Override
    public void configurarLimiarAlerta(int limiar) {
        this.limiarAlerta = mapearLimiar(limiar);
    }

    private boolean deveNotificar(NivelCriticidade nivelAlerta) {
        return nivelAlerta.ordinal() >= this.limiarAlerta.ordinal();
    }

    private NivelCriticidade mapearLimiar(int limiar) {
        return switch (limiar) {
            case 1 -> NivelCriticidade.BAIXO;
            case 3 -> NivelCriticidade.ALTO;
            default -> NivelCriticidade.MEDIO;
        };
    }

    public String getNome() {
        return this.nome;
    }

    public String getRm() {
        return this.rm;
    }

    public NivelCriticidade getLimiarAlerta() {
        return this.limiarAlerta;
    }

    public List<Alerta> getNotificacoesRecebidas() {
        return List.copyOf(this.notificacoesRecebidas);
    }

    @Override
    public String toString() {
        String formato =
                "================================%n"
                        + "   USUARIO%n"
                        + "================================%n"
                        + "Cargo:                   %s%n"
                        + "Nome:                    %s%n"
                        + "RM:                      %s%n"
                        + "Limiar de alerta:        %s%n";

        return String.format(
                formato,
                this.getCargo(),
                this.nome,
                this.rm,
                this.limiarAlerta.getDescricao());
    }
}

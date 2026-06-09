package br.com.echovita.domain.usuario;

import br.com.echovita.domain.alerta.Alerta;
import br.com.echovita.domain.enums.NivelCriticidade;
import br.com.echovita.domain.exception.EchoVitaException;
import br.com.echovita.domain.interfaces.NotificadorAlerta;
import br.com.echovita.domain.local.LocalMonitorado;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe abstrata que representa um usuário autorizado a receber alertas da EchoVita.
 * Subclasses especializam o perfil e o cargo do usuário.
 */
public abstract class Usuario implements NotificadorAlerta {

    private final String nome;
    private final String rm;
    private String imagemUrl;
    private String email;
    private String telefone;
    private NivelCriticidade limiarAlerta;
    private final List<Alerta> notificacoesRecebidas;
    private final List<LocalMonitorado> locaisVinculados;

    protected Usuario(String nome, String rm) {
        if (nome == null || nome.isBlank()) {
            throw new EchoVitaException("Nome do usuário é obrigatório.");
        }
        if (rm == null || rm.isBlank()) {
            throw new EchoVitaException("RM do usuário é obrigatório.");
        }
        this.nome = nome.trim();
        this.rm = rm.trim();
        this.limiarAlerta = NivelCriticidade.MEDIO;
        this.notificacoesRecebidas = new ArrayList<>();
        this.locaisVinculados = new ArrayList<>();
    }

    public void vincularLocal(LocalMonitorado local) {
        if (local == null) {
            throw new EchoVitaException("Local não pode ser nulo.");
        }
        if (!this.locaisVinculados.contains(local)) {
            this.locaisVinculados.add(local);
        }
    }

    public boolean isVinculadoAo(LocalMonitorado local) {
        return this.locaisVinculados.contains(local);
    }

    public List<LocalMonitorado> getLocaisVinculados() {
        return List.copyOf(this.locaisVinculados);
    }

    public abstract String exibirPerfil();

    public abstract String getCargo();

    @Override
    public void notificar(Alerta alerta) {
        if (alerta == null) {
            throw new EchoVitaException("Alerta não pode ser nulo.");
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

    public String getImagemUrl() {
        return this.imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public String getTelefone() {
        return this.telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone != null ? telefone.trim() : null;
    }

    public NivelCriticidade getLimiarAlerta() {
        return this.limiarAlerta;
    }

    public List<Alerta> getNotificacoesRecebidas() {
        return List.copyOf(this.notificacoesRecebidas);
    }

    protected String formatarLimiarAtual() {
        return this.limiarAlerta.formatarLimiar();
    }

    protected String formatarLocaisVinculados() {
        if (this.locaisVinculados.isEmpty()) {
            return "Nenhum";
        }
        return this.locaisVinculados.stream()
                .map(LocalMonitorado::getNome)
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        String formato =
                "================================%n"
                        + "   USUÁRIO%n"
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
                this.formatarLimiarAtual());
    }
}

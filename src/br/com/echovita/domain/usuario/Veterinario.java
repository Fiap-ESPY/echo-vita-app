package br.com.echovita.domain.usuario;

/**
 * Usuário veterinário autorizado a receber alertas da EchoVita.
 */
public class Veterinario extends Usuario {

    private final String crmv;
    private final String especialidade;

    public Veterinario(String nome, String rm, String crmv, String especialidade) {
        super(nome, rm);
        if (crmv == null || crmv.isBlank()) {
            throw new IllegalArgumentException("CRMV e obrigatório.");
        }
        if (especialidade == null || especialidade.isBlank()) {
            throw new IllegalArgumentException("Especialidade e obrigatória.");
        }
        this.crmv = crmv.trim();
        this.especialidade = especialidade.trim();
    }

    @Override
    public String getCargo() {
        return "Veterinário";
    }

    @Override
    public String exibirPerfil() {
        return this.toString();
    }

    @Override
    public String toString() {
        String formato =
                "================================%n"
                        + "   VETERINARIO%n"
                        + "================================%n"
                        + "Cargo:                   %s%n"
                        + "Nome:                    %s%n"
                        + "RM:                      %s%n"
                        + "Limiar de alerta:        %s%n"
                        + "CRMV:                    %s%n"
                        + "Especialidade:           %s%n";

        return String.format(
                formato,
                this.getCargo(),
                this.getNome(),
                this.getRm(),
                this.getLimiarAlerta().getDescricao(),
                this.crmv,
                this.especialidade);
    }

    public String getCrmv() {
        return this.crmv;
    }

    public String getEspecialidade() {
        return this.especialidade;
    }
}

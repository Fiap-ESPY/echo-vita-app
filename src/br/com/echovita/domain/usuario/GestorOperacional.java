package br.com.echovita.domain.usuario;

/**
 * Usuário gestor operacional responsável por ambientes monitorados.
 */
public class GestorOperacional extends Usuario {

    private final String setor;

    public GestorOperacional(String nome, String rm, String setor) {
        super(nome, rm);
        if (setor == null || setor.isBlank()) {
            throw new IllegalArgumentException("Setor é obrigatório.");
        }
        this.setor = setor.trim();
    }

    @Override
    public String getCargo() {
        return "Gestor Operacional";
    }

    @Override
    public String exibirPerfil() {
        return this.toString();
    }

    @Override
    public String toString() {
        String formato =
                "================================%n"
                        + "   GESTOR OPERACIONAL%n"
                        + "================================%n"
                        + "Cargo:                   %s%n"
                        + "Nome:                    %s%n"
                        + "RM:                      %s%n"
                        + "Limiar de alerta:        %s%n"
                        + "Setor:                   %s%n";

        return String.format(
                formato,
                this.getCargo(),
                this.getNome(),
                this.getRm(),
                this.getLimiarAlerta().getDescricao(),
                this.setor);
    }

    public String getSetor() {
        return this.setor;
    }
}

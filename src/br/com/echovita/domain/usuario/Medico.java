package br.com.echovita.domain.usuario;

import br.com.echovita.domain.exception.EchoVitaException;

/**
 * Usuário médico autorizado a receber alertas da EchoVita em unidades de saúde.
 */
public class Medico extends Usuario {

    private final String crm;
    private final String especialidade;

    public Medico(String nome, String rm, String crm, String especialidade) {
        super(nome, rm);
        if (crm == null || crm.isBlank()) {
            throw new EchoVitaException("CRM é obrigatório.");
        }
        if (especialidade == null || especialidade.isBlank()) {
            throw new EchoVitaException("Especialidade é obrigatória.");
        }
        this.crm = crm.trim();
        this.especialidade = especialidade.trim();
    }

    @Override
    public String getCargo() {
        return "Médico";
    }

    @Override
    public String exibirPerfil() {
        return this.toString();
    }

    @Override
    public String toString() {
        String formato =
                "================================%n"
                        + "   MEDICO%n"
                        + "================================%n"
                        + "Cargo:                   %s%n"
                        + "Nome:                    %s%n"
                        + "RM:                      %s%n"
                        + "Limiar de alerta:        %s%n"
                        + "Locais vinculados:       %s%n"
                        + "CRM:                     %s%n"
                        + "Especialidade:           %s%n";

        return String.format(
                formato,
                this.getCargo(),
                this.getNome(),
                this.getRm(),
                this.formatarLimiarAtual(),
                this.formatarLocaisVinculados(),
                this.crm,
                this.especialidade);
    }

    public String getCrm() {
        return this.crm;
    }

    public String getEspecialidade() {
        return this.especialidade;
    }
}

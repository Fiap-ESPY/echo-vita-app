package br.com.echovita.domain.usuario;

import br.com.echovita.domain.exception.EchoVitaException;

/**
 * Usuário proprietário rural autorizado a receber alertas da EchoVita em fazendas.
 */
public class ProprietarioRural extends Usuario {

    private final String nomeFazenda;
    private final String regiao;

    public ProprietarioRural(String nome, String rm, String nomeFazenda, String regiao) {
        super(nome, rm);
        if (nomeFazenda == null || nomeFazenda.isBlank()) {
            throw new EchoVitaException("Nome da fazenda é obrigatório.");
        }
        if (regiao == null || regiao.isBlank()) {
            throw new EchoVitaException("Região é obrigatória.");
        }
        this.nomeFazenda = nomeFazenda.trim();
        this.regiao = regiao.trim();
    }

    @Override
    public String getCargo() {
        return "Proprietário Rural";
    }

    @Override
    public String exibirPerfil() {
        return this.toString();
    }

    @Override
    public String toString() {
        String formato =
                "================================%n"
                        + "   PROPRIETARIO RURAL%n"
                        + "================================%n"
                        + "Cargo:                   %s%n"
                        + "Nome:                    %s%n"
                        + "RM:                      %s%n"
                        + "Limiar de alerta:        %s%n"
                        + "Locais vinculados:       %s%n"
                        + "Fazenda:                 %s%n"
                        + "Região:                  %s%n";

        return String.format(
                formato,
                this.getCargo(),
                this.getNome(),
                this.getRm(),
                this.formatarLimiarAtual(),
                this.formatarLocaisVinculados(),
                this.nomeFazenda,
                this.regiao);
    }

    public String getNomeFazenda() {
        return this.nomeFazenda;
    }

    public String getRegiao() {
        return this.regiao;
    }
}

package br.com.echovita.domain.enums;

public enum TipoAnomalia {
    TOSSE("Tosse"),
    ESPIRRO("Espirro"),
    CHORO("Choro"),
    ESTRESSE("Estresse"),
    AGITACAO("Agitação"),
    RUIDO_ANOMALO("Ruído anômalo");

    private final String descricao;

    TipoAnomalia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return this.descricao;
    }
}

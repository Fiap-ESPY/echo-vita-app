package br.com.echovita.domain.enums;

public enum NivelCriticidade {
    BAIXO("Baixo", "Recebe alertas de todos os níveis"),
    MEDIO("Médio", "Recebe médio e alto (padrão)"),
    ALTO("Alto", "Recebe apenas alertas críticos");

    private final String descricao;
    private final String comportamento;

    NivelCriticidade(String descricao, String comportamento) {
        this.descricao = descricao;
        this.comportamento = comportamento;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public String getComportamento() {
        return this.comportamento;
    }

    public String formatarLimiar() {
        return this.descricao + " — " + this.comportamento;
    }
}

package br.com.echovita.domain.interfaces;

public interface GeradorRelatorio {

    String gerarRelatorio();

    String gerarRelatorio(String filtro);

    String gerarRelatorio(String filtro, int limite);
}

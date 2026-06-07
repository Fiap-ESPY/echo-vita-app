package br.com.echovita.domain.exception;

/**
 * Exceção customizada para erros de validação e regras de negócio da EchoVita.
 */
public class EchoVitaException extends RuntimeException {

    public EchoVitaException(String mensagem) {
        super(mensagem);
    }

}

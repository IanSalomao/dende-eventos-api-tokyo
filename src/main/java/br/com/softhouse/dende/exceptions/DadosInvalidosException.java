package br.com.softhouse.dende.exceptions;

public class DadosInvalidosException extends RuntimeException {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}

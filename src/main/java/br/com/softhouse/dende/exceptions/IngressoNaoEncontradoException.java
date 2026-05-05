package br.com.softhouse.dende.exceptions;

public class IngressoNaoEncontradoException extends RuntimeException {

    public IngressoNaoEncontradoException(Long id) {
        super("Ingresso não encontrado com o id: " + id);
    }
}

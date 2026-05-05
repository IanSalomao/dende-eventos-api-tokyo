package br.com.softhouse.dende.exceptions;

public class EventoNaoEncontradoException extends RuntimeException {

    public EventoNaoEncontradoException(Long id) {
        super("Evento não encontrado com o id: " + id);
    }
}

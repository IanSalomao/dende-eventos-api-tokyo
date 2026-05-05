package br.com.softhouse.dende.exceptions;

public class CapacidadeExcedidaException extends RuntimeException {

    public CapacidadeExcedidaException(Long eventoId) {
        super("Evento " + eventoId + " não possui vagas disponíveis.");
    }
}

package br.com.softhouse.dende.model.dto;


import java.time.LocalDateTime;

public record IngressoDTO(
        Long id,
        String nomeEvento,
        LocalDateTime dataEvento,
        double valorPago,
        String status,
        LocalDateTime dataCompra
) {
}


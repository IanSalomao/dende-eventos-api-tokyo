package br.com.softhouse.dende.model.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IngressoDTO(
        Long id,
        String nomeEvento,
        LocalDateTime dataEvento,
        BigDecimal valorPago,
        String status,
        LocalDateTime dataCompra
) {
}


package br.com.softhouse.dende.model.dto.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IngressoResponseDTO(
        Long id,
        String nomeEvento,
        LocalDateTime dataEvento,
        BigDecimal valorPago,
        String status,
        LocalDateTime dataCompra
) {
}


package br.com.softhouse.dende.model.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

// [AVALIAÇÃO - Item 5] A conversão de Ingresso para IngressoDTO é feita inline no controller (duas vezes:
// em comprarIngresso e em listarIngressos). Uma classe IngressoMapper centralizaria esse mapeamento.
// Código sugerido: public static IngressoDTO toDTO(Ingresso i) { ... }
public record IngressoDTO(
        Long id,
        String nomeEvento,
        LocalDateTime dataEvento,
        BigDecimal valorPago,
        String status,
        LocalDateTime dataCompra
) {
}


package br.com.softhouse.dende.model.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CompraIngressoResponseDTO(
        List<IngressoResponseDTO> ingressos,
        BigDecimal valorTotal
) {}

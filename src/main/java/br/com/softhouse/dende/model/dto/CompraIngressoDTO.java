package br.com.softhouse.dende.model.dto;

import java.util.List;

public record CompraIngressoDTO(
        List<IngressoResponseDTO> ingressos,
        double valorTotal
) {}

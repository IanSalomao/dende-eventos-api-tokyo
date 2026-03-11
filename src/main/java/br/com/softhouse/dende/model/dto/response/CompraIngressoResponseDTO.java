package br.com.softhouse.dende.model.dto.response;

import br.com.softhouse.dende.model.Ingresso;

import java.util.List;

public record CompraIngressoResponseDTO(
        List<IngressoResponseDTO> ingressos,
        double valorTotal
) {}

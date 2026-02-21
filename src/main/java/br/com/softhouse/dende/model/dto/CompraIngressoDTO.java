package br.com.softhouse.dende.model.dto;

import br.com.softhouse.dende.model.Ingresso;

import java.util.List;

public record CompraIngressoDTO(
        List<Ingresso> ingressos,
        double valorTotal
) {}

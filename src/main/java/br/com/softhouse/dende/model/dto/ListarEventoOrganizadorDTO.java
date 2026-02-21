package br.com.softhouse.dende.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ListarEventoOrganizadorDTO (
        String nome,
        LocalDateTime dataInicio,
        LocalDateTime dataFinal,
        BigDecimal precoIngresso,
        Integer capacidadeMaxima,
        String localAcesso
){}
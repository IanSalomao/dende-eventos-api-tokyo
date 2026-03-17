package br.com.softhouse.dende.model.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventoOrganizadorResponseDTO(
        String nome,
        LocalDateTime dataInicio,
        LocalDateTime dataFinal,
        BigDecimal precoIngresso,
        Integer capacidadeMaxima,
        String localAcesso
){}
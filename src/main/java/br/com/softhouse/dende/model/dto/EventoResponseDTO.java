package br.com.softhouse.dende.model.dto;

import java.time.LocalDateTime;

public record EventoResponseDTO (
        String nome,
        String descricao,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String nomeOrganizador
){}

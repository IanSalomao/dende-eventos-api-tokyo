package br.com.softhouse.dende.model.dto.response;

import br.com.softhouse.dende.model.enums.ModalidadeEvento;
import br.com.softhouse.dende.model.enums.TipoEvento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventoResponseDTO(
        String nome,
        String descricao,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String nomeOrganizador,
        TipoEvento tipo,
        ModalidadeEvento modalidade,
        Integer vagasDisponiveis,
        String localAcesso,
        BigDecimal precoIngresso
){}

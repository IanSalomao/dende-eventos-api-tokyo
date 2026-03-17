package br.com.softhouse.dende.model.dto.request;

import br.com.softhouse.dende.model.enums.ModalidadeEvento;
import br.com.softhouse.dende.model.enums.TipoEvento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CadastrarEventoRequestDto(
        String nome,
        String descricao,
        String paginaEvento,
        LocalDateTime dataInicio,
        LocalDateTime dataFinal,
        TipoEvento tipo,
        ModalidadeEvento modalidade,
        Integer capacidadeMaxima,
        String localAcesso,
        BigDecimal precoIngresso,
        Boolean permiteEstorno,
        BigDecimal taxaEstorno,
        Long eventoPrincipalId
) {
}

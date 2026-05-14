package br.com.softhouse.dende.mappers;

import br.com.softhouse.dende.builders.EventoBuilder;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.dto.request.CadastrarEventoRequestDto;
import br.com.softhouse.dende.model.dto.response.EventoResponseDTO;

public class EventoMapper {

    public static Evento toModel(CadastrarEventoRequestDto dto, Evento eventoPrincipal) {
        return new EventoBuilder()
                .nome(dto.nome())
                .descricao(dto.descricao())
                .paginaEvento(dto.paginaEvento())
                .dataInicio(dto.dataInicio())
                .dataFinal(dto.dataFinal())
                .tipo(dto.tipo())
                .modalidade(dto.modalidade())
                .capacidadeMaxima(dto.capacidadeMaxima())
                .localAcesso(dto.localAcesso())
                .precoIngresso(dto.precoIngresso())
                .permiteEstorno(dto.permiteEstorno())
                .taxaEstorno(dto.taxaEstorno())
                .eventoPrincipal(eventoPrincipal)
                .build();
    }

    public static EventoResponseDTO toResponse(Evento evento){
        String nomeOrganizador = (evento.getOrganizador() != null) ? evento.getOrganizador().getNome() : null;
        return new EventoResponseDTO(
                evento.getNome(),
                evento.getDescricao(),
                evento.getDataInicio(),
                evento.getDataFinal(),
                nomeOrganizador,
                evento.getTipo(),
                evento.getModalidade(),
                evento.getCapacidadeMaxima(),
                evento.getLocalAcesso(),
                evento.getPrecoIngresso()
        );
    }


}

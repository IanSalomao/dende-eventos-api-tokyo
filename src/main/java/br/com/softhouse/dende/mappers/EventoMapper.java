package br.com.softhouse.dende.mappers;

import br.com.softhouse.dende.builders.EventoBuilder;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.dto.request.CadastrarEventoRequestDto;
import br.com.softhouse.dende.model.dto.response.EventoResponseDTO;
import br.com.softhouse.dende.repositories.Repositorio;

public class EventoMapper {

    public static Evento toModel(CadastrarEventoRequestDto dto){
        Evento eventoPrincipal = null;
        if(dto.eventoPrincipalId() != null){
            eventoPrincipal = Repositorio.getInstance().buscarEventoPorId(dto.eventoPrincipalId());
        }

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
        return new EventoResponseDTO(
                evento.getNome(),
                evento.getDescricao(),
                evento.getDataInicio(),
                evento.getDataFinal(),
                evento.getOrganizador().getNome(),
                evento.getTipo(),
                evento.getModalidade(),
                evento.getCapacidadeMaxima(),
                evento.getLocalAcesso(),
                evento.getPrecoIngresso()
        );
    }


}

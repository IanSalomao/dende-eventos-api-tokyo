package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.request.GetMapping;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.dto.EventoResponseDTO;
import br.com.softhouse.dende.repositories.Repositorio;

import java.util.List;
@RequestMapping(path = "/eventos")
public class EventoController {

    private final Repositorio repositorio;

    public EventoController() {
        this.repositorio = Repositorio.getInstance();
    }

    // [AVALIAÇÃO - Item 9] O método retorna 200 OK para a listagem, o que é adequado para GET.
    // Porém, não há tratamento de exceção: se feedEventos() lançar qualquer erro, a exceção
    // escapará sem retorno HTTP adequado. Sugestão: envolva em try-catch retornando 500.
    // [AVALIAÇÃO - Item 4] O EventoResponseDTO retorna apenas nome, descricao, dataHoraInicio, dataHoraFim e nomeOrganizador.
    // Para um feed de eventos (US12), faltam campos essenciais como precoIngresso, localAcesso,
    // modalidade e capacidadeMaxima, que o usuário precisaria para decidir se deseja comprar o ingresso.
    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> feedEventos() {
        List<EventoResponseDTO> lista = repositorio.feedEventos()
                .stream()
                .map(evento -> new EventoResponseDTO(
                        evento.getNome(),
                        evento.getDescricao(),
                        evento.getDataInicio(),
                        evento.getDataFinal(),
                        evento.getOrganizador().getNome()
                ))
                .toList();

        return ResponseEntity.ok(lista);
    }
}

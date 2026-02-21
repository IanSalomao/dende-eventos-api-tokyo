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

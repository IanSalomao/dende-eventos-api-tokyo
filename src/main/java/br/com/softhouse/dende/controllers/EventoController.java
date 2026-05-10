package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.GetMapping;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.mappers.EventoMapper;
import br.com.softhouse.dende.model.dto.response.EventoResponseDTO;
import br.com.softhouse.dende.repositories.EventoRepositoryImpl;

import java.util.List;

@Controller
@RequestMapping(path = "/eventos")
public class EventoController {

    private final EventoRepositoryImpl eventoRepository;

    public EventoController() {
        this.eventoRepository = new EventoRepositoryImpl();
    }

    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> feedEventos() {
        List<EventoResponseDTO> lista = eventoRepository.findFeedPublico(null, null, null)
                .stream()
                .map(EventoMapper::toResponse)
                .toList();

        return ResponseEntity.ok(lista);
    }
}

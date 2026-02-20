package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.EventoOrganizadorDTO;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.Repositorio;

import java.util.List;


@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final Repositorio repositorio;

    public OrganizadorController() {
        this.repositorio = Repositorio.getInstance();
    }

    @PostMapping
    public ResponseEntity<String> cadastrarOrganizador(@RequestBody Organizador organizador) {
        try {
            repositorio.salvarOrganizador(organizador);
            return ResponseEntity.ok("Organizador " + organizador.getNome() + " cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PostMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<String> cadastrarEvento(
            @PathVariable(parameter = "organizadorId") String organizadorId,
            @RequestBody Evento evento
    ){
        Organizador organizador = repositorio.buscarOrganizador(organizadorId);
        if(organizador == null){return ResponseEntity.status(404, "Organizador nao encontrado");}
        try {
            organizador.cadastrarEvento(evento);
            return ResponseEntity.ok("Evento " + evento.getNome() + " de ID: " + evento.getId() + " cadastrado com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PutMapping(path = "/{organizadorId}/eventos/{eventoId}")
    public ResponseEntity<String> alterarEvento(@PathVariable(parameter = "eventoId") long eventoId,
                                                @PathVariable(parameter = "organizadorId") String organizadorId,
                                                @RequestBody Evento evento) {

        Organizador organizadorInformado = repositorio.buscarOrganizador(organizadorId);
        Evento eventoInformado = repositorio.buscarEventoPorId(eventoId);

        if(organizadorInformado == null){return ResponseEntity.status(404, "Organizador nao encontrado.");}
        if(eventoInformado == null){return ResponseEntity.status(404, "Evento nao encontrado.");}

        try{
            organizadorInformado.alterarEvento(eventoId, evento);
            return ResponseEntity.ok("Dados alterados com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @GetMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<?> listarMeusEventos(
            @PathVariable(parameter = "organizadorId") String organizadorId
    ){
        Organizador organizador = repositorio.buscarOrganizador(organizadorId);
        if(organizador == null){
            return ResponseEntity.status(404, "Organizador nao encontrado");
        }

        List<EventoOrganizadorDTO> meusEventos = organizador.listarMeusEventos();

        return ResponseEntity.ok(meusEventos);
    }
}
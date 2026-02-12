package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.enums.StatusEvento;
import br.com.softhouse.dende.repositories.Repositorio;

import java.util.List;
import java.util.Objects;


@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final Repositorio repositorio;

    public OrganizadorController() {
        this.repositorio = Repositorio.getInstance();
    }

    @PostMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<String> cadastrarEvento(
            @PathVariable(parameter = "organizadorId") String organizadorId,
            @RequestBody Evento evento
    ){
        Organizador organizador = repositorio.buscarOrganizador(organizadorId);
        if(organizador == null){return ResponseEntity.status(404, "Organizador não encontrado");}
        try {
            organizador.cadastrarEvento(evento);
            return ResponseEntity.ok("Evento cadastrado com sucesso.");
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
            return ResponseEntity.status(404, "Organizador não encontrado");
        }

        List<Evento> meusEventos = organizador.listarMeusEventos();

        return ResponseEntity.ok(meusEventos);
    }

    @PutMapping(path = "/organizadores/{organizadorId}/eventos/{{eventId}}")
    public ResponseEntity<String> alterarEvento(@PathVariable(parameter = "eventoId") long eventoId,
                                                @PathVariable(parameter = "organizadorId") String organizadorId,
                                                @RequestBody Evento evento) {

        Organizador organizadorInformado = repositorio.buscarOrganizador(organizadorId);
        Evento eventoInformado = repositorio.buscarEventoPorId(eventoId);

        if(organizadorInformado == null ||
                !Objects.equals(organizadorInformado.getEmail(), eventoInformado.getOrganizador().getEmail()) ||
            eventoInformado == null){
            return ResponseEntity.status(404, "Organizador e evento não correspondem");}
        try{
            organizadorInformado.alterarEvento(eventoId, evento);
            return ResponseEntity.ok("Dados alterados com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }


    }
}

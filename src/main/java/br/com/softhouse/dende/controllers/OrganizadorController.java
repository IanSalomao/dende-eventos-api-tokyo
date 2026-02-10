package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.ResultadoValidacao;
import br.com.softhouse.dende.repositories.Repositorio;


@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final Repositorio repositorio;

    public OrganizadorController() {
        this.repositorio = Repositorio.getInstance();
    }

    @GetMapping(path = "/{organizadorId}/eventos")
    public ResponseEntity<String> listarEventoOrganizador(
            @PathVariable(parameter = "organizadorId") String organizadorId,
            @RequestBody Evento evento
    ){
        Organizador organizador = repositorio.buscarOrganizador(organizadorId);
        ResultadoValidacao resultado = organizador.cadastrarEvento(evento);

        if (!resultado.isValido()){
            return ResponseEntity.status(400, resultado.getMensagem());
        }
        return ResponseEntity.status(200, resultado.getMensagem());
    }
}

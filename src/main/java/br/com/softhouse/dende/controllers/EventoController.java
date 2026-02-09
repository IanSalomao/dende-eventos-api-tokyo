package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.PostMapping;
import br.com.dende.softhouse.annotations.request.PutMapping;
import br.com.dende.softhouse.annotations.request.RequestBody;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.ResultadoValidacao;
import br.com.softhouse.dende.repositories.Repositorio;
import br.com.softhouse.dende.model.Evento;

@Controller
@RequestMapping(path = "/eventos")
public class EventoController {
    private final Repositorio repositorio;

    public EventoController() {
        this.repositorio = Repositorio.getInstance();
    }

    @PostMapping
    public ResponseEntity<String> cadastroEvento(@RequestBody Evento evento){
        ResultadoValidacao resultado = evento.validarDatas();

        if (!resultado.isValido()){
            return ResponseEntity.status(400,resultado.getMensagem());
        }

        repositorio.salvarEvento(evento);
        return ResponseEntity.ok("Evento cadastrado com sucesso. ID: " + evento.getId());
    }
}

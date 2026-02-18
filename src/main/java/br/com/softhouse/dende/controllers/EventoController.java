package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.softhouse.dende.repositories.Repositorio;

@Controller
@RequestMapping(path = "/eventos")
public class EventoController {
    private final Repositorio repositorio;

    public EventoController() {
        this.repositorio = Repositorio.getInstance();
    }
}

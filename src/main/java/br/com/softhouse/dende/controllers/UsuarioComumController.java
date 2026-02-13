package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.GetMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.annotations.request.PutMapping;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.repositories.Repositorio;

import java.util.List;

@Controller
@RequestMapping(path = "/usuario")
public class UsuarioComumController {
    private final Repositorio repositorio;

    public UsuarioComumController(){
        this.repositorio = Repositorio.getInstance();
    }

    @GetMapping(path = "/{usuarioId}/ingressos")
    public ResponseEntity<List<Ingresso>> listarIngressos( @PathVariable(parameter = "usuarioId") Long usuarioId) {
        UsuarioComum usuario = (UsuarioComum) repositorio.buscarUsuarioPorId(usuarioId);

        List<Ingresso> ingressosOrdenados = usuario.listarIngresso();

        return ResponseEntity.ok(ingressosOrdenados);
    }

    @PutMapping(path = "/{usuarioId}/eventos/{eventoId}/ingresso")
    public ResponseEntity<List<Ingresso>> comprarIngresso(
            @PathVariable(parameter = "usuarioId") Long usuarioId,
            @PathVariable(parameter = "eventoId") Long eventoId) {

        UsuarioComum usuario = (UsuarioComum) repositorio.buscarUsuarioPorId(usuarioId);

        Evento evento = repositorio.buscarEventoPorId(eventoId);

        List<Ingresso> ingressosGerados = usuario.comprarIngresso(evento);

        for (Ingresso ingresso : ingressosGerados) {
            repositorio.salvarIngresso(ingresso);
        }

        return ResponseEntity.ok(ingressosGerados);
    }


}

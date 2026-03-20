package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.GetMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.mappers.IngressoMapper;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.repositories.Repositorio;


@Controller
@RequestMapping(path = "/ingressos")
public class IngressoController {
    private final Repositorio repositorio;

    public IngressoController(){ this.repositorio = Repositorio.getInstance();}

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> buscarIngressoPorId(@PathVariable(parameter = "id") Long id) {
        try {
            Ingresso ingresso = repositorio.buscarIngressoPorId(id);
            return ResponseEntity.ok(IngressoMapper.toResponse(ingresso));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrado"))
                return ResponseEntity.status(404, e.getMessage());
            return ResponseEntity.status(400, e.getMessage());
        }
    }
}

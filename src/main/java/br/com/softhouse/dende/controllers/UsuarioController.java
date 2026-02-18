package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.PostMapping;
import br.com.dende.softhouse.annotations.request.PutMapping;
import br.com.dende.softhouse.annotations.request.RequestBody;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.enums.StatusEvento;
import br.com.softhouse.dende.repositories.Repositorio;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/usuarios")
public class UsuarioController {

    private final Repositorio repositorio;

    public UsuarioController() {
        this.repositorio = Repositorio.getInstance();
    }

    @PostMapping
    public ResponseEntity<String> cadastroUsuario(@RequestBody Usuario usuario){
        return ResponseEntity.ok("Usuario " + usuario.getEmail() + " registrado com sucesso!");
    }

    @PutMapping(path = "/{usuarioId}")
    public ResponseEntity<String> alterarUsuario(@PathVariable(parameter = "usuarioId") long usuarioId, @RequestBody Usuario usuario) {
        return ResponseEntity.ok("Usuario " + usuario.getEmail() + " do usuarioId = " + usuarioId + " alterado com sucesso!");
    }

    @RequestMapping(path = "/feed")
    public List<Evento> obterFeed(){
        return repositorio.buscarTodosEventos().stream()
                .filter(e -> e.getStatus() == StatusEvento.ATIVO)
                .filter(e -> e.getDataFinal().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Evento::getDataInicio).thenComparing(Evento::getNome))
                .collect(Collectors.toList());
    }
}

package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.GetMapping;
import br.com.dende.softhouse.annotations.request.PutMapping;
import br.com.dende.softhouse.annotations.request.RequestBody;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.UsuarioOrganizador;
import br.com.softhouse.dende.repositories.Repositorio;

@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final Repositorio repositorio;

    public OrganizadorController() {
        this.repositorio = Repositorio.getInstance();
    }

    @PutMapping(path = "/{email}")
    public ResponseEntity<String> alterarOrganizador(
            @PathVariable(parameter = "email") String email,
            @RequestBody UsuarioOrganizador dadosNovos) {
        if (!repositorio.existeUsuario(email)) {
            return ResponseEntity.status(404, "Organizador com e-mail " + email + " nao encontrado.");
        }
        repositorio.atualizarUsuario(email, dadosNovos);
        return ResponseEntity.ok("Organizador " + email + " alterado com sucesso!");
    }

    @GetMapping(path = "/{email}")
    public ResponseEntity<String> visualizarPerfil(@PathVariable(parameter = "email") String email) {
        return repositorio.buscarUsuario(email)
                .filter(usuario -> usuario instanceof UsuarioOrganizador)
                .map(usuario -> ResponseEntity.ok(usuario.visualizarPerfil()))
                .orElseGet(() -> ResponseEntity.status(404,"Organizador com e-mail " + email + " nao encontrado."));
    }
}
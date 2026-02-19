package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.PostMapping;
import br.com.dende.softhouse.annotations.request.PutMapping;
import br.com.dende.softhouse.annotations.request.PatchMapping;
import br.com.dende.softhouse.annotations.request.RequestBody;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.ReativacaoRequest;
import br.com.softhouse.dende.repositories.Repositorio;

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


    @PatchMapping(path = "/{email}/{status}")
    public ResponseEntity<String> alterarStatus(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "status") String status,
            @RequestBody(required = false) ReativacaoRequest body) {

        Usuario usuario = repositorio.buscarUsuarioQualquer(email);

        if (usuario == null) {
            return ResponseEntity.status(404, "Usuário não encontrado.");
        }

        try {
            if ("desativar".equalsIgnoreCase(status)) {
                if (!usuario.isAtivo()) {
                    throw new IllegalArgumentException("Usuário já está inativo.");
                }
                usuario.desativar();
                return ResponseEntity.ok("Usuário desativado com sucesso.");
            }
            else if ("reativar".equalsIgnoreCase(status)) {
                if (usuario.isAtivo()) {
                    throw new IllegalArgumentException("Usuário já está ativo.");
                }

                String senha = (body != null) ? body.getSenha() : null;
                usuario.reativar(senha);
                return ResponseEntity.ok("Usuário reativado com sucesso.");
            }

            return ResponseEntity.status(400, "Ação inválida.");
        } catch (Exception e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }
}
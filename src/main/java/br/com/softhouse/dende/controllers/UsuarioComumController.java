package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.dto.AlterarPerfilDTO;
import br.com.softhouse.dende.model.dto.ReativarUsuarioDTO;
import br.com.softhouse.dende.repositories.Repositorio;

@Controller
@RequestMapping(path = "/usuarios")
public class UsuarioComumController {

    private final Repositorio repositorio;

    public UsuarioComumController() {
        this.repositorio = Repositorio.getInstance();
    }

    @PostMapping
    public ResponseEntity<String> cadastrarUsuario(@RequestBody UsuarioComum usuarioComum) {
        try {
            repositorio.salvarUsuario(usuarioComum);
            return ResponseEntity.ok("Usuario " + usuarioComum.getEmail() + " cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @GetMapping(path = "/{email}")
    public ResponseEntity<?> visualizarPerfil(@PathVariable(parameter = "email") String email) {
        Usuario usuario = repositorio.buscarUsuario(email);
        if (usuario == null) return ResponseEntity.status(404, "Usuario nao encontrado.");
        return ResponseEntity.ok(usuario.visualizarPerfil());
    }

    @PutMapping(path = "/{email}")
    public ResponseEntity<String> alterarUsuario(
            @PathVariable(parameter = "email") String email,
            @RequestBody AlterarPerfilDTO dto) {
        Usuario usuario = repositorio.buscarUsuario(email);
        if (usuario == null) return ResponseEntity.status(404, "Usuario nao encontrado.");
        try {
            usuario.alterarPerfil(dto);
            return ResponseEntity.ok("Perfil de " + email + " atualizado com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PatchMapping(path = "/{email}/desativar")
    public ResponseEntity<String> desativarUsuario(@PathVariable(parameter = "email") String email) {
        Usuario usuario = repositorio.buscarUsuario(email);
        if (usuario == null) return ResponseEntity.status(404, "Usuario nao encontrado.");
        if (!usuario.isAtivo()) return ResponseEntity.status(400, "Usuario ja esta inativo.");
        try {
            usuario.desativarUsuario();
            return ResponseEntity.ok("Usuario desativado com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PatchMapping(path = "/{email}/reativar")
    public ResponseEntity<String> reativarUsuario(
            @PathVariable(parameter = "email") String email,
            @RequestBody ReativarUsuarioDTO body) {
        Usuario usuario = repositorio.buscarUsuario(email);
        if (usuario == null) return ResponseEntity.status(404, "Usuario nao encontrado.");
        if (usuario.isAtivo()) return ResponseEntity.status(400, "Usuario ja esta ativo.");
        try {
            String senha = (body != null) ? body.senha() : null;
            usuario.reativarUsuario(email, senha);
            return ResponseEntity.ok("Usuario reativado com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }
}
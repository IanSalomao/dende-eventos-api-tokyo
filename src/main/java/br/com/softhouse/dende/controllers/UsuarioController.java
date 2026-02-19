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


    @PutMapping(path = "/{email}/desativar") // collection usa PATCH para desativar/reativar.
    public ResponseEntity<String> desativarUsuario(@PathVariable(parameter = "email") String email) {
        Usuario usuario = repositorio.buscarUsuarioQualquer(email);
        /* Bom verificar se ele já não tá inativo
        if (!this.ativo) throw new IllegalArgumentException("Usuário já está inativo.");
         */
        if (usuario == null) return ResponseEntity.status(404, "Usuário não encontrado.");
        try {
            usuario.desativar();
            return ResponseEntity.ok("Usuário desativado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }


    @PutMapping(path = "/{email}/reativar")// collection usa PATCH para desativar/reativar.
    public ResponseEntity<String> reativarUsuario(
            @PathVariable(parameter = "email") String email,
            @RequestBody ReativacaoRequest body) {

        Usuario usuario = repositorio.buscarUsuarioQualquer(email);
        /* Bom verificar se usuário já não tá ativo
        if (this.ativo) throw new IllegalArgumentException("Usuário já está ativo.");
         */
        if (usuario == null) return ResponseEntity.status(404, "Usuário não encontrado.");

        try {
            String senha = (body != null) ? body.senha : null; //body.getSenha()
            usuario.reativar(senha);
            return ResponseEntity.ok("Usuário reativado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }
}
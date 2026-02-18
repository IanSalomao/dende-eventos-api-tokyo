package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.PostMapping;
import br.com.dende.softhouse.annotations.request.PutMapping;
import br.com.dende.softhouse.annotations.request.RequestBody;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.dto.AlterarPerfilDTO;
import br.com.softhouse.dende.model.enums.Sexo;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;
import java.util.Map;

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
    public ResponseEntity<String> alterarPerfil(
            @PathVariable(parameter = "usuarioId") Long usuarioId,
            @RequestBody AlterarPerfilDTO dto) {
        try {
            Usuario usuario = repositorio.buscarUsuarioPorId(usuarioId);
            usuario.alterarPerfil(dto.nome(), dto.dataNascimento(), dto.sexo(), dto.senha());
            return ResponseEntity.ok("Perfil alterado com sucesso!");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(404, e.getMessage());
            }
            return ResponseEntity.status(400, e.getMessage());
        }
    }
}

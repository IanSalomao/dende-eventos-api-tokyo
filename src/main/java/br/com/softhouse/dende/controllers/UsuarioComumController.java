package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.mappers.IngressoMapper;
import br.com.softhouse.dende.mappers.UsuarioComumMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.model.dto.AlterarPerfilComumDTO;
import br.com.softhouse.dende.model.dto.request.CadastrarUsuarioComumRequestDto;
import br.com.softhouse.dende.model.dto.response.IngressoResponseDTO;
import br.com.softhouse.dende.model.dto.ReativarUsuarioDTO;
import br.com.softhouse.dende.repositories.UsuarioRepositoryImpl;
import br.com.softhouse.dende.exceptions.EmailJaCadastradoException;
import br.com.softhouse.dende.exceptions.UsuarioNaoEncontradoException;
import java.util.List;

@Controller
@RequestMapping(path = "/usuarios")
public class UsuarioComumController {
    private final UsuarioRepositoryImpl usuarioRepository;

    public UsuarioComumController(){
        this.usuarioRepository = new UsuarioRepositoryImpl();
    }

    @PostMapping
    public ResponseEntity<String> cadastrarUsuario(@RequestBody CadastrarUsuarioComumRequestDto dto) {
        try {
            UsuarioComum usuario = UsuarioComumMapper.toModel(dto);
            usuarioRepository.save(usuario);
            return ResponseEntity.ok("Usuario " + usuario.getEmail() + " cadastrado com sucesso!");
        } catch (EmailJaCadastradoException e) {
            return ResponseEntity.status(409, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @GetMapping(path = "/{email}")
    public ResponseEntity<?> visualizarPerfil(@PathVariable(parameter = "email") String email) {
        try {
            Usuario usuario = usuarioRepository.findById(email);
            if (usuario == null || !(usuario instanceof UsuarioComum usuarioComum)) {
                throw new UsuarioNaoEncontradoException(email);
            }
            return ResponseEntity.ok(UsuarioComumMapper.toResponse(usuarioComum));
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        }
    }

    @PutMapping(path = "/{email}")
    public ResponseEntity<String> alterarUsuario(
            @PathVariable(parameter = "email") String email,
            @RequestBody AlterarPerfilComumDTO dto) {
        try {
            Usuario usuario = usuarioRepository.findById(email);
            if (usuario == null || !(usuario instanceof UsuarioComum usuarioComum)) {
                throw new UsuarioNaoEncontradoException(email);
            }
            usuarioComum.alterarPerfil(dto);
            usuarioRepository.update(usuarioComum);
            return ResponseEntity.ok("Perfil de " + email + " atualizado com sucesso.");
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PatchMapping(path = "/{email}/desativar")
    public ResponseEntity<String> desativarUsuario(@PathVariable(parameter = "email") String email) {
        try {
            Usuario usuario = usuarioRepository.findById(email);
            if (usuario == null || !(usuario instanceof UsuarioComum usuarioComum)) {
                throw new UsuarioNaoEncontradoException(email);
            }
            usuarioComum.desativarUsuario();
            usuarioRepository.update(usuarioComum);
            return ResponseEntity.ok("Usuario desativado com sucesso.");
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PatchMapping(path = "/{email}/reativar")
    public ResponseEntity<String> reativarUsuario(
            @PathVariable(parameter = "email") String email,
            @RequestBody ReativarUsuarioDTO body) {
        try {
            Usuario usuario = usuarioRepository.findById(email);
            if (usuario == null || !(usuario instanceof UsuarioComum usuarioComum)) {
                throw new UsuarioNaoEncontradoException(email);
            }
            String senha = (body != null) ? body.senha() : null;
            usuarioComum.reativarUsuario(email, senha);
            usuarioRepository.update(usuarioComum);
            return ResponseEntity.ok("Usuario reativado com sucesso.");
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }
}

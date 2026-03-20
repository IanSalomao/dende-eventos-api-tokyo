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
import br.com.softhouse.dende.repositories.Repositorio;

import java.util.List;

@Controller
@RequestMapping(path = "/usuarios")
public class UsuarioComumController {
    private final Repositorio repositorio;

    public UsuarioComumController(){
        this.repositorio = Repositorio.getInstance();
    }

    @PostMapping
    public ResponseEntity<String> cadastrarUsuario(@RequestBody CadastrarUsuarioComumRequestDto dto) {
        try {
            UsuarioComum usuario = UsuarioComumMapper.toModel(dto);
            repositorio.salvarUsuario(usuario);
            return ResponseEntity.ok("Usuario " + usuario.getEmail() + " cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @GetMapping(path = "/{email}")
    public ResponseEntity<?> visualizarPerfil(@PathVariable(parameter = "email") String email) {
        try {
            UsuarioComum usuario = repositorio.buscarUsuarioComum(email);
            return ResponseEntity.ok(UsuarioComumMapper.toResponse(usuario));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrado"))
                return ResponseEntity.status(404, e.getMessage());
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PutMapping(path = "/{email}")
    public ResponseEntity<String> alterarUsuario(
            @PathVariable(parameter = "email") String email,
            @RequestBody AlterarPerfilComumDTO dto) {
        UsuarioComum usuario = repositorio.buscarUsuarioComum(email);
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
        try {
            Usuario usuario = repositorio.buscarUsuarioComum(email);
            if (!usuario.isAtivo()) return ResponseEntity.status(400, "Usuario ja esta inativo.");
            usuario.desativarUsuario();
            return ResponseEntity.ok("Usuario desativado com sucesso.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrado"))
                return ResponseEntity.status(404, e.getMessage());
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PatchMapping(path = "/{email}/reativar")
    public ResponseEntity<String> reativarUsuario(
            @PathVariable(parameter = "email") String email,
            @RequestBody ReativarUsuarioDTO body) {
       try {
           Usuario usuario = repositorio.buscarUsuarioComum(email);
           if (usuario.isAtivo()) return ResponseEntity.status(400, "Usuario ja esta ativo.");
           String senha = (body != null) ? body.senha() : null;
            usuario.reativarUsuario(email, senha);
            return ResponseEntity.ok("Usuario reativado com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PostMapping(path = "/{email}/eventos/{eventoId}/ingressos")
    public ResponseEntity<?> comprarIngresso(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "eventoId") Long eventoId) {
        try {
            UsuarioComum usuario = repositorio.buscarUsuarioComum(email);
            Evento evento = repositorio.buscarEventoPorId(eventoId);

            List<Ingresso> ingressos = usuario.comprarIngresso(evento);
            ingressos.forEach(repositorio::salvarIngresso);

            return ResponseEntity.ok(IngressoMapper.toCompraResponse(ingressos));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(404, e.getMessage());
            }
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @GetMapping(path = "/{email}/ingressos")
    public ResponseEntity<?> listarIngressos(
            @PathVariable(parameter = "email") String email) {
        try {
            UsuarioComum usuario = repositorio.buscarUsuarioComum(email);
            List<IngressoResponseDTO> lista = usuario.listarIngressos()
                    .stream()
                    .map(IngressoMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(lista);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(404, e.getMessage());
            }
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PutMapping(path = "/{email}/ingressos/{ingressoId}/cancelar")
    public ResponseEntity<String> cancelarIngresso(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "ingressoId") Long ingressoId) {
        try {
            Ingresso ingresso = repositorio.buscarIngressoPorId(ingressoId);

            if (!ingresso.getUsuario().getEmail().equals(email)) {
                return ResponseEntity.status(403, "Ingresso não pertence a este usuário.");
            }

            double valorEstorno = ingresso.cancelarIngresso();

            String mensagem = "Ingresso cancelado com sucesso!";
            if (valorEstorno > 0) {
                mensagem += " Valor de estorno: R$ " + String.format("%.2f", valorEstorno);
            } else {
                mensagem += " Sem estorno disponível.";
            }

            return ResponseEntity.ok(mensagem);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(404, e.getMessage());
            }
            return ResponseEntity.status(400, e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(500, "ERRO: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }

}

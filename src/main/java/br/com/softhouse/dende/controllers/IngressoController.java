package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.mappers.IngressoMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.model.dto.response.IngressoResponseDTO;
import br.com.softhouse.dende.repositories.Repositorio;

import java.util.List;


@Controller
@RequestMapping(path = "/ingresso")
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


    @PostMapping(path = "/usuario/{email}/evento/{eventoId}")
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

    @GetMapping(path = "/{email}")
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

    @PutMapping(path = "/usuario/{email}/ingresso/{ingressoId}/cancelar")
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

package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.model.dto.CompraIngressoDTO;
import br.com.softhouse.dende.model.dto.IngressoDTO;import br.com.softhouse.dende.repositories.Repositorio;

import java.util.List;

@Controller
@RequestMapping(path = "/usuario")
public class UsuarioComumController {
    private final Repositorio repositorio;

    public UsuarioComumController(){
        this.repositorio = Repositorio.getInstance();
    }


    @GetMapping(path = "/{usuarioId}/ingressos")
    public ResponseEntity<?> listarIngressos(@PathVariable(parameter = "usuarioId") Long usuarioId) {
        try {
            UsuarioComum usuario = repositorio.buscarUsuarioComumPorId(usuarioId);
            List<IngressoDTO> lista = usuario.listarIngressos()
                    .stream()
                    .map(i -> new IngressoDTO(
                            i.getId(),
                            i.getEvento().getNome(),
                            i.getEvento().getDataHoraInicio(),
                            i.getValorPago(),
                            i.getStatus().name(),
                            i.getDataCompra()
                    ))
                    .toList();
            return ResponseEntity.ok(lista);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(404, e.getMessage());
            }
            return ResponseEntity.status(400, e.getMessage());
        }
    }


    @PutMapping(path = "/{usuarioId}/ingressos/{ingressoId}/cancelar")
    public ResponseEntity<String> cancelarIngresso(
            @PathVariable(parameter = "usuarioId") Long usuarioId,
            @PathVariable(parameter = "ingressoId") Long ingressoId) {
        try {
            Ingresso ingresso = repositorio.buscarIngressoPorId(ingressoId);

            if (!ingresso.getUsuario().getId().equals(usuarioId)) {
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
            return ResponseEntity.status(404, e.getMessage());
        }
    }


    @PostMapping(path = "/{usuarioId}/eventos/{eventoId}/ingresso")
    public ResponseEntity<?> comprarIngresso(
            @PathVariable(parameter = "usuarioId") Long usuarioId,
            @PathVariable(parameter = "eventoId") Long eventoId) {
        try {
            UsuarioComum usuario = (UsuarioComum) repositorio.buscarUsuarioPorId(usuarioId);
            Evento evento = repositorio.buscarEventoPorId(eventoId);

            CompraIngressoDTO compra = usuario.comprarIngresso(evento);

            compra.ingressos().forEach(repositorio::salvarIngresso);

            List<IngressoDTO> ingressosDTO = compra.ingressos()
                    .stream()
                    .map(i -> new IngressoDTO(
                            i.getId(),
                            i.getEvento().getNome(),
                            i.getEvento().getDataHoraInicio(),
                            i.getValorPago(),
                            i.getStatus().name(),
                            i.getDataCompra()
                    ))
                    .toList();

            return ResponseEntity.ok(
                    new Object() {
                        public final double valorTotal = compra.valorTotal();
                        public final List<IngressoDTO> ingressos = ingressosDTO;
                    }
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (ClassCastException e) {
            return ResponseEntity.status(400, "Usuário informado não é um usuário comum.");
        }
    }
}

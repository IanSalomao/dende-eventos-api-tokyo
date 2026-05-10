package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.exceptions.*;
import br.com.softhouse.dende.mappers.IngressoMapper;
import br.com.softhouse.dende.model.*;
import br.com.softhouse.dende.model.dto.response.IngressoResponseDTO;
import br.com.softhouse.dende.model.enums.StatusEvento;
import br.com.softhouse.dende.repositories.EventoRepositoryImpl;
import br.com.softhouse.dende.repositories.IngressoRepositoryImpl;
import br.com.softhouse.dende.repositories.UsuarioRepositoryImpl;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping(path = "/ingresso")
public class IngressoController {

    private final IngressoRepositoryImpl ingressoRepository;
    private final EventoRepositoryImpl eventoRepository;
    private final UsuarioRepositoryImpl usuarioRepository;

    public IngressoController() {
        this.eventoRepository = new EventoRepositoryImpl();
        this.usuarioRepository = new UsuarioRepositoryImpl();
        this.ingressoRepository = new IngressoRepositoryImpl(eventoRepository, usuarioRepository);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> buscarIngressoPorId(@PathVariable(parameter = "id") Long id) {
        try {
            Ingresso ingresso = ingressoRepository.findById(id);
            return ResponseEntity.ok(IngressoMapper.toResponse(ingresso));
        } catch (IngressoNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (OperacaoNaoPermitidaException e) {
            return ResponseEntity.status(500, e.getMessage());
        }
    }

    @PostMapping(path = "/usuario/{email}/evento/{eventoId}")
    public ResponseEntity<?> comprarIngresso(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "eventoId") Long eventoId) {
        try {
            Usuario usuario = usuarioRepository.findById(email);
            if (usuario == null)
                throw new UsuarioNaoEncontradoException(email);
            if (!(usuario instanceof UsuarioComum usuarioComum))
                throw new OperacaoNaoPermitidaException("O usuário informado não é um usuário comum.");

            Evento evento = eventoRepository.findById(eventoId);

            if (evento.getStatus() != StatusEvento.ATIVO)
                throw new OperacaoNaoPermitidaException("Evento não está ativo. Status atual: " + evento.getStatus());

            if (evento.getCapacidadeMaxima() != null) {
                int ativos = ingressoRepository.countAtivosByEvento(eventoId);
                if (ativos >= evento.getCapacidadeMaxima())
                    throw new CapacidadeExcedidaException(eventoId);
            }

            List<Ingresso> ingressos = usuarioComum.solicitarIngresso(evento);
            ingressoRepository.saveAll(ingressos);

            return ResponseEntity.ok(IngressoMapper.toCompraResponse(ingressos));
        } catch (EventoNaoEncontradoException | UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (CapacidadeExcedidaException | OperacaoNaoPermitidaException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500, "Erro interno: " + e.getMessage());
        }
    }

    @GetMapping(path = "/{email}")
    public ResponseEntity<?> listarIngressos(
            @PathVariable(parameter = "email") String email) {
        try {
            Usuario usuario = usuarioRepository.findById(email);
            if (usuario == null)
                throw new UsuarioNaoEncontradoException(email);
            if (!(usuario instanceof UsuarioComum))
                throw new OperacaoNaoPermitidaException("O usuário informado não é um usuário comum.");

            List<IngressoResponseDTO> lista = ingressoRepository.findByUsuario(email)
                    .stream()
                    .map(IngressoMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(lista);
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (OperacaoNaoPermitidaException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PutMapping(path = "/{ingressoId}/usuario/{email}/cancelar")
    public ResponseEntity<String> cancelarIngresso(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "ingressoId") Long ingressoId) {
        try {
            Ingresso ingresso = ingressoRepository.findById(ingressoId);

            if (!ingresso.getUsuario().getEmail().equals(email))
                return ResponseEntity.status(403, "Ingresso não pertence a este usuário.");

            BigDecimal valorEstorno = ingresso.cancelarIngresso();
            ingressoRepository.update(ingresso);

            String mensagem = "Ingresso cancelado com sucesso!";
            if (valorEstorno.compareTo(BigDecimal.ZERO) > 0) {
                mensagem += " Valor de estorno: R$ " + String.format("%.2f", valorEstorno);
            } else {
                mensagem += " Sem estorno disponível.";
            }
            return ResponseEntity.ok(mensagem);
        } catch (IngressoNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (OperacaoNaoPermitidaException e) {
            return ResponseEntity.status(500, e.getMessage());
        }
    }
}

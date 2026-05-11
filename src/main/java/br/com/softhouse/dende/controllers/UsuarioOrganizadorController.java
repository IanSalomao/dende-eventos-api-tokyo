package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.exceptions.*;
import br.com.softhouse.dende.mappers.EventoMapper;
import br.com.softhouse.dende.mappers.UsuarioOrganizadorMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioOrganizador;
import br.com.softhouse.dende.model.dto.AlterarPerfilOrganizadorDTO;
import br.com.softhouse.dende.model.dto.ReativarUsuarioDTO;
import br.com.softhouse.dende.model.dto.request.CadastrarEventoRequestDto;
import br.com.softhouse.dende.model.dto.request.CadastrarUsuarioOrganizadorRequestDto;
import br.com.softhouse.dende.model.dto.response.EventoOrganizadorResponseDTO;
import br.com.softhouse.dende.model.enums.StatusEvento;
import br.com.softhouse.dende.repositories.EventoRepositoryImpl;
import br.com.softhouse.dende.repositories.IngressoRepositoryImpl;
import br.com.softhouse.dende.repositories.UsuarioRepositoryImpl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/organizadores")
public class UsuarioOrganizadorController {

    private final UsuarioRepositoryImpl usuarioRepository;
    private final EventoRepositoryImpl eventoRepository;
    private final IngressoRepositoryImpl ingressoRepository;

    public UsuarioOrganizadorController() {
        this.usuarioRepository = new UsuarioRepositoryImpl();
        this.eventoRepository = new EventoRepositoryImpl();
        this.ingressoRepository = new IngressoRepositoryImpl(eventoRepository, usuarioRepository);
    }

    private UsuarioOrganizador buscarOrganizador(String email) {
        Usuario usuario = usuarioRepository.findById(email);
        if (usuario == null || !(usuario instanceof UsuarioOrganizador organizador)) {
            throw new UsuarioNaoEncontradoException(email);
        }
        return organizador;
    }

    @PostMapping
    public ResponseEntity<String> cadastrarOrganizador(@RequestBody CadastrarUsuarioOrganizadorRequestDto dto) {
        try {
            UsuarioOrganizador organizador = UsuarioOrganizadorMapper.toModel(dto);
            usuarioRepository.save(organizador);
            return ResponseEntity.ok("Organizador " + organizador.getEmail() + " cadastrado com sucesso!");
        } catch (EmailJaCadastradoException e) {
            return ResponseEntity.status(409, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @GetMapping(path = "/{email}")
    public ResponseEntity<?> visualizarPerfil(@PathVariable(parameter = "email") String email) {
        try {
            UsuarioOrganizador organizador = buscarOrganizador(email);
            return ResponseEntity.ok(UsuarioOrganizadorMapper.toResponse(organizador));
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        }
    }

    @PutMapping(path = "/{email}")
    public ResponseEntity<String> alterarOrganizador(
            @PathVariable(parameter = "email") String email,
            @RequestBody AlterarPerfilOrganizadorDTO dto) {
        try {
            UsuarioOrganizador organizador = buscarOrganizador(email);
            organizador.alterarPerfil(dto);
            usuarioRepository.update(organizador);
            return ResponseEntity.ok("Perfil de " + email + " atualizado com sucesso.");
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PatchMapping(path = "/{email}/desativar")
    public ResponseEntity<String> desativarOrganizador(@PathVariable(parameter = "email") String email) {
        try {
            UsuarioOrganizador organizador = buscarOrganizador(email);
            if (!organizador.isAtivo())
                return ResponseEntity.status(400, "Organizador ja esta inativo.");

            boolean temEventosAtivos = eventoRepository.findByOrganizador(email).stream()
                    .anyMatch(e -> e.getStatus() == StatusEvento.ATIVO);
            if (temEventosAtivos)
                throw new OperacaoNaoPermitidaException("Não é possível desativar a conta com eventos ativos.");

            organizador.desativarUsuario();
            usuarioRepository.update(organizador);
            return ResponseEntity.ok("Organizador desativado com sucesso.");
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (OperacaoNaoPermitidaException | IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PatchMapping(path = "/{email}/reativar")
    public ResponseEntity<String> reativarOrganizador(
            @PathVariable(parameter = "email") String email,
            @RequestBody ReativarUsuarioDTO body) {
        try {
            UsuarioOrganizador organizador = buscarOrganizador(email);
            if (organizador.isAtivo())
                return ResponseEntity.status(400, "Organizador ja esta ativo.");

            String senha = (body != null) ? body.senha() : null;
            organizador.reativarUsuario(email, senha);
            usuarioRepository.update(organizador);
            return ResponseEntity.ok("Organizador reativado com sucesso.");
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PostMapping(path = "/{email}/eventos")
    public ResponseEntity<String> cadastrarEvento(
            @PathVariable(parameter = "email") String email,
            @RequestBody CadastrarEventoRequestDto dto) {
        try {
            UsuarioOrganizador organizador = buscarOrganizador(email);
            if (!organizador.isAtivo())
                return ResponseEntity.status(400, "Organizador inativo nao pode cadastrar eventos.");

            Evento eventoPrincipal = null;
            if (dto.eventoPrincipalId() != null) {
                eventoPrincipal = eventoRepository.findById(dto.eventoPrincipalId());
            }

            Evento evento = EventoMapper.toModel(dto, eventoPrincipal);

            evento.validarInvariantes();
            organizador.cadastrarEvento(evento);

            eventoRepository.save(evento);

            return ResponseEntity.ok("Evento '" + evento.getNome() + "' cadastrado com sucesso. ID: " + evento.getId());
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PutMapping(path = "/{email}/eventos/{eventoId}")
    public ResponseEntity<String> alterarEvento(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "eventoId") Long eventoId,
            @RequestBody Evento novosDados) {
        try {
            buscarOrganizador(email); // Valida se organizador existe
            Evento evento = eventoRepository.findById(eventoId);

            if (!evento.getOrganizador().getEmail().equals(email)) {
                return ResponseEntity.status(403, "Voce nao tem permissao para alterar este evento.");
            }

            evento.alterarDados(novosDados);
            eventoRepository.update(evento);
            return ResponseEntity.ok("Evento alterado com sucesso.");

        } catch (UsuarioNaoEncontradoException | EventoNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @GetMapping(path = "/{email}/eventos")
    public ResponseEntity<?> listarEventos(@PathVariable(parameter = "email") String email) {
        try {
            buscarOrganizador(email);
            // Agora os eventos não vêm mais da memória do organizador, mas da tabela de Eventos!
            List<EventoOrganizadorResponseDTO> resultado = eventoRepository.findByOrganizador(email).stream()
                    .sorted(Comparator.comparing(Evento::getDataInicio)
                            .thenComparing(Evento::getNome, String.CASE_INSENSITIVE_ORDER))
                    .map(UsuarioOrganizadorMapper::toListarEventoOrganizadorDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(resultado);
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        }
    }

    @PatchMapping(path = "/{email}/eventos/{eventoId}/{status}")
    public ResponseEntity<String> alterarStatusEvento(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "eventoId") Long eventoId,
            @PathVariable(parameter = "status") String status) {
        try {
            buscarOrganizador(email);
            Evento evento = eventoRepository.findById(eventoId);

            if (!evento.getOrganizador().getEmail().equals(email)) {
                return ResponseEntity.status(403, "Voce nao tem permissao para alterar este evento.");
            }

            switch (status.toLowerCase()) {
                case "ativar" -> {
                    evento.ativarEvento();
                    eventoRepository.update(evento);
                    return ResponseEntity.ok("Evento ativado com sucesso!");
                }
                case "desativar" -> {
                    evento.desativarEvento();
                    ingressoRepository.cancelarAtivosByEvento(eventoId);
                    eventoRepository.update(evento);
                    return ResponseEntity.ok("Evento desativado com sucesso! Ingressos ativos foram cancelados.");
                }
                case "cancelar" -> {
                    evento.cancelarEvento();
                    ingressoRepository.cancelarAtivosByEvento(eventoId);
                    eventoRepository.update(evento);
                    return ResponseEntity.ok("Evento cancelado com sucesso! Ingressos ativos foram cancelados.");
                }
                case "encerrar" -> {
                    evento.encerrarEvento();
                    eventoRepository.update(evento);
                    return ResponseEntity.ok("Evento encerrado com sucesso!");
                }
                default -> throw new OperacaoNaoPermitidaException("Status invalido. Use 'ativar', 'desativar', 'cancelar' ou 'encerrar'.");
            }
        } catch (UsuarioNaoEncontradoException | EventoNaoEncontradoException e) {
            return ResponseEntity.status(404, e.getMessage());
        } catch (OperacaoNaoPermitidaException | IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }
}
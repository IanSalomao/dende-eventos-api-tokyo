package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.model.UsuarioOrganizador;
import br.com.softhouse.dende.model.dto.AlterarPerfilOrganizadorDTO;
import br.com.softhouse.dende.model.dto.ListarEventoOrganizadorDTO;
import br.com.softhouse.dende.model.dto.ReativarUsuarioDTO;
import br.com.softhouse.dende.repositories.Repositorio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping(path = "/organizadores")
public class UsuarioOrganizadorController {

    private final Repositorio repositorio;

    public UsuarioOrganizadorController() {
        this.repositorio = Repositorio.getInstance();
    }

    @PostMapping
    public ResponseEntity<String> cadastrarOrganizador(@RequestBody UsuarioOrganizador usuarioOrganizador) {
        try {
            System.out.println(usuarioOrganizador);

            repositorio.salvarUsuario(usuarioOrganizador);
            return ResponseEntity.ok("Organizador " + usuarioOrganizador.getEmail() + " cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }


    @GetMapping(path = "/{email}")
    public ResponseEntity<?> visualizarPerfil(@PathVariable(parameter = "email") String email) {
        UsuarioOrganizador organizador = repositorio.buscarOrganizador(email);
        if (organizador == null) return ResponseEntity.status(404, "Organizador nao encontrado.");
        return ResponseEntity.ok(organizador.visualizarPerfil());
    }

    @PutMapping(path = "/{email}")
    public ResponseEntity<String> alterarOrganizador(
            @PathVariable(parameter = "email") String email,
            @RequestBody AlterarPerfilOrganizadorDTO dto) {
        UsuarioOrganizador organizador = repositorio.buscarOrganizador(email);
        if (organizador == null) return ResponseEntity.status(404, "Organizador nao encontrado.");
        try {
            organizador.alterarPerfil(dto);
            return ResponseEntity.ok("Perfil de " + email + " atualizado com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PatchMapping(path = "/{email}/desativar")
    public ResponseEntity<String> desativarOrganizador(@PathVariable(parameter = "email") String email) {
        UsuarioOrganizador organizador = repositorio.buscarOrganizador(email);
        if (organizador == null) return ResponseEntity.status(404, "Organizador nao encontrado.");
        if (!organizador.isAtivo()) return ResponseEntity.status(400, "Organizador ja esta inativo.");
        try {
            organizador.desativarUsuario();
            return ResponseEntity.ok("Organizador desativado com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PatchMapping(path = "/{email}/reativar")
    public ResponseEntity<String> reativarOrganizador(
            @PathVariable(parameter = "email") String email,
            @RequestBody ReativarUsuarioDTO body) {
        UsuarioOrganizador organizador = repositorio.buscarOrganizador(email);
        if (organizador == null) return ResponseEntity.status(404, "Organizador nao encontrado.");
        if (organizador.isAtivo()) return ResponseEntity.status(400, "Organizador ja esta ativo.");
        try {
            String senha = (body != null) ? body.senha() : null;
            organizador.reativarUsuario(email, senha);
            return ResponseEntity.ok("Organizador reativado com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PostMapping(path = "/{email}/eventos")
    public ResponseEntity<String> cadastrarEvento(
            @PathVariable(parameter = "email") String email,
            @RequestBody Evento evento) {
        UsuarioOrganizador organizador = repositorio.buscarOrganizador(email);
        if (organizador == null) return ResponseEntity.status(404, "Organizador nao encontrado.");
        if (!organizador.isAtivo()) return ResponseEntity.status(400, "Organizador inativo nao pode cadastrar eventos.");
        try {
            evento.validarInvariantes();
            organizador.cadastrarEvento(evento);
            repositorio.salvarEvento(evento);
            return ResponseEntity.ok("Evento '" + evento.getNome() + "' cadastrado com sucesso. ID: " + evento.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PutMapping(path = "/{email}/eventos/{eventoId}")
    public ResponseEntity<String> alterarEvento(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "eventoId") long eventoId,
            @RequestBody Evento novosDados) {
        UsuarioOrganizador organizador = repositorio.buscarOrganizador(email);
        if (organizador == null) return ResponseEntity.status(404, "Organizador nao encontrado.");
        try {
            repositorio.buscarEventoPorId(eventoId);
            organizador.alterarEvento(eventoId, novosDados);
            return ResponseEntity.ok("Evento alterado com sucesso.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrado")) return ResponseEntity.status(404, e.getMessage());
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @GetMapping(path = "/{email}/eventos")
    public ResponseEntity<?> listarEventos(@PathVariable(parameter = "email") String email) {
        UsuarioOrganizador organizador = repositorio.buscarOrganizador(email);
        if (organizador == null) return ResponseEntity.status(404, "Organizador nao encontrado.");

        List<ListarEventoOrganizadorDTO> resultado = organizador.listarMeusEventos().stream()
                .map(e -> new ListarEventoOrganizadorDTO(
                        e.getNome(),
                        e.getDataInicio(),
                        e.getDataFinal(),
                        e.getPrecoIngresso(),
                        e.getCapacidadeMaxima(),
                        e.getLocalAcesso()
                ))
                .sorted(java.util.Comparator
                        .comparing(ListarEventoOrganizadorDTO::dataInicio)
                        .thenComparing(ListarEventoOrganizadorDTO::nome, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }


    @PatchMapping(path = "/{email}/eventos/{eventoId}/{status}")
    public ResponseEntity<String> alterarStatusEvento(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "eventoId") Long eventoId,
            @PathVariable(parameter = "status") String status) {
        try {
            UsuarioOrganizador organizador = repositorio.buscarOrganizadorPorEmail(email);
            Evento evento = repositorio.buscarEventoPorId(eventoId);

            if (!evento.getOrganizador().getEmail().equals(email)) {
                return ResponseEntity.status(403, "Voce nao tem permissao para alterar este evento.");
            }

            switch (status.toLowerCase()) {
                case "ativar" -> evento.ativarEvento();
                case "desativar" -> {
                    Map<UsuarioComum, BigDecimal> estornos = evento.desativarEvento();
                    estornos.forEach((usuario, valor) ->
                            System.out.println("Estorno de R$ " + valor + " para " + usuario.getEmail())
                    );
                }

                default -> throw new IllegalArgumentException("Status invalido. Use 'ativar' ou 'desativar'.");
            }

            return ResponseEntity.ok("Evento " + status + " com sucesso!");

        } catch (IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrado")) return ResponseEntity.status(404, e.getMessage());
            return ResponseEntity.status(400, e.getMessage());
        }
    }

}

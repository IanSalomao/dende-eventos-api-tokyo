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

    // [AVALIAÇÃO - Item 9] O método retorna 200 OK para criação de recurso.
    // Para operações de criação (POST), o status correto é 201 Created.
    // Código sugerido: return ResponseEntity.status(201, "Organizador " + usuarioOrganizador.getEmail() + " cadastrado com sucesso!");
    @PostMapping
    public ResponseEntity<String> cadastrarOrganizador(@RequestBody UsuarioOrganizador usuarioOrganizador) {
        try {
            // [AVALIAÇÃO] System.out.println() deixado no código de produção é um code smell.
            // Sugestão: remova esta linha ou substitua por um logger adequado (ex: Logger do java.util.logging ou SLF4J).
            System.out.println(usuarioOrganizador);

            repositorio.salvarUsuario(usuarioOrganizador);
            return ResponseEntity.ok("Organizador " + usuarioOrganizador.getEmail() + " cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }


    // [AVALIAÇÃO - Item 8] O método retorna organizador.visualizarPerfil(), que é uma String formatada.
    // Uma API REST deveria retornar objetos estruturados (DTO/JSON), não Strings concatenadas manualmente.
    // Sugestão: crie um OrganizadorPerfilDTO com os campos necessários e retorne ResponseEntity.ok(dto).
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

    // [AVALIAÇÃO - Item 9] Quando o organizador já está inativo, retorna 400 (Bad Request).
    // Um conflito de estado seria melhor representado por 409 (Conflict).
    // Código sugerido: return ResponseEntity.status(409, "Organizador ja esta inativo.");
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

    // [AVALIAÇÃO - Item 9] O método retorna 200 OK para criação de evento.
    // Para operações de criação (POST), o status correto é 201 Created.
    // Código sugerido: return ResponseEntity.status(201, "Evento '" + evento.getNome() + "' cadastrado com sucesso. ID: " + evento.getId());
    @PostMapping(path = "/{email}/eventos")
    public ResponseEntity<String> cadastrarEvento(
            @PathVariable(parameter = "email") String email,
            @RequestBody Evento evento) {
        UsuarioOrganizador organizador = repositorio.buscarOrganizador(email);
        if (organizador == null) return ResponseEntity.status(404, "Organizador nao encontrado.");
        if (!organizador.isAtivo()) return ResponseEntity.status(400, "Organizador inativo nao pode cadastrar eventos.");
        try {
            // [AVALIAÇÃO - Item 6] A chamada a evento.validarInvariantes() no controller é redundante,
            // pois o construtor de Evento já chama validarInvariantes() internamente.
            // A validação de regras de negócio deve ficar centralizada na entidade, não no controller.
            // Sugestão: remova esta linha; a validação já ocorre na criação do objeto.
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


    // [AVALIAÇÃO - Item 9] A ação (ativar/desativar) é passada como variável de path ({status}).
    // Uma abordagem mais RESTful seria ter dois endpoints separados:
    // @PatchMapping("/{email}/eventos/{eventoId}/ativar") e @PatchMapping("/{email}/eventos/{eventoId}/desativar")
    // ou usar um request body com o novo status desejado.
    // [AVALIAÇÃO - Item 1] O nome do parâmetro 'status' não deixa claro que é uma ação (verbo).
    // Sugestão: renomeie para 'acao' para deixar explícito que é um comando.
    // Código sugerido: @PathVariable(parameter = "acao") String acao
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
                    // [AVALIAÇÃO] Os estornos são apenas impressos no console com System.out.println.
                    // Numa aplicação real, os estornos deveriam ser processados (ex: notificar usuários,
                    // registrar em banco de dados). Deixar apenas um System.out.println é código incompleto.
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

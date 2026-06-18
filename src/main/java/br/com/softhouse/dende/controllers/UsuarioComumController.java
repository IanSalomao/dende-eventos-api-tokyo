package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.model.dto.AlterarPerfilComumDTO;
import br.com.softhouse.dende.model.dto.CompraIngressoDTO;
import br.com.softhouse.dende.model.dto.IngressoDTO;
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

    // [AVALIAÇÃO - Item 9] O método retorna 200 OK para criação de recurso.
    // Para operações de criação (POST), o status correto é 201 Created.
    // Código sugerido: return ResponseEntity.status(201, "Usuario " + usuarioComum.getEmail() + " cadastrado com sucesso!");
    @PostMapping
    public ResponseEntity<String> cadastrarUsuario(@RequestBody UsuarioComum usuarioComum) {
        try {
            repositorio.salvarUsuario(usuarioComum);
            return ResponseEntity.ok("Usuario " + usuarioComum.getEmail() + " cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    // [AVALIAÇÃO - Item 8] O método retorna usuario.visualizarPerfil() que é uma String formatada.
    // Uma API REST deveria retornar objetos estruturados (DTO/JSON), não Strings concatenadas.
    // Sugestão: crie um UsuarioPerfilDTO e retorne ResponseEntity.ok(perfilDTO).
    @GetMapping(path = "/{email}")
    public ResponseEntity<?> visualizarPerfil(@PathVariable(parameter = "email") String email) {
        Usuario usuario = repositorio.buscarUsuarioComum(email);
        if (usuario == null) return ResponseEntity.status(404, "Usuario nao encontrado.");
        return ResponseEntity.ok(usuario.visualizarPerfil());
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

    // [AVALIAÇÃO - Item 9] Quando o usuário já está inativo, retorna 400 (Bad Request).
    // Um conflito de estado seria melhor representado por 409 (Conflict).
    // Código sugerido: return ResponseEntity.status(409, "Usuario ja esta inativo.");
    @PatchMapping(path = "/{email}/desativar")
    public ResponseEntity<String> desativarUsuario(@PathVariable(parameter = "email") String email) {
        Usuario usuario = repositorio.buscarUsuarioComum(email);
        if (usuario == null) return ResponseEntity.status(404, "Usuario nao encontrado.");
        if (!usuario.isAtivo()) return ResponseEntity.status(400, "Usuario ja esta inativo.");
        try {
            usuario.desativarUsuario();
            return ResponseEntity.ok("Usuario desativado com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @PatchMapping(path = "/{email}/reativar")
    public ResponseEntity<String> reativarUsuario(
            @PathVariable(parameter = "email") String email,
            @RequestBody ReativarUsuarioDTO body) {
        Usuario usuario = repositorio.buscarUsuarioComum(email);
        if (usuario == null) return ResponseEntity.status(404, "Usuario nao encontrado.");
        if (usuario.isAtivo()) return ResponseEntity.status(400, "Usuario ja esta ativo.");
        try {
            String senha = (body != null) ? body.senha() : null;
            usuario.reativarUsuario(email, senha);
            return ResponseEntity.ok("Usuario reativado com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    // [AVALIAÇÃO - Item 9] O método retorna 200 OK para criação de ingresso.
    // Para operações de criação (POST), o status correto é 201 Created.
    // [AVALIAÇÃO - Item 4] A resposta usa uma classe anônima (new Object() {...}) para montar o JSON.
    // Isso é um code smell. Crie um DTO nomeado para a resposta da compra, ex: CompraResponseDTO.
    // [AVALIAÇÃO - Item 14] O campo 'valorTotal' da classe anônima é 'double'. Use BigDecimal.
    @PostMapping(path = "/{email}/eventos/{eventoId}/ingressos")
    public ResponseEntity<?> comprarIngresso(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "eventoId") Long eventoId) {
        try {
            UsuarioComum usuario = repositorio.buscarUsuarioComumPorEmail(email);
            Evento evento = repositorio.buscarEventoPorId(eventoId);

            CompraIngressoDTO compra = usuario.comprarIngresso(evento);
            compra.ingressos().forEach(repositorio::salvarIngresso);

            List<IngressoDTO> ingressosDTO = compra.ingressos()
                    .stream()
                    .map(i -> new IngressoDTO(
                            i.getId(),
                            i.getEvento().getNome(),
                            i.getEvento().getDataInicio(),
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
            UsuarioComum usuario = repositorio.buscarUsuarioComumPorEmail(email);
            List<IngressoDTO> lista = usuario.listarIngressos()
                    .stream()
                    .map(i -> new IngressoDTO(
                            i.getId(),
                            i.getEvento().getNome(),
                            i.getEvento().getDataInicio(),
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

    // [AVALIAÇÃO - Item 9] O método usa @PutMapping para uma operação de cancelamento parcial de recurso.
    // PUT substitui o recurso inteiro. Para atualização parcial de estado, use @PatchMapping.
    // Código sugerido: @PatchMapping(path = "/{email}/ingressos/{ingressoId}/cancelar")
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

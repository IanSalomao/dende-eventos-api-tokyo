package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.PatchMapping;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.Repositorio;


@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {
    private final Repositorio repositorio;

    public OrganizadorController(){
        this.repositorio = Repositorio.getInstance();
    }


    @PatchMapping(path = "/{email}/eventos/{eventoId}/{status}")
    public ResponseEntity<String> alterarStatusEvento(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "eventoId") Long eventoId,
            @PathVariable(parameter = "status") String status) {
        try {
            Organizador organizador = repositorio.buscarOrganizadorPorEmail(email);
            Evento evento = repositorio.buscarEventoPorId(eventoId);

            if (!evento.getOrganizador().getEmail().equals(email)) {
                return ResponseEntity.status(403, "Você não tem permissão para alterar este evento.");
            }

            switch (status.toLowerCase()) {
                case "ativar" -> evento.ativarEvento();
                case "desativar" -> evento.desativarEvento();
                default -> throw new IllegalArgumentException("Status inválido. Use 'ativar' ou 'desativar'.");
            }

            return ResponseEntity.ok("Evento " + status + " com sucesso!");

        } catch (IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(404, e.getMessage());
            }
            return ResponseEntity.status(400, e.getMessage());
        }
    }

}

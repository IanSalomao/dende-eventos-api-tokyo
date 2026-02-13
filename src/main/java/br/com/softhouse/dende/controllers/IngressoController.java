package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.PathVariable;
import br.com.dende.softhouse.annotations.request.PutMapping;
import br.com.dende.softhouse.annotations.request.RequestMapping;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.repositories.Repositorio;

@Controller
@RequestMapping(path = "/ingresso")
public class IngressoController {

    private final Repositorio repositorio;

    public IngressoController(){
        this.repositorio = Repositorio.getInstance();
    }

    @PutMapping(path = "/{id}/cancelar")
    public ResponseEntity<String> cancelarIngresso(@PathVariable(parameter = "id") long id){
        try{
            Ingresso ingresso = repositorio.buscarIngressoPorId(id);

            if (!ingresso.getUsuario().getId().equals(usuari
                    Id)) {
                return ResponseEntity.status(403, "Ingresso não pertence ao usuário.");
            }

            double valorEstorno = ingresso.cancelarIngresso();
            return ResponseEntity.status(200, "Ingresso cancelado. Valor de estorno R$ " + valorEstorno);

        } catch (IllegalArgumentException e){
            return ResponseEntity.status(404, e.getMessage());
        }
    }
}

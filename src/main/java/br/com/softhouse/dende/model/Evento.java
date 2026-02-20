package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.enums.StatusEvento;
import br.com.softhouse.dende.model.enums.StatusIngresso;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evento {

    private StatusEvento status;
    private List<Ingresso> ingressos = new ArrayList<>();


    public void ativarEvento() {
        if (this.status == StatusEvento.ATIVO) {
            throw new IllegalStateException("Evento já está ativo.");
        }
        validarDatas(this.dataInicio, this.dataFinal);
        this.status = StatusEvento.ATIVO;
    }

    public void desativarEvento() {
        if (this.status != StatusEvento.ATIVO) {
            throw new IllegalStateException("Evento não está ativo.");
        }

        this.status = StatusEvento.INATIVO;

        Map<UsuarioComum, BigDecimal> estornos = new HashMap<>();

        for (Ingresso ingresso : ingressos) {
            if (ingresso.getStatus() == StatusIngresso.ATIVO) {
                estornos.put(ingresso.getUsuario(), ingresso.getValorPago());
                ingresso.cancelarPorEvento();
            }
        }
    }
}

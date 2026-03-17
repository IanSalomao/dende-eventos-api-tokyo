package br.com.softhouse.dende.builders;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.enums.ModalidadeEvento;
import br.com.softhouse.dende.model.enums.TipoEvento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventoBuilder {

    private String nome;
    private String descricao;
    private String paginaEvento;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFinal;
    private TipoEvento tipo;
    private ModalidadeEvento modalidade;
    private Integer capacidadeMaxima;
    private String localAcesso;
    private BigDecimal precoIngresso;
    private Boolean permiteEstorno;
    private BigDecimal taxaEstorno;
    private Evento eventoPrincipal;

    public EventoBuilder nome(String nome) {
        this.nome = nome;
        return this;
    }

    public EventoBuilder descricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public EventoBuilder paginaEvento(String paginaEvento) {
        this.paginaEvento = paginaEvento;
        return this;
    }

    public EventoBuilder dataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
        return this;
    }

    public EventoBuilder dataFinal(LocalDateTime dataFinal) {
        this.dataFinal = dataFinal;
        return this;
    }

    public EventoBuilder tipo(TipoEvento tipo) {
        this.tipo = tipo;
        return this;
    }

    public EventoBuilder modalidade(ModalidadeEvento modalidade) {
        this.modalidade = modalidade;
        return this;
    }

    public EventoBuilder capacidadeMaxima(Integer capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
        return this;
    }

    public EventoBuilder localAcesso(String localAcesso) {
        this.localAcesso = localAcesso;
        return this;
    }

    public EventoBuilder precoIngresso(BigDecimal precoIngresso) {
        this.precoIngresso = precoIngresso;
        return this;
    }

    public EventoBuilder permiteEstorno(Boolean permiteEstorno) {
        this.permiteEstorno = permiteEstorno;
        return this;
    }

    public EventoBuilder taxaEstorno(BigDecimal taxaEstorno) {
        this.taxaEstorno = taxaEstorno;
        return this;
    }

    public EventoBuilder eventoPrincipal(Evento eventoPrincipal) {
        this.eventoPrincipal = eventoPrincipal;
        return this;
    }

    public Evento build() {
        return new Evento(
                nome, descricao, paginaEvento,
                dataInicio, dataFinal,
                tipo, modalidade,
                capacidadeMaxima, localAcesso,
                precoIngresso, permiteEstorno, taxaEstorno,
                eventoPrincipal
        );
    }
}
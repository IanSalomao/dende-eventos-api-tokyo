package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.enums.ModalidadeEvento;
import br.com.softhouse.dende.model.enums.StatusEvento;
import br.com.softhouse.dende.model.enums.TipoEvento;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public class Evento {
    private long id;
    private String nome;
    private String descricao;
    private String paginaEvento;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private TipoEvento tipo;
    private ModalidadeEvento modalidade;
    private Integer capacidadeMaxima;
    private String localAcesso;
    private StatusEvento status;
    private BigDecimal precoIngresso;
    private Boolean permiteEstorno;
    private BigDecimal taxaEstorno;
    private Evento eventoPrincipal;

    public Evento(
            final String nome,
            final String descricao,
            final String paginaEvento,
            final LocalDateTime dataHoraInicio,
            final LocalDateTime dataHoraFim,
            final TipoEvento tipo,
            final ModalidadeEvento modalidade,
            final Integer capacidadeMaxima,
            final String localAcesso,
            final StatusEvento status,
            final BigDecimal precoIngresso,
            final Boolean permiteEstorno,
            final BigDecimal taxaEstorno,
            final Evento eventoPrincipal
    ) {
        this.nome = nome;
        this.descricao = descricao;
        this.paginaEvento = paginaEvento;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.tipo = tipo;
        this.modalidade = modalidade;
        this.capacidadeMaxima = capacidadeMaxima;
        this.localAcesso = localAcesso;
        this.status = status;
        this.precoIngresso = precoIngresso;
        this.permiteEstorno = permiteEstorno;
        this.taxaEstorno = taxaEstorno;
        this.eventoPrincipal = eventoPrincipal;
    }

    public Evento() {
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public long getId() {
        return id;
    }

    public String getPaginaEvento() {
        return paginaEvento;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public ModalidadeEvento getModalidade() {
        return modalidade;
    }

    public Integer getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public String getLocalAcesso() {
        return localAcesso;
    }

    public StatusEvento getStatus() {
        return status;
    }

    public BigDecimal getPrecoIngresso() {
        return precoIngresso;
    }

    public Boolean isPermiteEstorno() {
        return permiteEstorno;
    }

    public BigDecimal getTaxaEstorno() {
        return taxaEstorno;
    }

    public Evento getEventoPrincipal() {
        return eventoPrincipal;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPaginaEvento(String paginaEvento) {
        this.paginaEvento = paginaEvento;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public void setTipo(TipoEvento tipo) {
        this.tipo = tipo;
    }

    public void setModalidade(ModalidadeEvento modalidade) {
        this.modalidade = modalidade;
    }

    public void setCapacidadeMaxima(Integer capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public void setLocalAcesso(String endereco) {
        this.localAcesso = endereco;
    }

    public void setPrecoIngresso(BigDecimal precoIngresso) {
        this.precoIngresso = precoIngresso;
    }

    public void setPermiteEstorno(Boolean permiteEstorno) {
        this.permiteEstorno = permiteEstorno;
    }

    public void setTaxaEstorno(BigDecimal taxaEstorno) {
        this.taxaEstorno = taxaEstorno;
    }

    public void setEventoPrincipal(Evento eventoPrincipal) {
        this.eventoPrincipal = eventoPrincipal;
    }

    public void atribuirId(final long id){
        if(this.id == 0){
            this.id = id;
        }
    }

    public ResultadoValidacao validarDatas() {
        if (this.dataHoraInicio == null || this.dataHoraFim == null){
            return new ResultadoValidacao(false, "Datas e Horários não podem ser nulos.");
        }
        if (this.dataHoraFim.isBefore(this.dataHoraInicio)){
            return new ResultadoValidacao(false, "Data final não pode ser antes da data inicial.");
        }
        if (this.dataHoraInicio.isAfter(this.dataHoraFim)){
            return new ResultadoValidacao(false, "Data de início não pode ser posterior a data final.");
        }

        if (Duration.between(this.dataHoraInicio, this.dataHoraFim).toMinutes() < 30){
            return new ResultadoValidacao(false, "Evento não pode durar menos de 30 min.");
        }

        return new ResultadoValidacao(true, "Intervalo válido.");
    }
}



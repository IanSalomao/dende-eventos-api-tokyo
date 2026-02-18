package br.com.softhouse.dende.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventoOrganizadorDTO {
    private String nome;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private BigDecimal precoIngresso;
    private Integer capacidadeMaxima;
    private String localAcesso;

    public EventoOrganizadorDTO(String nome,
                                LocalDateTime dataHoraInicio,
                                LocalDateTime dataHoraFim,
                                BigDecimal precoIngresso,
                                Integer capacidadeMaxima,
                                String localAcesso) {
        this.nome = nome;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.precoIngresso = precoIngresso;
        this.capacidadeMaxima = capacidadeMaxima;
        this.localAcesso = localAcesso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public BigDecimal getPrecoIngresso() {
        return precoIngresso;
    }

    public void setPrecoIngresso(BigDecimal precoIngresso) {
        this.precoIngresso = precoIngresso;
    }

    public Integer getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(Integer capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public String getLocalAcesso() {
        return localAcesso;
    }

    public void setLocalAcesso(String localAcesso) {
        this.localAcesso = localAcesso;
    }
}

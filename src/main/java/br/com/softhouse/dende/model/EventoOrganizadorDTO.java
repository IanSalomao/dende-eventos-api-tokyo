package br.com.softhouse.dende.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EventoOrganizadorDTO {
    private String nome;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFinal;
    private BigDecimal precoIngresso;
    private Integer capacidadeMaxima;
    private String localAcesso;

    public EventoOrganizadorDTO(String nome,
                                LocalDateTime dataInicio,
                                LocalDateTime dataFinal,
                                BigDecimal precoIngresso,
                                Integer capacidadeMaxima,
                                String localAcesso) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFinal = dataFinal;
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

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(LocalDateTime dataHoraFim) {
        this.dataFinal = dataFinal;
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

package br.com.softhouse.dende.model;
import br.com.softhouse.dende.model.enums.StatusIngresso;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ingresso {

    private Long id;
    private BigDecimal valorPago;
    private StatusIngresso status;
    private LocalDateTime dataCompra;
    private Evento evento;
    private UsuarioComum usuario;

    private Ingresso(){}

    private Ingresso(Evento evento, BigDecimal valorPago, UsuarioComum usuario){
        this.evento = evento;
        this.valorPago = valorPago;
        this.usuario = usuario;
        this.status = StatusIngresso.ATIVO;
        this.dataCompra = LocalDateTime.now();
    }

    public Evento getEvento() { return evento; }
    public UsuarioComum getUsuario() { return usuario; }
    public BigDecimal getValorPago() { return valorPago; }
    public StatusIngresso getStatus() { return status; }
    public Long getId() {return id;}
    public void setId(Long id) { this.id = id; }

    public static Ingresso processarCompraIngresso(Evento evento, double valorPago, UsuarioComum usuario){
        if(evento.calcularVagasDisponiveis() <= 0){
            throw new IllegalStateException("Evento sem vagas disponíveis");
        }

        if(!evento.estaAtivo()){
            throw new IllegalStateException("Evento não está ativo.");
        }

        evento.reduzirIngresso();

        return new Ingresso(evento, valorPago, usuario);
    }

    public double cancelarIngresso(){
        if(this.status == StatusIngresso.CANCELADO){
            throw new IllegalStateException("Ingresso já está cancelado.");
        }

        this.status = StatusIngresso.CANCELADO;

        evento.aumentarIngresso();

        return evento.calcularValorEstorno(this);
    }

    public void cancelarPorEvento() {
        if (this.status != StatusIngresso.ATIVO) {
            return;
        }
        this.status = StatusIngresso.CANCELADO_PELO_EVENTO;
        evento.aumentarIngresso();
    }
}

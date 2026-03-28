package br.com.softhouse.dende.model;
import br.com.softhouse.dende.model.enums.StatusIngresso;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Ingresso {

    private Long id;
    private BigDecimal valorPago;
    private StatusIngresso status;
    private LocalDateTime dataCompra;
    private Evento evento;
    private UsuarioComum usuario;

    private Ingresso() {}

    private Ingresso(Evento evento, UsuarioComum usuario, BigDecimal valorPago) {
        if (evento == null) throw new IllegalArgumentException("Evento não pode ser nulo.");
        if (usuario == null) throw new IllegalArgumentException("Usuário não pode ser nulo.");
        if (valorPago == null || valorPago.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Valor pago inválido.");
        this.evento = evento;
        this.valorPago = valorPago;
        this.usuario = usuario;
        this.status = StatusIngresso.ATIVO;
        this.dataCompra = LocalDateTime.now();
    }

    public static Ingresso criar(Evento evento, UsuarioComum usuario, BigDecimal valorPago) {
        return new Ingresso(evento, usuario, valorPago);
    }

    public Evento getEvento() { return this.evento; }
    public UsuarioComum getUsuario() { return this.usuario; }
    public BigDecimal getValorPago() { return this.valorPago; }
    public StatusIngresso getStatus() { return this.status; }
    public LocalDateTime getDataCompra() { return this.dataCompra; }
    public Long getId() { return this.id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal cancelarIngresso() {
        if (!evento.isPermiteEstorno())
            throw new IllegalStateException("Evento não permite estorno.");
        if (this.status != StatusIngresso.ATIVO)
            throw new IllegalStateException("Ingresso já está cancelado.");
        this.status = StatusIngresso.CANCELADO_PELO_USUARIO;
        return evento.calcularValorEstorno(this);
    }

    public void cancelarPorEvento() {
        if (this.status != StatusIngresso.ATIVO) return;
        this.status = StatusIngresso.CANCELADO_PELO_EVENTO;
    }

    public boolean estaCancelado() {
        return this.status == StatusIngresso.CANCELADO_PELO_USUARIO
                || this.status == StatusIngresso.CANCELADO_PELO_EVENTO;
    }

    public static BigDecimal calcularValorTotal(List<Ingresso> ingressos) {
        return ingressos.stream()
                .map(Ingresso::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

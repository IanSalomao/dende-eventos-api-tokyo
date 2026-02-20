package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.enums.ModalidadeEvento;
import br.com.softhouse.dende.model.enums.StatusEvento;
import br.com.softhouse.dende.model.enums.StatusIngresso;
import br.com.softhouse.dende.model.enums.TipoEvento;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evento {
    private long id;
    private String nome;
    private String descricao;
    private String paginaEvento;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFinal;
    private TipoEvento tipo;
    private ModalidadeEvento modalidade;
    private Integer capacidadeMaxima;
    private String localAcesso;
    private StatusEvento status;
    private BigDecimal precoIngresso;
    private Boolean permiteEstorno;
    private BigDecimal taxaEstorno;
    private Evento eventoPrincipal;
    private UsuarioOrganizador usuarioOrganizador;

    private List<Ingresso> ingressos = new ArrayList<>();

    public Evento(
            final String nome,
            final String descricao,
            final String paginaEvento,
            final LocalDateTime dataInicio,
            final LocalDateTime dataFinal,
            final TipoEvento tipo,
            final ModalidadeEvento modalidade,
            final Integer capacidadeMaxima,
            final String localAcesso,
            final BigDecimal precoIngresso,
            final Boolean permiteEstorno,
            final BigDecimal taxaEstorno,
            final Evento eventoPrincipal
    ) {
        this.nome = nome;
        this.descricao = descricao;
        this.paginaEvento = paginaEvento;
        this.dataInicio = dataInicio;
        this.dataFinal = dataFinal;
        this.tipo = tipo;
        this.modalidade = modalidade;
        this.capacidadeMaxima = capacidadeMaxima;
        this.localAcesso = localAcesso;
        this.precoIngresso = precoIngresso;
        this.permiteEstorno = permiteEstorno;
        this.taxaEstorno = taxaEstorno;
        this.eventoPrincipal = eventoPrincipal;

        validarInvariantes();
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

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public LocalDateTime getDataFinal() {
        return dataFinal;
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

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setDataFinal(LocalDateTime dataFinal) {
        this.dataFinal = dataFinal;
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

    public void setStatus(StatusEvento status) {
        this.status = status;
    }

    public UsuarioOrganizador getOrganizador() {
        return usuarioOrganizador;
    }

    public void atribuirId(final long id) {
        if (this.id == 0) this.id = id;
    }

    private void validarDatas(LocalDateTime dataInicio, LocalDateTime dataFinal) {

        if (dataInicio == null || dataFinal == null)
            throw new IllegalArgumentException("Datas e Horários não podem ser nulos.");
        if (dataInicio.isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Data e horário iniciais não podem ser antetiores as atuais");

        long duracaoMinutos = Duration.between(dataInicio, dataFinal).toMinutes();
        if (duracaoMinutos < 0)
            throw new IllegalArgumentException("Data e horário finais não podem ser antriores a data e horário iniciais.");
        if (duracaoMinutos < 30) throw new IllegalArgumentException("Evento não pode durar menos de 30 min.");
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome não pode ser vazio");
    }

    private void validarCapacidade(Integer capacidade) {
        if (capacidade != null && capacidade <= 0)
            throw new IllegalArgumentException("Capacidade não pode ser negativa ou igual a zero");
    }

    private void validarPreco(BigDecimal preco) {
        if (preco != null && preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo ou nulo.");
        }
    }

    private void validarLocalAcesso(String localAcesso) {
        if (localAcesso == null || localAcesso.trim().isEmpty())
            throw new IllegalArgumentException("Local de acesso não pode ser vazio");
    }

    public void validarInvariantes() {
        validarNome(this.nome);
        validarDatas(this.dataInicio, this.dataFinal);
        validarCapacidade(this.capacidadeMaxima);
        validarPreco(this.precoIngresso);
        validarPreco(this.taxaEstorno);
        validarLocalAcesso(this.localAcesso);
    }

    public void atribuirOrganizador(UsuarioOrganizador usuarioOrganizador) {
        if (this.usuarioOrganizador != null) throw new IllegalArgumentException("Esse evento já possui organizador");

        this.usuarioOrganizador = usuarioOrganizador;
    }

    public void alterarDados(Evento novosDados) {

        if (this.status != StatusEvento.ATIVO) {
            throw new IllegalArgumentException("Apenas eventos ativos podem ser alterados. Status atual: " + this.status);
        }

        LocalDateTime novoHorarioInicio = (novosDados.getDataInicio() != null) ? novosDados.getDataInicio() : this.dataInicio;
        LocalDateTime novoHorarioFim = (novosDados.getDataFinal() != null) ? novosDados.getDataFinal() : this.dataFinal;
        Boolean novoPermiteEstorno = (novosDados.isPermiteEstorno() != null) ? novosDados.isPermiteEstorno() : this.permiteEstorno;
        BigDecimal novaTaxaEstorno = (novosDados.getTaxaEstorno() != null) ? novosDados.getTaxaEstorno() : this.taxaEstorno;

        validarDatas(novoHorarioInicio, novoHorarioFim);

        if (novoPermiteEstorno != null && !novoPermiteEstorno && novaTaxaEstorno != null) {
            throw new IllegalArgumentException("Não é permitido definir taxa de estorno para eventos que não permitem estorno.");
        }

        this.dataInicio = novoHorarioInicio;
        this.dataFinal = novoHorarioFim;

        if (novosDados.getNome() != null) {
            this.nome = novosDados.getNome();
        }
        if (novosDados.getCapacidadeMaxima() != null) {
            this.capacidadeMaxima = novosDados.getCapacidadeMaxima();
        }
        if (novosDados.getDescricao() != null) {
            this.descricao = novosDados.getDescricao();
        }
        if (novosDados.getEventoPrincipal() != null) {
            this.eventoPrincipal = novosDados.getEventoPrincipal();
        }
        if (novosDados.getLocalAcesso() != null) {
            this.localAcesso = novosDados.getLocalAcesso();
        }
        if (novosDados.getModalidade() != null) {
            this.modalidade = novosDados.getModalidade();
        }
        if (novosDados.getPaginaEvento() != null) {
            this.paginaEvento = novosDados.getPaginaEvento();
        }
        if (novosDados.getPrecoIngresso() != null) {
            this.precoIngresso = novosDados.getPrecoIngresso();
        }
        if (novosDados.getTipo() != null) {
            this.tipo = novosDados.getTipo();
        }
        if (novosDados.getPrecoIngresso() != null) {
            this.precoIngresso = novosDados.getPrecoIngresso();
        }

        this.permiteEstorno = novoPermiteEstorno;
        this.taxaEstorno = novaTaxaEstorno;
    }

    public void adicionarIngresso(Ingresso ingresso) {
        this.ingressos.add(ingresso);
    }

    public int calcularVagasDisponiveis(){
        return this.capacidadeMaxima - this.ingressos.size();
    }

    public boolean estaAtivo(){
        return this.status == StatusEvento.ATIVO;
    }
    public boolean estaInativo(){
        return this.status != StatusEvento.ATIVO;
    }

    public double calcularValorEstorno(Ingresso ingresso) {
        if (!permiteEstorno) {
            return 0;
        }

        double valor = ingresso.getValorPago().doubleValue();
        return valor - (valor * this.taxaEstorno.doubleValue());
    }

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
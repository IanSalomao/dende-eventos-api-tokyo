package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.dto.AlterarPerfilComumDTO;
import br.com.softhouse.dende.model.dto.CompraIngressoDTO;
import br.com.softhouse.dende.model.enums.Sexo;
import br.com.softhouse.dende.model.enums.StatusIngresso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioComum extends Usuario{

    private List<Ingresso> ingressos = new ArrayList<>();

    public UsuarioComum() {
        super();
    }

    public UsuarioComum(Long id, String nome, LocalDate dataNascimento, Sexo sexo, String email, String senha) {
        super(id, nome, dataNascimento, sexo, email, senha);
    }

    public void alterarPerfil(AlterarPerfilComumDTO dto) {
        if (dto.nome() != null) setNome(dto.nome());
        if (dto.dataNascimento() != null) setDataNascimento(dto.dataNascimento());
        if (dto.sexo() != null) setSexo(dto.sexo());
        if (dto.senha() != null) setSenha(dto.senha());
    }

    /**
     * Lista todos os ingressos do usuário ordenados por:
     * 1. Status: eventos ativos não realizados primeiro, cancelados/finalizados por último
     * 2. Data de início do evento
     * 3. Nome do evento (ordem alfabética)
     */
    public List<Ingresso> listarIngressos() {
        LocalDateTime agora = LocalDateTime.now();

        return this.ingressos.stream()
                .sorted(Comparator
                        // Primeiro: eventos ativos e não realizados (prioridade 0)
                        // Último: eventos cancelados ou finalizados (prioridade 1)
                        .comparing((Ingresso ingresso) -> {
                            Evento evento = ingresso.getEvento();
                            boolean eventoAtivo = evento.estaAtivo();
                            boolean eventoJaRealizado = evento.getDataInicio().isBefore(agora);
                            boolean ingressoCancelado = ingresso.estaCancelado();
                            boolean eventoCancelado = ingresso.getStatus() == StatusIngresso.CANCELADO_PELO_EVENTO;

                            // Se ingresso cancelado OU evento inativo OU já realizado -> vai pro final
                            if (ingressoCancelado || !eventoAtivo || eventoJaRealizado ||eventoCancelado) {
                                return 1;
                            }
                            // Caso contrário, vai pro início
                            return 0;
                        })
                        // Segundo: ordena por data de início do evento
                        .thenComparing(ingresso -> ingresso.getEvento().getDataInicio())
                        // Terceiro: ordena por nome do evento (alfabética)
                        .thenComparing(ingresso -> ingresso.getEvento().getNome())
                ).collect(Collectors.toList());
    }


    // [AVALIAÇÃO - Item 6] O método comprarIngresso() acumula muitas responsabilidades na camada de modelo:
    // cria ingressos, adiciona ao histórico do usuário, adiciona à lista do evento e retorna um DTO.
    // A lógica de orquestração (adicionar ingresso ao evento e ao usuário) deveria ser coordenada por
    // um serviço ou pelo repositório, mantendo o modelo focado nas regras de negócio.
    // [AVALIAÇÃO - Item 14] A variável 'valorTotal' usa 'double', que é inadequado para valores financeiros.
    // Sugestão: use BigDecimal para valorTotal.
    // Código sugerido: BigDecimal valorTotal = BigDecimal.ZERO;
    //                  valorTotal = valorTotal.add(eventoPrincipal.getPrecoIngresso());
    public CompraIngressoDTO comprarIngresso(Evento evento) {
        List<Ingresso> ingressosGerados = new ArrayList<>();

        // [AVALIAÇÃO - Item 14] Valor financeiro representado com 'double'.
        // Sugestão: BigDecimal valorTotal = BigDecimal.ZERO;
        double valorTotal = 0.0;

        if (evento.getEventoPrincipal() != null) {

            Evento eventoPrincipal = evento.getEventoPrincipal();

            Ingresso ingressoPrincipal =
                    Ingresso.processarCompraIngresso(eventoPrincipal,
                            eventoPrincipal.getPrecoIngresso(),
                            this);

            ingressosGerados.add(ingressoPrincipal);
            this.ingressos.add(ingressoPrincipal);
            eventoPrincipal.adicionarIngresso(ingressoPrincipal);

            valorTotal += eventoPrincipal.getPrecoIngresso().doubleValue();

        }

        Ingresso ingressoEvento =
                Ingresso.processarCompraIngresso(evento,
                        evento.getPrecoIngresso(),
                        this);

        ingressosGerados.add(ingressoEvento);
        evento.adicionarIngresso(ingressoEvento);
        this.ingressos.add(ingressoEvento);

        valorTotal += evento.getPrecoIngresso().doubleValue();

        return new CompraIngressoDTO(ingressosGerados, valorTotal);
    }

    @Override
    public String toString() {
        return "UsuarioComum{" + super.toString() + "}";
    }
}

package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.enums.Sexo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioComum extends Usuario{

    private List<Ingresso> ingressos = new ArrayList<>();

    public UsuarioComum(String nome, String usuario, LocalDate dataNascimento, Sexo sexo, String email, String senha){
        super(nome, usuario, dataNascimento, sexo, email, senha);
    }

    /**
     * Lista todos os ingressos do usuário ordenados por:
     * 1. Status: eventos ativos não realizados primeiro, cancelados/finalizados por último
     * 2. Data de início do evento
     * 3. Nome do evento (ordem alfabética)
     */
    public List<Ingresso> listarIngresso() {
        LocalDateTime agora = LocalDateTime.now();

        return this.ingressos.stream()
                .sorted(Comparator
                        // Primeiro: eventos ativos e não realizados (prioridade 0)
                        // Último: eventos cancelados ou finalizados (prioridade 1)
                        .comparing((Ingresso ingresso) -> {
                            Evento evento = ingresso.getEvento();
                            boolean eventoAtivo = evento.isAtivo();
                            boolean eventoJaRealizado = evento.getDataInicio().isBefore(agora);
                            boolean ingressoCancelado = ingresso.isCancelado();

                            // Se ingresso cancelado OU evento inativo OU já realizado -> vai pro final
                            if (ingressoCancelado || !eventoAtivo || eventoJaRealizado) {
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


    public List<Ingresso> comprarIngresso(Evento evento){
        List<Ingresso> ingressosGerados = new ArrayList<>();

        if(evento.possuiEventoPrincipal()){
            Evento eventoPrincipal = evento.getEventoPrincipal();

            Ingresso ingressoPrincipal = Ingresso.processarCompraIngresso(eventoPrincipal, eventoPrincipal.getValorIngresso(), this);
            ingressosGerados.add(ingressoPrincipal);
            this.ingressos.add(ingressoPrincipal);
        }

        Ingresso ingressoEvento = Ingresso.processarCompraIngresso(evento, evento.getValorIngresso(), this);
        ingressosGerados.add(ingressoEvento);
        this.ingressos.add(ingressoEvento);
        return ingressosGerados;
    }
}

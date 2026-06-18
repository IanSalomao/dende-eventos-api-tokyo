package br.com.softhouse.dende.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// [AVALIAÇÃO - Item 5] A conversão de Evento para ListarEventoOrganizadorDTO é feita inline no controller.
// Uma boa prática seria ter uma classe EventoMapper com um método toListarOrganizadorDTO(Evento e),
// centralizando o mapeamento. Não era obrigatório nesta avaliação, mas é recomendado.
// Código sugerido: public static ListarEventoOrganizadorDTO toListarOrganizadorDTO(Evento e) { ... }
public record ListarEventoOrganizadorDTO (
        String nome,
        LocalDateTime dataInicio,
        LocalDateTime dataFinal,
        BigDecimal precoIngresso,
        Integer capacidadeMaxima,
        String localAcesso
){}
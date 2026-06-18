package br.com.softhouse.dende.model.dto;

import java.time.LocalDateTime;

// [AVALIAÇÃO - Item 5] A conversão de Evento para EventoResponseDTO é feita inline no controller
// (new EventoResponseDTO(evento.getNome(), ...)). Uma boa prática seria ter uma classe EventoMapper
// com um método estático toResponseDTO(Evento evento), centralizando o mapeamento.
// Isso não era obrigatório nesta avaliação, mas é recomendado para manutenibilidade.
// Código sugerido: public class EventoMapper { public static EventoResponseDTO toResponseDTO(Evento e) { ... } }
// [AVALIAÇÃO - Item 4] O DTO retorna apenas nome, descricao, dataHoraInicio, dataHoraFim e nomeOrganizador.
// Para o feed de eventos (US12), faltam campos relevantes como precoIngresso, localAcesso e modalidade,
// que o usuário precisaria para decidir participar. Considere adicioná-los.
public record EventoResponseDTO (
        String nome,
        String descricao,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        String nomeOrganizador
){}

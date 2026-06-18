package br.com.softhouse.dende.model.dto;

import br.com.softhouse.dende.model.Ingresso;

import java.util.List;

// [AVALIAÇÃO - Item 14] O campo 'valorTotal' usa o tipo 'double', que é inadequado para valores financeiros
// devido a problemas de precisão com ponto flutuante.
// Sugestão: altere para BigDecimal.
// Código sugerido: public record CompraIngressoDTO(List<Ingresso> ingressos, BigDecimal valorTotal) {}
// [AVALIAÇÃO - Item 4] O campo 'ingressos' expõe objetos de domínio (Ingresso) diretamente no DTO.
// Uma boa prática seria usar List<IngressoDTO> para não expor detalhes internos da entidade.
public record CompraIngressoDTO(
        List<Ingresso> ingressos,
        double valorTotal
) {}

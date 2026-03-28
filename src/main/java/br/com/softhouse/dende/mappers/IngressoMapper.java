package br.com.softhouse.dende.mappers;

import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.dto.response.CompraIngressoResponseDTO;
import br.com.softhouse.dende.model.dto.response.IngressoResponseDTO;

import java.util.List;

public class IngressoMapper {
    public static IngressoResponseDTO toResponse(Ingresso ingresso) {
        return new IngressoResponseDTO(
                ingresso.getId(),
                ingresso.getEvento().getNome(),
                ingresso.getEvento().getDataInicio(),
                ingresso.getValorPago(),
                ingresso.getStatus().name(),
                ingresso.getDataCompra()
        );
    }

    public static CompraIngressoResponseDTO toCompraResponse(List<Ingresso> ingressos) {
        java.math.BigDecimal valorTotal = Ingresso.calcularValorTotal(ingressos);
        List<IngressoResponseDTO> lista = ingressos.stream()
                .map(IngressoMapper::toResponse)
                .toList();
        return new CompraIngressoResponseDTO(lista, valorTotal);
    }
}

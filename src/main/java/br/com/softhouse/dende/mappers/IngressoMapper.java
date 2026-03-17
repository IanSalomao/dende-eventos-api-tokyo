package br.com.softhouse.dende.mappers;

import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.dto.response.IngressoResponseDTO;

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
}

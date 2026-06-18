package br.com.softhouse.dende.model.dto;

import br.com.softhouse.dende.model.enums.Sexo;

import java.time.LocalDate;

// [AVALIAÇÃO - Item 5] A aplicação dos dados deste DTO ao modelo é feita diretamente em UsuarioComum.alterarPerfil(dto).
// Uma boa prática seria ter uma classe UsuarioMapper com um método apply(AlterarPerfilComumDTO dto, UsuarioComum usuario),
// separando a responsabilidade de mapeamento da entidade. Não era obrigatório nesta avaliação.
public record AlterarPerfilComumDTO(
        String nome,
        LocalDate dataNascimento,
        Sexo sexo,
        String senha
) {}

package br.com.softhouse.dende.model.dto;

import br.com.softhouse.dende.model.enums.Sexo;

import java.time.LocalDate;

public record AlterarPerfilComumDTO(
        String nome,
        LocalDate dataNascimento,
        Sexo sexo,
        String senha
) {}

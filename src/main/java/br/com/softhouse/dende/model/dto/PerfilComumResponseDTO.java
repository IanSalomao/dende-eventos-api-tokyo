package br.com.softhouse.dende.model.dto;

import br.com.softhouse.dende.model.enums.Sexo;

import java.time.LocalDate;

public record PerfilComumResponseDTO(
        String email,
        String nome,
        LocalDate dataNascimento,
        String idade,
        Sexo sexo
) {}



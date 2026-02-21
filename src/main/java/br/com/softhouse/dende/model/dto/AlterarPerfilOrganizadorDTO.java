package br.com.softhouse.dende.model.dto;

import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.enums.Sexo;

import java.time.LocalDate;

public record AlterarPerfilOrganizadorDTO(
        String nome,
        LocalDate dataNascimento,
        Sexo sexo,
        String senha,
        Empresa empresa
) { }

package br.com.softhouse.dende.model.dto.response;

import br.com.softhouse.dende.model.enums.Sexo;

import java.time.LocalDate;

public record PerfilOrganizadorResponseDTO(
        String email,
        String nome,
        LocalDate dataNascimento,
        String idade,
        Sexo sexo,
        String nomeEmpresa,
        String cnpj,
        String razaoSocial
) {}



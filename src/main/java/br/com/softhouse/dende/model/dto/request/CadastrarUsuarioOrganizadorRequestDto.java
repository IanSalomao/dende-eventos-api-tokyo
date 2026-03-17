package br.com.softhouse.dende.model.dto.request;

import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.enums.Sexo;

import java.time.LocalDate;

public record CadastrarUsuarioOrganizadorRequestDto(
        String nome,
        LocalDate dataNascimento,
        Sexo sexo,
        String email,
        String senha,
        Empresa empresa
) {
}

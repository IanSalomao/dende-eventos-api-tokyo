package br.com.softhouse.dende.model.dto;

import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.enums.Sexo;

import java.time.LocalDate;

// [AVALIAÇÃO - Item 5] A aplicação deste DTO ao modelo é feita em UsuarioOrganizador.alterarPerfil(dto).
// Uma boa prática seria ter uma classe UsuarioMapper com um método apply(AlterarPerfilOrganizadorDTO, UsuarioOrganizador).
// Não era obrigatório nesta avaliação, mas é recomendado para manutenibilidade.
// [AVALIAÇÃO - Item 4] O campo 'empresa' usa o objeto de domínio Empresa diretamente no DTO.
// Considere criar um EmpresaDTO para evitar acoplamento entre a camada de transporte e o domínio.
public record AlterarPerfilOrganizadorDTO(
        String nome,
        LocalDate dataNascimento,
        Sexo sexo,
        String senha,
        Empresa empresa
) { }

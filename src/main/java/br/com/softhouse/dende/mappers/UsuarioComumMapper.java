package br.com.softhouse.dende.mappers;

import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.model.dto.request.CadastrarUsuarioComumRequestDto;
import br.com.softhouse.dende.model.dto.response.PerfilComumResponseDTO;

public class UsuarioComumMapper {

    public static UsuarioComum toModel(CadastrarUsuarioComumRequestDto dto) {
        return new UsuarioComum(
                dto.nome(),
                dto.dataNascimento(),
                dto.sexo(),
                dto.email(),
                dto.senha()
        );
    }

    public static PerfilComumResponseDTO toResponse(UsuarioComum usuario) {
        return new PerfilComumResponseDTO(
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getDataNascimento(),
                usuario.calcularIdade(),
                usuario.getSexo()
        );
    }
}

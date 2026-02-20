package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.dto.AlterarPerfilDTO;
import br.com.softhouse.dende.model.enums.Sexo;

import java.time.LocalDate;
import java.util.List;

public class UsuarioComum extends Usuario {

    public UsuarioComum() {
        super();
    }

    public UsuarioComum(Long id, String nome, LocalDate dataNascimento, Sexo sexo, String email, String senha) {
        super(id, nome, dataNascimento, sexo, email, senha);
    }

    @Override
    public void alterarPerfil(AlterarPerfilDTO dto) {
        if (dto.nome() != null) setNome(dto.nome());
        if (dto.dataNascimento() != null) setDataNascimento(dto.dataNascimento());
        if (dto.sexo() != null) setSexo(dto.sexo());
        if (dto.senha() != null) setSenha(dto.senha());
    }

    public List<Object> comprarIngressos(Object evento, Integer quantidade) {
        throw new UnsupportedOperationException("Funcionalidade ainda nao implementada");
    }

    public void cancelarIngresso(Object ingresso) {
        throw new UnsupportedOperationException("Funcionalidade ainda nao implementada");
    }

    public List<Object> listarIngressos() {
        throw new UnsupportedOperationException("Funcionalidade ainda nao implementada");
    }

    public List<Object> visualizarFeedEventos() {
        throw new UnsupportedOperationException("Funcionalidade ainda nao implementada");
    }

    @Override
    public String toString() {
        return "UsuarioComum{" + super.toString() + "}";
    }
}
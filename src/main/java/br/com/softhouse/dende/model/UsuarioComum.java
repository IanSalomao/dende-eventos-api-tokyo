package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.dto.AlterarPerfilComumDTO;
import br.com.softhouse.dende.model.enums.Sexo;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioComum extends Usuario {

    public UsuarioComum() {
        super();
    }

    public UsuarioComum(String nome, LocalDate dataNascimento, Sexo sexo, String email, String senha) {
        super(nome, dataNascimento, sexo, email, senha);
    }

    public void alterarPerfil(AlterarPerfilComumDTO dto) {
        if (dto.nome() != null) setNome(dto.nome());
        if (dto.dataNascimento() != null) setDataNascimento(dto.dataNascimento());
        if (dto.sexo() != null) setSexo(dto.sexo());
        if (dto.senha() != null) setSenha(dto.senha());
    }

    public List<Ingresso> solicitarIngresso(Evento evento) {
        return evento.processarCompraIngresso(this);
    }

    public List<Ingresso> listarIngressos() {
        List<Ingresso> todos = Repositorio.getInstance().buscarIngressosPorUsuario(this);

        return todos.stream()
                .sorted(Comparator
                        .comparingInt((Ingresso ingresso) -> {
                            boolean ingressoAtivo = ingresso.getStatus() == br.com.softhouse.dende.model.enums.StatusIngresso.ATIVO;
                            boolean eventoAtivo = ingresso.getEvento().estaAtivo();
                            return (ingressoAtivo && eventoAtivo) ? 0 : 1;
                        })
                        .thenComparing(ingresso -> ingresso.getEvento().getDataInicio())
                        .thenComparing(ingresso -> ingresso.getEvento().getNome())
                )
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "UsuarioComum{" + super.toString() + "}";
    }
}

package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.dto.AlterarPerfilOrganizadorDTO;
import br.com.softhouse.dende.model.enums.Sexo;
import br.com.softhouse.dende.model.enums.StatusEvento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioOrganizador extends Usuario {

    private Empresa empresa;
    private final List<Evento> eventos = new ArrayList<>();

    public UsuarioOrganizador() {
        super();
    }

    public UsuarioOrganizador(String nome, LocalDate dataNascimento, Sexo sexo, String email, String senha, Empresa empresa) {
        super(nome, dataNascimento, sexo, email, senha);
        this.empresa = empresa;
    }

    public Empresa getEmpresa(){return empresa;}


    public void alterarPerfil(AlterarPerfilOrganizadorDTO dto) {
        if (dto.nome() != null) setNome(dto.nome());
        if (dto.dataNascimento() != null) setDataNascimento(dto.dataNascimento());
        if (dto.sexo() != null) setSexo(dto.sexo());
        if (dto.senha() != null) setSenha(dto.senha());
        if (dto.cnpj() != null || dto.razaoSocial() != null || dto.nomeFantasia() != null)
            this.empresa = new Empresa(dto.cnpj(), dto.razaoSocial(), dto.nomeFantasia());
    }

    @Override
    public void desativarUsuario() {
        LocalDateTime agora = LocalDateTime.now();
        boolean impedido = eventos.stream().anyMatch(e ->
                e.getStatus() == StatusEvento.ATIVO &&
                        e.getDataFinal().isAfter(agora)
        );
        if (impedido)
            throw new IllegalArgumentException("Não é possível desativar a conta com eventos ativos ou em execução.");
        super.desativarUsuario();
    }

    @Override
    public String visualizarPerfil() {
        String perfilBase = super.visualizarPerfil();
        if (empresa != null) {
            return perfilBase +
                    "\nEmpresa: " + empresa.getNomeFantasia() +
                    " | CNPJ: " + empresa.getCnpj() +
                    " | Razao Social: " + empresa.getRazaoSocial();
        }
        return perfilBase;
    }

    public void cadastrarEvento(Evento evento) {
        evento.atribuirOrganizador(this);
        this.eventos.add(evento);
    }

    public void alterarEvento(long eventoId, Evento novosDados) {
        Evento evento = eventos.stream()
                .filter(e -> e.getId() == eventoId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado para este organizador."));
        evento.alterarDados(novosDados);
    }

    public List<Evento> listarEventosOrganizador() {
        return this.eventos.stream()
                .sorted(Comparator
                        .comparing(Evento::getDataInicio)
                        .thenComparing(Evento::getNome, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "UsuarioOrganizador{" + visualizarPerfil() + "}";
    }
}
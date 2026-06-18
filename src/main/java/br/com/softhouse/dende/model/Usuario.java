package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.enums.Sexo;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public abstract class Usuario {

    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private Sexo sexo;
    private String email;
    private String senha;
    private Boolean ativo;

    public Usuario() {
        this.ativo = true;
    }

    public Usuario(Long id, String nome, LocalDate dataNascimento, Sexo sexo, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.email = email;
        this.senha = senha;
        this.ativo = true;
    }

    // [AVALIAÇÃO - Item 8] O método retorna uma String formatada em vez de um objeto (DTO).
    // Retornar uma String dificulta o consumo e a serialização padronizada dos dados pelo controller.
    // Sugestão: crie um UsuarioPerfilDTO com os campos nome, idade, sexo, email e retorne-o.
    // Código sugerido: public UsuarioPerfilDTO visualizarPerfil()
    public String visualizarPerfil() {
        return "Nome: " + nome +
                "\nIdade: " + calcularIdade() +
                "\nSexo: " + sexo +
                "\nE-mail: " + email;
    }

    public void desativarUsuario() {
        this.ativo = false;
    }

    // [AVALIAÇÃO - Item 7] O método reativarUsuario() lança exceção se as credenciais forem inválidas.
    // Se chamado mais de uma vez com credenciais corretas num usuário já ativo, não causa efeito colateral
    // (define true sobre true). Porém, considere verificar se o usuário já está ativo antes de prosseguir
    // para tornar a operação explicitamente idempotente e mais legível.
    public void reativarUsuario(String email, String senha) {
        if (!this.email.equals(email) || !this.senha.equals(senha)) {
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        }
        this.ativo = true;
    }

    public String calcularIdade() {
        if (dataNascimento == null) return null;
        Period periodo = Period.between(dataNascimento, LocalDate.now());
        return periodo.getYears() + " anos, " + periodo.getMonths() + " meses e " + periodo.getDays() + " dias";
    }

    public boolean isAtivo() {
        return Boolean.TRUE.equals(ativo);
    }


    // [AVALIAÇÃO - Item 8] O campo 'id' não possui getter público. Sem acesso ao id do usuário,
    // é impossível utilizá-lo como referência em outras partes da aplicação ou retorná-lo em respostas.
    // Sugestão: adicione: public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public void setSexo(Sexo sexo) { this.sexo = sexo; }
    public String getEmail() { return email; }
    public void setSenha(String senha) { this.senha = senha; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Usuario usuario = (Usuario) object;
        return Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + "', email='" + email + "', ativo=" + ativo + '}';
    }
}
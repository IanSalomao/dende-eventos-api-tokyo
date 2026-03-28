package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.enums.Sexo;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public abstract class Usuario {

    private String email;
    private String nome;

    private LocalDate dataNascimento;
    private Sexo sexo;

    private String senha;
    private Boolean ativo;

    protected Usuario() {
        this.ativo = true;
    }

    public Usuario(String nome, LocalDate dataNascimento, Sexo sexo, String email, String senha) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio.");
        if (email == null || email.isBlank() || !email.contains("@"))
            throw new IllegalArgumentException("E-mail inválido.");
        if (senha == null || senha.isBlank())
            throw new IllegalArgumentException("Senha não pode ser nula ou vazia.");
        if (dataNascimento == null)
            throw new IllegalArgumentException("Data de nascimento não pode ser nula.");
        if (dataNascimento.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Data de nascimento não pode ser futura.");
        if (sexo == null)
            throw new IllegalArgumentException("Sexo não pode ser nulo.");
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.email = email;
        this.senha = senha;
        this.ativo = true;
    }

    public String visualizarPerfil() {
        return "Nome: " + nome +
                "\nIdade: " + calcularIdade() +
                "\nSexo: " + sexo +
                "\nE-mail: " + email;
    }

    public void desativarUsuario() {
        if (!this.ativo) throw new IllegalStateException("Usuario ja esta inativo.");
        this.ativo = false;
    }

    public void reativarUsuario(String email, String senha) {
        if (this.ativo) throw new IllegalStateException("Usuario ja esta ativo.");
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


    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public LocalDate getDataNascimento() {return dataNascimento;}
    public Sexo getSexo() {return sexo;}
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
        return "Usuario{ nome='" + nome + "', email='" + email + "', ativo=" + ativo + '}';
    }
}
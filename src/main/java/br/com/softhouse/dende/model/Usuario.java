package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.enums.Sexo;

import java.time.LocalDate;
import java.util.Objects;

public class Usuario {
    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private Sexo sexo;
    private String email;
    private String senha;
    private boolean ativo;

    public Usuario() {}

    public Usuario(
            final String nome,
            final LocalDate dataNascimento,
            final Sexo sexo,
            final String email,
            final String senha
    ) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.email = email;
        this.senha = senha;
        this.ativo = true;
    }

    public Long getId() {return id;}
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    public Sexo getSexo() {
        return sexo;
    }
    public String getEmail() {
        return email;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }
    public void setId(Long id) { this.id = id; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Usuario usuario = (Usuario) object;
        return Objects.equals(nome, usuario.nome) && Objects.equals(dataNascimento, usuario.dataNascimento) && Objects.equals(sexo, usuario.sexo) && Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, dataNascimento, sexo, email);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nome='" + nome + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", sexo='" + sexo + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.dto.AlterarPerfilComumDTO;
import br.com.softhouse.dende.model.dto.AlterarPerfilOrganizadorDTO;
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

    public String visualizarPerfil() {
        return "Nome: " + nome +
                "\nIdade: " + calcularIdade() +
                "\nSexo: " + sexo +
                "\nE-mail: " + email;
    }

    public void desativarUsuario() {
        this.ativo = false;
    }

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
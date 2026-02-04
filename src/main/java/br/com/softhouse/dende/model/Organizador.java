package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.util.Objects;

public class Organizador {

    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String email;
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;

    public Organizador(String nome, LocalDate dataNascimento, String sexo, String email, String cnpj, String razaoSocial, String nomeFantasia) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.email = email;
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
    }

    public Organizador() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Organizador that = (Organizador) object;
        return Objects.equals(nome, that.nome) && Objects.equals(dataNascimento, that.dataNascimento) && Objects.equals(sexo, that.sexo) && Objects.equals(email, that.email) && Objects.equals(cnpj, that.cnpj) && Objects.equals(razaoSocial, that.razaoSocial) && Objects.equals(nomeFantasia, that.nomeFantasia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, dataNascimento, sexo, email, cnpj, razaoSocial, nomeFantasia);
    }

    @Override
    public String toString() {
        return "Organizador{" +
                "nome='" + nome + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", sexo='" + sexo + '\'' +
                ", email='" + email + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", razaoSocial='" + razaoSocial + '\'' +
                ", nomeFantasia='" + nomeFantasia + '\'' +
                '}';
    }
}

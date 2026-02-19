package br.com.softhouse.dende.model;

import java.time.LocalDate;

public class Organizador extends Usuario {

    private Empresa empresa;

    public Organizador() {
        super();
    }

    public Organizador(String nome, LocalDate dataNascimento, String sexo, String email, String senha, Empresa empresa) {
        super(nome, dataNascimento, sexo, email, senha);
        this.empresa = empresa;
    }

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }

    @Override
    public void desativar() {

        super.desativar();
    }
}
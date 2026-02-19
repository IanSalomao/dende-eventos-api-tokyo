package br.com.softhouse.dende.model;

import br.com.softhouse.dende.repositories.Repositorio;

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
        if (!Repositorio.getInstance().listarEventosOrganizador(this.email, StatusEvento.ATIVO).isEmpty()){ //Esse metodo foi feito por mim (Bia) no repo
            throw new IllegalArgumentException("Não é possível desativar usuários organizadores com eventos ativos.");
        }
        super.desativar();
    }
}
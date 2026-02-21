package br.com.softhouse.dende.model;

public class Empresa {
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;

    public Empresa() {}

    public Empresa(String cnpj, String razaoSocial, String nomeFantasia) {
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() { return cnpj; }
    public String getRazaoSocial() { return razaoSocial; }
    public String getNomeFantasia() { return nomeFantasia; }
}
package br.com.softhouse.dende.model;

public class Empresa {
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;

    protected Empresa() {}

    public Empresa(String cnpj, String razaoSocial, String nomeFantasia) {
        if (cnpj == null || cnpj.isBlank())
            throw new IllegalArgumentException("CNPJ não pode ser nulo ou vazio.");
        if (razaoSocial == null || razaoSocial.isBlank())
            throw new IllegalArgumentException("Razão social não pode ser nula ou vazia.");
        if (nomeFantasia == null || nomeFantasia.isBlank())
            throw new IllegalArgumentException("Nome fantasia não pode ser nulo ou vazio.");
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() { return cnpj; }
    public String getRazaoSocial() { return razaoSocial; }
    public String getNomeFantasia() { return nomeFantasia; }
}
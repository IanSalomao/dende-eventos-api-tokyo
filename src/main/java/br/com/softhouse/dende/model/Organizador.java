package br.com.softhouse.dende.model;

import br.com.softhouse.dende.repositories.Repositorio; // Import adicionado conforme revisão
import java.time.LocalDate;

public class Organizador extends Usuario {

    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;

    public Organizador(String nome, LocalDate dataNascimento, String sexo, String email, String senha,
                       String cnpj, String razaoSocial, String nomeFantasia) {
        super(nome, dataNascimento, sexo, email, senha);
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
    }

    @Override
    public void desativar() {
        if (!Repositorio.getInstance().listarEventosOrganizador(this.getEmail(), StatusEvento.ATIVO).isEmpty()) {
            throw new IllegalArgumentException("Não é possível desativar usuários organizadores com eventos ativos.");
        }
        super.desativar();
    }

    public String getCnpj() { return cnpj; }
    public String getRazaoSocial() { return razaoSocial; }
    public String getNomeFantasia() { return nomeFantasia; }
}
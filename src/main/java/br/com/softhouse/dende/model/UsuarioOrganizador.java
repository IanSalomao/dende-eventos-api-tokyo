package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.enums.Sexo;

import java.time.LocalDate;
import java.util.List;

public class UsuarioOrganizador extends Usuario {

    private Empresa empresa;

    public UsuarioOrganizador() {
        super();
    }

    public UsuarioOrganizador(Long id, String nome, LocalDate dataNascimento, Sexo sexo, String email, String senha, Empresa empresa) {
        super(id, nome, dataNascimento, sexo, email, senha);
        this.empresa = empresa;
    }

    @Override
    public void alterarPerfil(Usuario dadosNovos) {
        setNome(dadosNovos.getNome());
        setDataNascimento(dadosNovos.getDataNascimento());
        setSexo(dadosNovos.getSexo());
        setSenha(dadosNovos.getSenha());
        if (dadosNovos instanceof UsuarioOrganizador organizadorNovos) {
            this.empresa = organizadorNovos.getEmpresa();
        }
    }

    @Override
    public String visualizarPerfil() {
        String perfilBase = super.visualizarPerfil();
        if (empresa != null) {
            return perfilBase + "\n" + empresa.informacoesEmpresa();
        }
        return perfilBase;
    }

    public void cadastrarEvento(Object evento) {
        throw new UnsupportedOperationException("Funcionalidade ainda nao implementada");
    }

    public void alterarEvento() {
        throw new UnsupportedOperationException("Funcionalidade ainda nao implementada");
    }

    public void ativarEvento() {
        throw new UnsupportedOperationException("Funcionalidade ainda nao implementada");
    }

    public void desativarEvento() {
        throw new UnsupportedOperationException("Funcionalidade ainda nao implementada");
    }

    public List<Object> listarMeusEventos() {
        throw new UnsupportedOperationException("Funcionalidade ainda nao implementada");
    }

    public List<Object> verificarEventosAtivos() {
        throw new UnsupportedOperationException("Funcionalidade ainda nao implementada");
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @Override
    public String toString() {
        return "UsuarioOrganizador{" + super.toString() + ", empresa=" + empresa + "}";
    }
}
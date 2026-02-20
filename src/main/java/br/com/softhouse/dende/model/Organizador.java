package br.com.softhouse.dende.model;

import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.enums.StatusEvento;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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
        if (!Repositorio.getInstance().listarEventosOrganizador(super.getEmail(), StatusEvento.ATIVO).isEmpty()){
            throw new IllegalArgumentException("Não é possível desativar usuários organizadores com eventos ativos.");
        }
        super.desativar();
    }

    public void cadastrarEvento(Evento novoEvento){
        novoEvento.setStatus(StatusEvento.INATIVO);
        novoEvento.atribuirOrganizador(this);
        novoEvento.validarInvariantes();
        Repositorio.getInstance().salvarEvento(novoEvento);
    }

    public List<EventoOrganizadorDTO> listarMeusEventos(){
        return Repositorio.getInstance().listarEventosOrganizador(super.getEmail(), null);
    }

    public void alterarEvento(Long eventoId, Evento eventoAtualizado){
        Evento evento = Repositorio.getInstance().buscarEventoPorId(eventoId);

        if(evento == null){throw new IllegalArgumentException("Evento inexistente.");}
        if(!Objects.equals(evento.getOrganizador(), this)){throw new IllegalArgumentException("Apenas o organizador do evento pode alterá-lo");}

        evento.alterarDados(eventoAtualizado);
    }

}

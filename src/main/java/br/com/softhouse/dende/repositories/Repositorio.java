package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.EventoOrganizadorDTO;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.enums.StatusEvento;

import java.util.*;

public class Repositorio {

    private static Repositorio instance = new Repositorio();
    private final Map<String, Usuario> usuariosComum;
    private final Map<String, Organizador> organizadores;
    private final Map<Long, Evento> eventos;

    private Repositorio() {
        this.usuariosComum = new HashMap<>();
        this.organizadores = new HashMap<>();
        this.eventos = new HashMap<>();
    }

    public static Repositorio getInstance() {
        return instance;
    }

    static long proximoIdEvento = 0;
    public void salvarEvento(Evento evento){
        evento.atribuirId(proximoIdEvento);
        eventos.put(proximoIdEvento, evento);
        proximoIdEvento ++;
    }

    public Organizador buscarOrganizador(String organizadorId){
        return organizadores.get(organizadorId);
    }

    public List<EventoOrganizadorDTO> listarEventosOrganizador(String organizadorId, StatusEvento status){

        List<EventoOrganizadorDTO> eventosOrganizador = new ArrayList<EventoOrganizadorDTO>();

        for(Evento evento : eventos.values()){
            boolean organizadorCorresponde = organizadorId == null ||
                    (evento.getOrganizador() != null &&
                            evento.getOrganizador().
                                    getEmail().equals(organizadorId));
            boolean statusCorresponde = status == null || evento.getStatus() == status;
            if (organizadorCorresponde && statusCorresponde){
               EventoOrganizadorDTO eventoOrganizador= new EventoOrganizadorDTO(
                       evento.getNome(),
                       evento.getDataInicio(),
                       evento.getDataFinal(),
                       evento.getPrecoIngresso(),
                       evento.getCapacidadeMaxima(),
                       evento.getLocalAcesso()
               );
                eventosOrganizador.add(eventoOrganizador);
            }
        }
        eventosOrganizador.sort(Comparator.comparing(EventoOrganizadorDTO::getDataInicio));

        return eventosOrganizador;
    }

    public Evento buscarEventoPorId(Long eventoId) {
        return eventos.get(eventoId);
    }
}

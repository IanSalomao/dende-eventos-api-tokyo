package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repositorio {

    private static Repositorio instance = new Repositorio();
    private final Map<Long, Ingresso> ingressos;
    private final Map<String, Usuario> usuarios;
    private final Map<Long, Evento> eventos;

    private long sequenciaIngressoId = 1L;
    private long sequenciaEventoId = 1L;

    private Repositorio() {
        this.ingressos = new HashMap<>();
        this.usuarios= new HashMap<>();
        this.eventos = new HashMap<>();
    }

    public static Repositorio getInstance() {
        return instance;
    }

    public void salvarIngresso(Ingresso ingresso){
        ingresso.setId(sequenciaIngressoId);
        ingressos.put(sequenciaIngressoId, ingresso);
        sequenciaIngressoId++;
    }

    public void salvarUsuario(Usuario usuario) {
        if (usuarios.containsKey(usuario.getEmail())) {
            throw new IllegalArgumentException("Já existe usuário com esse e-mail.");
        }
        usuarios.put(usuario.getEmail(), usuario);
    }

    public void salvarEvento(Evento evento) {
        evento.atribuirId(sequenciaEventoId);
        eventos.put(sequenciaEventoId, evento);
        sequenciaEventoId++;
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        Usuario usuario = usuarios.get(email);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        return usuario;
    }

    public UsuarioComum buscarUsuarioComumPorEmail(String email) {
        Usuario usuario = buscarUsuarioPorEmail(email);
        if (!(usuario instanceof UsuarioComum)) {
            throw new IllegalArgumentException("Usuário informado não é um usuário comum.");
        }
        return (UsuarioComum) usuario;
    }

    public Organizador buscarOrganizadorPorEmail(String email) {
        Usuario usuario = buscarUsuarioPorEmail(email);
        throw new IllegalArgumentException("Usuário informado não é um organizador.");
    }

    public Ingresso buscarIngressoPorId(long id) {
        Ingresso ingresso = ingressos.get(id);
        if (ingresso == null) {
            throw new IllegalArgumentException("Ingresso não encontrado.");
        }
        return ingresso;
    }

    public Evento buscarEventoPorId(long id){
        Evento evento = eventos.get(id);
        if (evento== null) {
            throw new IllegalArgumentException("Evento não encontrado.");
        }
        return evento;
    }

    public List<Evento> feedEventos(){
        LocalDateTime agora = LocalDateTime.now();

        return eventos.values()
                .stream()
                .filter(Evento::estaAtivo)
                .filter(e -> e.getDataHoraFim().isAfter(agora))
                .filter(e -> e.calcularVagasDisponiveis() > 0)
                .sorted(Comparator
                        .comparing(Evento::getDataHoraInicio)
                        .thenComparing(Evento::getNome))
                .toList();
    }
}

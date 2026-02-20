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
    private final Map<Long, Usuario> usuarios;
    private final Map<Long, Evento> eventos;

    private long sequenciaIngressoId = 1L;
    private long sequenciaUsuarioId = 1L;

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

    public void salvarUsuario(Usuario usuario){
        usuario.setId(sequenciaUsuarioId);
        usuarios.put(sequenciaUsuarioId, usuario);
        sequenciaUsuarioId++;
    }

    public Usuario buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarios.get(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        return usuario;
    }

    public UsuarioComum buscarUsuarioComumPorId(Long id) {
        Usuario usuario = buscarUsuarioPorId(id);
        if (!(usuario instanceof UsuarioComum)) {
            throw new IllegalArgumentException("Usuário informado não é um usuário comum.");
        }
        return (UsuarioComum) usuario;
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

package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Repositorio {

    private static final Repositorio instance = new Repositorio();
    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final Map<Long, Ingresso> ingressos = new HashMap<>();
    private final Map<Long, Evento> eventos = new HashMap<>();


    private final AtomicLong eventoIdSequence = new AtomicLong(1);
    private long sequenciaIngressoId = 1L;
    private long sequenciaEventoId = 1L;

    private Repositorio() {}

    public static Repositorio getInstance() {
        return instance;
    }


    /** ===================
     *        USUARIO
     *  ===================
     */

    public boolean existeUsuario(String email) {
        return usuarios.containsKey(email);
    }

    public void salvarUsuario(UsuarioComum usuario) {
        if (existeUsuario(usuario.getEmail()))
            throw new IllegalArgumentException("Já existe um usuário com o e-mail: " + usuario.getEmail());
        if (usuario.getEmail() == null || usuario.getEmail().isBlank())
            throw new IllegalArgumentException("E-mail é obrigatório.");
        usuarios.put(usuario.getEmail(), usuario);
    }

    public void salvarUsuario(UsuarioOrganizador usuario) {
        if (existeUsuario(usuario.getEmail())) {
            throw new IllegalArgumentException("Ja existe um usuario com o e-mail: " + usuario.getEmail());
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank())
            throw new IllegalArgumentException("E-mail é obrigatório.");
        usuarios.put(usuario.getEmail(), usuario);
    }


    public UsuarioComum buscarUsuarioComum(String email) {
        Usuario usuario = usuarios.get(email);
        if (usuario instanceof UsuarioComum usuarioComum) {
            return usuarioComum;
        }
        return null;
    }

    public UsuarioOrganizador buscarOrganizador(String email) {
        Usuario usuario = usuarios.get(email);
        if (usuario instanceof UsuarioOrganizador organizador) {
            return organizador;
        }
        return null;
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

    public UsuarioOrganizador buscarOrganizadorPorEmail(String email) {
        Usuario usuario = buscarUsuarioPorEmail(email);
        if (!(usuario instanceof UsuarioOrganizador)) {
            throw new IllegalArgumentException("Usuário informado não é um organizador.");
        }
        return (UsuarioOrganizador) usuario;
    }

    /** ===================
     *        EVENTO
     *  ===================
     */

    public void salvarEvento(Evento evento) {
        long id = eventoIdSequence.getAndIncrement();
        evento.atribuirId(id);
        eventos.put(id, evento);
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
                .filter(e -> e.getDataFinal().isAfter(agora))
                .filter(e -> e.calcularVagasDisponiveis() > 0)
                .sorted(Comparator
                        .comparing(Evento::getDataInicio)
                        .thenComparing(Evento::getNome))
                .toList();
    }

    /** ===================
     *        INGRESSO
     *  ===================
     */

    public void salvarIngresso(Ingresso ingresso){
        ingresso.setId(sequenciaIngressoId);
        ingressos.put(sequenciaIngressoId, ingresso);
        sequenciaIngressoId++;
    }



    public Ingresso buscarIngressoPorId(long id) {
        Ingresso ingresso = ingressos.get(id);
        if (ingresso == null) {
            throw new IllegalArgumentException("Ingresso não encontrado.");
        }
        return ingresso;
    }


}

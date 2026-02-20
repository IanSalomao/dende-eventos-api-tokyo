package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioOrganizador;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Repositorio {

    private static final Repositorio instance = new Repositorio();
    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final Map<Long, Evento> eventos = new HashMap<>();
    private final AtomicLong eventoIdSequence = new AtomicLong(1);

    private Repositorio() {}

    public static Repositorio getInstance() {
        return instance;
    }

    public boolean existeUsuario(String email) {
        return usuarios.containsKey(email);
    }

    public void salvarUsuario(Usuario usuario) {
        if (existeUsuario(usuario.getEmail())) {
            throw new IllegalArgumentException("Ja existe um usuario com o e-mail: " + usuario.getEmail());
        }
        usuarios.put(usuario.getEmail(), usuario);
    }

    public Usuario buscarUsuario(String email) {
        return usuarios.get(email);
    }

    public UsuarioOrganizador buscarOrganizador(String email) {
        Usuario usuario = usuarios.get(email);
        if (usuario instanceof UsuarioOrganizador organizador) {
            return organizador;
        }
        return null;
    }

    public void salvarEvento(Evento evento) {
        long id = eventoIdSequence.getAndIncrement();
        evento.atribuirId(id);
        eventos.put(id, evento);
    }

    public Evento buscarEventoPorId(Long eventoId) {
        return eventos.get(eventoId);
    }
}
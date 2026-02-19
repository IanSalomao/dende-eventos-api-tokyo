package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;

import java.util.HashMap;
import java.util.Map;

public class Repositorio {

    private static Repositorio instance = new Repositorio();
    private final Map<String, Usuario> usuariosComum;
    private final Map<String, Organizador> organizadores;

    private Repositorio() {
        this.usuariosComum = new HashMap<>();
        this.organizadores = new HashMap<>();
    }

    public static Repositorio getInstance() {
        return instance;
    }

    public void salvarOrganizador(Organizador organizador) {
        if (usuariosComum.containsKey(organizador.getEmail()) || organizadores.containsKey(organizador.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado na plataforma.");
        }
        organizadores.put(organizador.getEmail(), organizador);
    }

    public Organizador buscarOrganizador(String email) {
        return organizadores.get(email);
    }

    public Usuario buscarUsuarioQualquer(String email) {
        if (usuariosComum.containsKey(email)) return usuariosComum.get(email);
        if (organizadores.containsKey(email)) return organizadores.get(email);
        return null;
    }
}

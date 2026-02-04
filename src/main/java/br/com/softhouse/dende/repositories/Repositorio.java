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

}

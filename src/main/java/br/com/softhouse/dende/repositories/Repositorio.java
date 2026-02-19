package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Usuario;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Repositorio {

    private static final Repositorio instance = new Repositorio();
    private final Map<String, Usuario> usuarios;

    private Repositorio() {
        this.usuarios = new HashMap<>();
    }

    public static Repositorio getInstance() {
        return instance;
    }

    public boolean existeUsuario(String email) {
        return usuarios.containsKey(email);
    }

    public void salvarUsuario(Usuario usuario) {
        usuarios.put(usuario.getEmail(), usuario);
    }

    public Optional<Usuario> buscarUsuario(String email) {
        return Optional.ofNullable(usuarios.get(email));
    }

    public void atualizarUsuario(String email, Usuario dadosNovos) {
        buscarUsuario(email).ifPresent(usuario -> usuario.alterarPerfil(dadosNovos));
    }

    public void desativarUsuario(String email) {
        buscarUsuario(email).ifPresent(Usuario::desativarUsuario);
    }

    public void reativarUsuario(String email, String senha) {
        buscarUsuario(email).ifPresent(usuario -> usuario.reativarUsuario(email, senha));
    }
}
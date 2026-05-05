package br.com.softhouse.dende.exceptions;

public class UsuarioNaoEncontradoException extends RuntimeException {

    public UsuarioNaoEncontradoException(String email) {
        super("Usuário não encontrado com o e-mail: " + email);
    }
}

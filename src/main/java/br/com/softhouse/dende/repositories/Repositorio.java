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

    // [AVALIAÇÃO - Item 12] Há apenas uma coleção de usuários indexada por email (String).
    // Não existe uma segunda coleção por ID, o que torna buscas por ID ineficientes.
    // Sugestão: criar uma chave composta UsuarioKey com os campos 'id' e 'email',
    // e uma única coleção: private Map<UsuarioKey, Usuario> usuarios = new HashMap<>();
    // Inserção: usuarios.put(new UsuarioKey(usuario.getId(), usuario.getEmail()), usuario);
    // Busca por email: usuarios.entrySet().stream().filter(e -> e.getKey().email().equals(email)).findFirst()
    // Busca por id:    usuarios.entrySet().stream().filter(e -> e.getKey().id().equals(id)).findFirst()
    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final Map<Long, Ingresso> ingressos = new HashMap<>();
    private final Map<Long, Evento> eventos = new HashMap<>();

    // [AVALIAÇÃO - Item 13] Há um contador para eventos (eventoIdSequence) e outro para ingressos
    // (sequenciaIngressoId), mas NÃO existe um contador para usuários. Cada entidade deve crescer
    // de forma independente com seu próprio sequenciador.
    // Sugestão: adicione: private final AtomicLong usuarioIdSequence = new AtomicLong(1);
    // E em salvarUsuario: usuario.atribuirId(usuarioIdSequence.getAndIncrement());

    // [AVALIAÇÃO - Item 13] O contador de ingressos usa 'long' simples com incremento manual (++),
    // enquanto o de eventos usa AtomicLong (thread-safe). Para consistência e segurança, todos os
    // contadores deveriam ser AtomicLong.
    // Código sugerido: private final AtomicLong ingressoIdSequence = new AtomicLong(1);
    private final AtomicLong eventoIdSequence = new AtomicLong(1);
    private long sequenciaIngressoId = 1L;

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

    // [AVALIAÇÃO - Item 3] A verificação de existência de usuário ocorre ANTES da verificação de email nulo.
    // Se o email for null, a chamada a existeUsuario(email) pode gerar comportamento inesperado (HashMap aceita
    // null como chave). A ordem correta é validar o null primeiro.
    // Sugestão: inverta a ordem das verificações. Considere também:
    // Objects.requireNonNull(usuario.getEmail(), "E-mail é obrigatório.");
    public void salvarUsuario(UsuarioComum usuario) {
        if (existeUsuario(usuario.getEmail()))
            throw new IllegalArgumentException("Já existe um usuário com o e-mail: " + usuario.getEmail());
        if (usuario.getEmail() == null || usuario.getEmail().isBlank())
            throw new IllegalArgumentException("E-mail é obrigatório.");
        usuarios.put(usuario.getEmail(), usuario);
    }

    // [AVALIAÇÃO - Item 3] Mesmo problema de ordenação das verificações presente em salvarUsuario(UsuarioComum).
    // Sugestão: verifique o null do email ANTES de chamar existeUsuario().
    public void salvarUsuario(UsuarioOrganizador usuario) {
        if (existeUsuario(usuario.getEmail())) {
            throw new IllegalArgumentException("Ja existe um usuario com o e-mail: " + usuario.getEmail());
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank())
            throw new IllegalArgumentException("E-mail é obrigatório.");
        usuarios.put(usuario.getEmail(), usuario);
    }

    // [AVALIAÇÃO - Item 8] O método retorna null quando o usuário não é encontrado ou não é do tipo UsuarioComum.
    // Retornar null força todos os chamadores a verificar 'if (usuario == null)', espalhando esse padrão pela aplicação.
    // Sugestão: altere o tipo de retorno para Optional<UsuarioComum>.
    // Código sugerido:
    // public Optional<UsuarioComum> buscarUsuarioComum(String email) {
    //     Usuario usuario = usuarios.get(email);
    //     return (usuario instanceof UsuarioComum uc) ? Optional.of(uc) : Optional.empty();
    // }
    public UsuarioComum buscarUsuarioComum(String email) {
        Usuario usuario = usuarios.get(email);
        if (usuario instanceof UsuarioComum usuarioComum) {
            return usuarioComum;
        }
        return null;
    }

    // [AVALIAÇÃO - Item 8] Mesmo problema de buscarUsuarioComum: retorna null em vez de Optional.
    // Sugestão:
    // public Optional<UsuarioOrganizador> buscarOrganizador(String email) {
    //     Usuario usuario = usuarios.get(email);
    //     return (usuario instanceof UsuarioOrganizador org) ? Optional.of(org) : Optional.empty();
    // }
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

    public Evento buscarEventoPorId(long id) {
        Evento evento = eventos.get(id);
        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado.");
        }
        return evento;
    }

    public List<Evento> feedEventos() {
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

    // [AVALIAÇÃO - Item 11] O incremento sequencial está correto (atribui o ID atual e depois avança o contador).
    // Porém, para consistência com eventoIdSequence (AtomicLong), considere adotar AtomicLong aqui também.
    // Código sugerido:
    // private final AtomicLong ingressoIdSequence = new AtomicLong(1);
    // ingresso.setId(ingressoIdSequence.getAndIncrement());
    // ingressos.put(ingresso.getId(), ingresso);
    public void salvarIngresso(Ingresso ingresso) {
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

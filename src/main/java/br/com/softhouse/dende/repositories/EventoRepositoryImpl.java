package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.exceptions.EventoNaoEncontradoException;
import br.com.softhouse.dende.exceptions.OperacaoNaoPermitidaException;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.UsuarioOrganizador;
import br.com.softhouse.dende.model.enums.ModalidadeEvento;
import br.com.softhouse.dende.model.enums.StatusEvento;
import br.com.softhouse.dende.model.enums.TipoEvento;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.softhouse.dende.repositories.util.CrudRepository;
import br.com.softhouse.dende.repositories.util.RowMapper;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventoRepositoryImpl implements CrudRepository<Evento, Long> {

    private final UsuarioRepositoryImpl usuarioRepository;

    public EventoRepositoryImpl() {
        this.usuarioRepository = new UsuarioRepositoryImpl();
    }

    @Override
    public void save(Evento evento) {
        String sql = """
                INSERT INTO evento (
                    organizador_id, evento_principal_id,
                    nome, descricao, pagina_web,
                    tipo_evento, modalidade, local_evento,
                    data_inicio, data_fim,
                    capacidade_maxima, preco_ingresso,
                    estorna_ingresso, taxa_estorno, ativo
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherStatement(stmt, evento, conn);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) evento.atribuirId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao salvar evento: " + e.getMessage());
        }
    }

    @Override
    public Evento findById(Long id) {
        String sql = "SELECT * FROM evento WHERE id = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return new EventoRowMapper().mapRow(rs);
            }
        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao buscar evento: " + e.getMessage());
        }
        throw new EventoNaoEncontradoException(id);
    }

    @Override
    public List<Evento> findAll() {
        String sql = "SELECT * FROM evento";
        List<Evento> eventos = new ArrayList<>();
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            EventoRowMapper mapper = new EventoRowMapper();
            while (rs.next()) eventos.add(mapper.mapRow(rs));

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao listar eventos: " + e.getMessage());
        }
        return eventos;
    }

    @Override
    public void update(Evento evento) {
        String sql = """
                UPDATE evento SET
                    organizador_id = ?, evento_principal_id = ?,
                    nome = ?, descricao = ?, pagina_web = ?,
                    tipo_evento = ?, modalidade = ?, local_evento = ?,
                    data_inicio = ?, data_fim = ?,
                    capacidade_maxima = ?, preco_ingresso = ?,
                    estorna_ingresso = ?, taxa_estorno = ?, ativo = ?
                WHERE id = ?
                """;

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preencherStatement(stmt, evento, conn);
            stmt.setLong(16, evento.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao atualizar evento: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM evento WHERE id = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao deletar evento: " + e.getMessage());
        }
    }

    public List<Evento> findByOrganizador(String emailOrganizador) {
        String sql = """
                SELECT e.* FROM evento e
                INNER JOIN usuario u ON u.id = e.organizador_id
                WHERE u.email = ?
                """;
        return buscarLista(sql, stmt -> stmt.setString(1, emailOrganizador),
                "Erro ao buscar eventos por organizador");
    }

    public List<Evento> findByStatus(StatusEvento status) {
        String sql = "SELECT * FROM evento WHERE ativo = ?";
        return buscarLista(sql,
                stmt -> stmt.setBoolean(1, status == StatusEvento.ATIVO),
                "Erro ao buscar eventos por status");
    }

    public List<Evento> findFeedPublico(TipoEvento tipo, ModalidadeEvento modalidade, String nomeParcial) {
        StringBuilder sql = new StringBuilder("""
                SELECT e.* FROM evento e
                WHERE e.ativo = 1
                  AND e.data_fim > NOW()
                  AND (SELECT COUNT(*) FROM ingresso i
                       WHERE i.evento_id = e.id AND i.status = 'ATIVO') < e.capacidade_maxima
                """);

        if (tipo != null)        sql.append(" AND e.tipo_evento = ?");
        if (modalidade != null)  sql.append(" AND e.modalidade = ?");
        if (nomeParcial != null) sql.append(" AND e.nome LIKE ?");
        sql.append(" ORDER BY e.data_inicio ASC");

        List<Evento> eventos = new ArrayList<>();
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (tipo != null)        stmt.setString(idx++, tipo.name());
            if (modalidade != null)  stmt.setString(idx++, modalidade.name());
            if (nomeParcial != null) stmt.setString(idx, "%" + nomeParcial + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                EventoRowMapper mapper = new EventoRowMapper();
                while (rs.next()) eventos.add(mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao buscar feed público: " + e.getMessage());
        }
        return eventos;
    }

    public List<Evento> findSubEventos(Long eventoPrincipalId) {
        String sql = "SELECT * FROM evento WHERE evento_principal_id = ?";
        return buscarLista(sql,
                stmt -> stmt.setLong(1, eventoPrincipalId),
                "Erro ao buscar sub-eventos");
    }

    private void preencherStatement(PreparedStatement stmt, Evento evento, Connection conn)
            throws SQLException {

        long organizadorId = buscarUsuarioId(conn, evento.getOrganizador().getEmail());
        stmt.setLong(1, organizadorId);

        if (evento.getEventoPrincipal() != null) {
            stmt.setLong(2, evento.getEventoPrincipal().getId());
        } else {
            stmt.setNull(2, Types.BIGINT);
        }

        stmt.setString(3, evento.getNome());
        stmt.setString(4, evento.getDescricao());
        stmt.setString(5, evento.getPaginaEvento());
        stmt.setString(6, evento.getTipo().name());
        stmt.setString(7, evento.getModalidade().name());
        stmt.setString(8, evento.getLocalAcesso());
        stmt.setTimestamp(9, Timestamp.valueOf(evento.getDataInicio()));
        stmt.setTimestamp(10, Timestamp.valueOf(evento.getDataFinal()));

        stmt.setInt(11, evento.getCapacidadeMaxima() != null ? evento.getCapacidadeMaxima() : 0);

        stmt.setBigDecimal(12, evento.getPrecoIngresso() != null
                ? evento.getPrecoIngresso() : BigDecimal.ZERO);

        stmt.setBoolean(13, Boolean.TRUE.equals(evento.isPermiteEstorno()));

        stmt.setBigDecimal(14, evento.getTaxaEstorno() != null
                ? evento.getTaxaEstorno() : BigDecimal.ZERO);

        stmt.setBoolean(15, evento.getStatus() == StatusEvento.ATIVO);
    }

    private long buscarUsuarioId(Connection conn, String email) throws SQLException {
        String sql = "SELECT id FROM usuario WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        throw new SQLException("Organizador não encontrado para o email: " + email);
    }

    @FunctionalInterface
    private interface StatementConfig {
        void configure(PreparedStatement stmt) throws SQLException;
    }

    private List<Evento> buscarLista(String sql, StatementConfig config, String mensagemErro) {
        List<Evento> eventos = new ArrayList<>();
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            config.configure(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                EventoRowMapper mapper = new EventoRowMapper();
                while (rs.next()) eventos.add(mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException(mensagemErro + ": " + e.getMessage());
        }
        return eventos;
    }

    private class EventoRowMapper implements RowMapper<Evento> {

        @Override
        public Evento mapRow(ResultSet rs) throws SQLException {
            LocalDateTime dataInicio = rs.getTimestamp("data_inicio").toLocalDateTime();
            LocalDateTime dataFim    = rs.getTimestamp("data_fim").toLocalDateTime();

            TipoEvento tipo             = TipoEvento.valueOf(rs.getString("tipo_evento"));
            ModalidadeEvento modalidade = ModalidadeEvento.valueOf(rs.getString("modalidade"));

            int cap = rs.getInt("capacidade_maxima");
            Integer capacidadeMaxima = rs.wasNull() ? null : cap;

            BigDecimal preco       = rs.getBigDecimal("preco_ingresso");
            BigDecimal taxaEstorno = rs.getBigDecimal("taxa_estorno");
            boolean permiteEstorno = rs.getBoolean("estorna_ingresso");

            // evento_principal: shallow (só id)
            long principalId = rs.getLong("evento_principal_id");
            Evento eventoPrincipal = null;
            if (!rs.wasNull()) {
                eventoPrincipal = new Evento();
                eventoPrincipal.atribuirId(principalId);
            }

            Evento evento = new Evento(
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    rs.getString("pagina_web"),
                    dataInicio,
                    dataFim,
                    tipo,
                    modalidade,
                    capacidadeMaxima,
                    rs.getString("local_evento"),
                    preco,
                    permiteEstorno,
                    taxaEstorno,
                    eventoPrincipal
            );

            evento.atribuirId(rs.getLong("id"));

            long organizadorId = rs.getLong("organizador_id");
            String emailOrganizador = buscarEmailOrganizador(organizadorId);
            if (emailOrganizador != null) {
                UsuarioOrganizador organizador = new UsuarioOrganizador(emailOrganizador);
                evento.atribuirOrganizador(organizador);
            }

            boolean ativo = rs.getBoolean("ativo");
            evento.restaurarStatus(ativo ? StatusEvento.ATIVO : StatusEvento.INATIVO);

            return evento;
        }

        private String buscarEmailOrganizador(long organizadorId) {
            String sql = "SELECT email FROM usuario WHERE id = ?";
            try (Connection conn = ConnectionPool.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, organizadorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return rs.getString("email");
                }
            } catch (SQLException e) {
                throw new OperacaoNaoPermitidaException(
                        "Erro ao buscar email do organizador: " + e.getMessage());
            }
            return null;
        }
    }
}
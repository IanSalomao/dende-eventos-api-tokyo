package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.exceptions.IngressoNaoEncontradoException;
import br.com.softhouse.dende.exceptions.OperacaoNaoPermitidaException;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.model.enums.StatusIngresso;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.softhouse.dende.repositories.util.CrudRepository;
import br.com.softhouse.dende.repositories.util.RowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngressoRepositoryImpl implements CrudRepository<Ingresso, Long> {

    private final EventoRepositoryImpl eventoRepository;
    private final UsuarioRepositoryImpl usuarioRepository;

    public IngressoRepositoryImpl(EventoRepositoryImpl eventoRepository,
                                  UsuarioRepositoryImpl usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
    }


    @Override
    public void save(Ingresso ingresso) {
        String sql = """
                INSERT INTO ingresso (usuario_id, evento_id, valor_pago, status, data_compra)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, buscarUsuarioId(conn, ingresso.getUsuario().getEmail()));
            stmt.setLong(2, ingresso.getEvento().getId());
            stmt.setBigDecimal(3, ingresso.getValorPago());
            stmt.setString(4, toStatusBanco(ingresso.getStatus()));
            stmt.setTimestamp(5, Timestamp.valueOf(ingresso.getDataCompra()));
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) ingresso.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao salvar ingresso: " + e.getMessage());
        }
    }

    @Override
    public Ingresso findById(Long id) {
        String sql = "SELECT * FROM ingresso WHERE id = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return new IngressoRowMapper(eventoRepository, usuarioRepository).mapRow(rs);
            }
        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao buscar ingresso: " + e.getMessage());
        }
        throw new IngressoNaoEncontradoException(id);
    }

    @Override
    public List<Ingresso> findAll() {
        String sql = "SELECT * FROM ingresso";
        List<Ingresso> ingressos = new ArrayList<>();
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            IngressoRowMapper mapper = new IngressoRowMapper(eventoRepository, usuarioRepository);
            while (rs.next()) ingressos.add(mapper.mapRow(rs));

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao listar ingressos: " + e.getMessage());
        }
        return ingressos;
    }

    @Override
    public void update(Ingresso ingresso) {
        String sql = """
                UPDATE ingresso SET
                    status = ?,
                    valor_estornado = ?,
                    data_cancelamento = ?
                WHERE id = ?
                """;
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, toStatusBanco(ingresso.getStatus()));

            boolean cancelado = ingresso.estaCancelado();
            stmt.setBigDecimal(2, cancelado
                    ? ingresso.getEvento().calcularValorEstorno(ingresso)
                    : java.math.BigDecimal.ZERO);

            if (cancelado) {
                stmt.setTimestamp(3, Timestamp.valueOf(java.time.LocalDateTime.now()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }

            stmt.setLong(4, ingresso.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao atualizar ingresso: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM ingresso WHERE id = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao deletar ingresso: " + e.getMessage());
        }
    }

    public List<Ingresso> findByUsuario(String emailUsuario) {
        String sql = """
                SELECT i.* FROM ingresso i
                INNER JOIN usuario u ON u.id = i.usuario_id
                WHERE u.email = ?
                ORDER BY i.data_compra DESC
                """;
        return buscarLista(sql, stmt -> stmt.setString(1, emailUsuario),
                "Erro ao buscar ingressos por usuário");
    }

    public List<Ingresso> findByEvento(Long eventoId) {
        String sql = "SELECT * FROM ingresso WHERE evento_id = ? ORDER BY data_compra DESC";
        return buscarLista(sql, stmt -> stmt.setLong(1, eventoId),
                "Erro ao buscar ingressos por evento");
    }

    public int countAtivosByEvento(Long eventoId) {
        String sql = "SELECT COUNT(*) FROM ingresso WHERE evento_id = ? AND status = 'ATIVO'";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, eventoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao contar ingressos ativos: " + e.getMessage());
        }
        return 0;
    }

    public void cancelarAtivosByEvento(Long eventoId) {
        String sql = """
                UPDATE ingresso
                SET status = 'CANCELADO', data_cancelamento = NOW()
                WHERE evento_id = ? AND status = 'ATIVO'
                """;
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, eventoId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao cancelar ingressos do evento: " + e.getMessage());
        }
    }

    public void saveAll(List<Ingresso> ingressos) {
        String sql = """
                INSERT INTO ingresso (usuario_id, evento_id, valor_pago, status, data_compra)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectionPool.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                for (Ingresso ingresso : ingressos) {
                    stmt.setLong(1, buscarUsuarioId(conn, ingresso.getUsuario().getEmail()));
                    stmt.setLong(2, ingresso.getEvento().getId());
                    stmt.setBigDecimal(3, ingresso.getValorPago());
                    stmt.setString(4, toStatusBanco(ingresso.getStatus()));
                    stmt.setTimestamp(5, Timestamp.valueOf(ingresso.getDataCompra()));
                    stmt.addBatch();
                }

                stmt.executeBatch();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    int i = 0;
                    while (keys.next()) {
                        ingressos.get(i++).setId(keys.getLong(1));
                    }
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw new OperacaoNaoPermitidaException("Erro na transação de ingressos: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro de conexão ao salvar ingressos: " + e.getMessage());
        }
    }

    private String toStatusBanco(StatusIngresso status) {
        return status == StatusIngresso.ATIVO ? "ATIVO" : "CANCELADO";
    }

    private long buscarUsuarioId(Connection conn, String email) throws SQLException {
        String sql = "SELECT id FROM usuario WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        throw new SQLException("Usuário não encontrado para o email: " + email);
    }

    @FunctionalInterface
    private interface StatementConfig {
        void configure(PreparedStatement stmt) throws SQLException;
    }

    private List<Ingresso> buscarLista(String sql, StatementConfig config, String mensagemErro) {
        List<Ingresso> ingressos = new ArrayList<>();
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            config.configure(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                IngressoRowMapper mapper = new IngressoRowMapper(eventoRepository, usuarioRepository);
                while (rs.next()) ingressos.add(mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException(mensagemErro + ": " + e.getMessage());
        }
        return ingressos;
    }


    private static class IngressoRowMapper implements RowMapper<Ingresso> {

        private final EventoRepositoryImpl eventoRepository;
        private final UsuarioRepositoryImpl usuarioRepository;

        IngressoRowMapper(EventoRepositoryImpl eventoRepository,
                          UsuarioRepositoryImpl usuarioRepository) {
            this.eventoRepository = eventoRepository;
            this.usuarioRepository = usuarioRepository;
        }

        @Override
        public Ingresso mapRow(ResultSet rs) throws SQLException {
            Long eventoId   = rs.getLong("evento_id");
            Long usuarioId  = rs.getLong("usuario_id");

            Evento evento = eventoRepository.findById(eventoId);

            String emailUsuario = buscarEmailUsuario(usuarioId);
            UsuarioComum usuario = (UsuarioComum) usuarioRepository.findById(emailUsuario);

            Ingresso ingresso = Ingresso.criar(evento, usuario, rs.getBigDecimal("valor_pago"));
            ingresso.setId(rs.getLong("id"));

            String statusBanco = rs.getString("status");
            if ("CANCELADO".equals(statusBanco)) {
                ingresso.cancelarPorEvento();
            }

            return ingresso;
        }

        private String buscarEmailUsuario(Long usuarioId) throws SQLException {
            String sql = "SELECT email FROM usuario WHERE id = ?";
            try (Connection conn = ConnectionPool.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, usuarioId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return rs.getString("email");
                }
            }
            throw new SQLException("Usuário não encontrado com id: " + usuarioId);
        }
    }
}
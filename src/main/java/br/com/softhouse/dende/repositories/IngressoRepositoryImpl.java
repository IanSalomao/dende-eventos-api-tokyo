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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IngressoRepositoryImpl implements CrudRepository<Ingresso, Long> {

    private final EventoRepositoryImpl eventoRepository;
    private final UsuarioRepositoryImpl usuarioRepository;

    public IngressoRepositoryImpl(EventoRepositoryImpl eventoRepository, UsuarioRepositoryImpl usuarioRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void save(Ingresso ingresso) {
        String sql = """
                INSERT INTO ingressos (valor_pago, status, data_compra, evento_id, usuario_email)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setBigDecimal(1, ingresso.getValorPago());
            stmt.setString(2, ingresso.getStatus().name());
            stmt.setTimestamp(3, Timestamp.valueOf(ingresso.getDataCompra()));
            stmt.setLong(4, ingresso.getEvento().getId());
            stmt.setString(5, ingresso.getUsuario().getEmail());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    ingresso.setId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao salvar ingresso: " + e.getMessage());
        }
    }

    @Override
    public Ingresso findById(Long id) {
        String sql = "SELECT * FROM ingressos WHERE id = ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new IngressoRowMapper().mapRow(rs);
                }
            }

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao buscar ingresso: " + e.getMessage());
        }

        throw new IngressoNaoEncontradoException(id);
    }

    @Override
    public List<Ingresso> findAll() {
        String sql = "SELECT * FROM ingressos";
        List<Ingresso> ingressos = new ArrayList<>();

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            IngressoRowMapper mapper = new IngressoRowMapper();
            while (rs.next()) {
                ingressos.add(mapper.mapRow(rs));
            }

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao listar ingressos: " + e.getMessage());
        }

        return ingressos;
    }

    @Override
    public void update(Ingresso ingresso) {
        String sql = """
                UPDATE ingressos SET
                    valor_pago = ?, status = ?, data_compra = ?,
                    evento_id = ?, usuario_email = ?
                WHERE id = ?
                """;

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, ingresso.getValorPago());
            stmt.setString(2, ingresso.getStatus().name());
            stmt.setTimestamp(3, Timestamp.valueOf(ingresso.getDataCompra()));
            stmt.setLong(4, ingresso.getEvento().getId());
            stmt.setString(5, ingresso.getUsuario().getEmail());
            stmt.setLong(6, ingresso.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao atualizar ingresso: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM ingressos WHERE id = ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao deletar ingresso: " + e.getMessage());
        }
    }



    public List<Ingresso> findByUsuario(String emailUsuario) {
        String sql = "SELECT * FROM ingressos WHERE usuario_email = ? ORDER BY data_compra DESC";
        List<Ingresso> ingressos = new ArrayList<>();

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emailUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                IngressoRowMapper mapper = new IngressoRowMapper();
                while (rs.next()) {
                    ingressos.add(mapper.mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao buscar ingressos por usuário: " + e.getMessage());
        }

        return ingressos;
    }

    public List<Ingresso> findByEvento(Long eventoId) {
        String sql = "SELECT * FROM ingressos WHERE evento_id = ? ORDER BY data_compra DESC";
        List<Ingresso> ingressos = new ArrayList<>();

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, eventoId);

            try (ResultSet rs = stmt.executeQuery()) {
                IngressoRowMapper mapper = new IngressoRowMapper();
                while (rs.next()) {
                    ingressos.add(mapper.mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao buscar ingressos por evento: " + e.getMessage());
        }

        return ingressos;
    }

    public int countAtivosByEvento(Long eventoId) {
        String sql = "SELECT COUNT(*) FROM ingressos WHERE evento_id = ? AND status = 'ATIVO'";
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
        String sql = "UPDATE ingressos SET status = 'CANCELADO_PELO_EVENTO' WHERE evento_id = ? AND status = 'ATIVO'";
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
                INSERT INTO ingressos (valor_pago, status, data_compra, evento_id, usuario_email)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConnectionPool.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (Ingresso ingresso : ingressos) {
                    stmt.setBigDecimal(1, ingresso.getValorPago());
                    stmt.setString(2, ingresso.getStatus().name());
                    stmt.setTimestamp(3, Timestamp.valueOf(ingresso.getDataCompra()));
                    stmt.setLong(4, ingresso.getEvento().getId());
                    stmt.setString(5, ingresso.getUsuario().getEmail());
                    stmt.addBatch();
                }

                stmt.executeBatch();

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    int i = 0;
                    while (keys.next()) {
                        ingressos.get(i).setId(keys.getLong(1));
                        i++;
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



    private class IngressoRowMapper implements RowMapper<Ingresso> {

        @Override
        public Ingresso mapRow(ResultSet rs) throws SQLException {
            Long eventoId = rs.getLong("evento_id");
            String usuarioEmail = rs.getString("usuario_email");

            Evento evento = eventoRepository.findById(eventoId);
            UsuarioComum usuario = (UsuarioComum) usuarioRepository.findById(usuarioEmail);

            Ingresso ingresso = Ingresso.criar(evento, usuario, rs.getBigDecimal("valor_pago"));
            ingresso.setId(rs.getLong("id"));

            StatusIngresso status = StatusIngresso.valueOf(rs.getString("status"));
            restaurarStatus(ingresso, status);

            return ingresso;
        }

        private void restaurarStatus(Ingresso ingresso, StatusIngresso statusPersistido) {
            switch (statusPersistido) {
                case CANCELADO_PELO_USUARIO -> ingresso.cancelarIngresso();
                case CANCELADO_PELO_EVENTO  -> ingresso.cancelarPorEvento();
                case ATIVO -> { }
            }
        }
    }
}
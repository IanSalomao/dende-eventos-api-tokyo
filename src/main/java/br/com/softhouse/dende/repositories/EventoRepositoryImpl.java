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
    @Override
    public void save(Evento evento) {
        String sql = """
                INSERT INTO eventos (
                    nome, descricao, pagina_evento,
                    data_inicio, data_final,
                    tipo, modalidade,
                    capacidade_maxima, local_acesso,
                    status, preco_ingresso,
                    permite_estorno, taxa_estorno,
                    evento_principal_id, organizador_email
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherStatement(stmt, evento);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    evento.atribuirId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao salvar evento: " + e.getMessage());
        }
    }

    @Override
    public Evento findById(Long id) {
        String sql = "SELECT * FROM eventos WHERE id = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new EventoRowMapper().mapRow(rs);
                }
            }

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao buscar evento: " + e.getMessage());
        }

        throw new EventoNaoEncontradoException(id);
    }

    @Override
    public List<Evento> findAll() {
        String sql = "SELECT * FROM eventos";
        List<Evento> eventos = new ArrayList<>();

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            EventoRowMapper mapper = new EventoRowMapper();
            while (rs.next()) {
                eventos.add(mapper.mapRow(rs));
            }

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao listar eventos: " + e.getMessage());
        }

        return eventos;
    }

    @Override
    public void update(Evento evento) {
        String sql = """
                UPDATE eventos SET
                    nome = ?, descricao = ?, pagina_evento = ?,
                    data_inicio = ?, data_final = ?,
                    tipo = ?, modalidade = ?,
                    capacidade_maxima = ?, local_acesso = ?,
                    status = ?, preco_ingresso = ?,
                    permite_estorno = ?, taxa_estorno = ?,
                    evento_principal_id = ?, organizador_email = ?
                WHERE id = ?
                """;

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preencherStatement(stmt, evento);
            stmt.setLong(16, evento.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao atualizar evento: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM eventos WHERE id = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao deletar evento: " + e.getMessage());
        }
    }


    // consultas
    public List<Evento> findByOrganizador(String emailOrganizador) {
        String sql = "SELECT * FROM eventos WHERE organizador_email = ?";
        List<Evento> eventos = new ArrayList<>();

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emailOrganizador);

            try (ResultSet rs = stmt.executeQuery()) {
                EventoRowMapper mapper = new EventoRowMapper();
                while (rs.next()) {
                    eventos.add(mapper.mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao buscar eventos por organizador: " + e.getMessage());
        }

        return eventos;
    }

    public List<Evento> findByStatus(StatusEvento status) {
        String sql = "SELECT * FROM eventos WHERE status = ?";
        List<Evento> eventos = new ArrayList<>();

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                EventoRowMapper mapper = new EventoRowMapper();
                while (rs.next()) {
                    eventos.add(mapper.mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao buscar eventos por status: " + e.getMessage());
        }

        return eventos;
    }

    public List<Evento> findFeedPublico(TipoEvento tipo, ModalidadeEvento modalidade, String nomeParcial) {
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM eventos WHERE status = 'ATIVO' AND data_final > NOW() " +
                        "AND (capacidade_maxima IS NULL OR " +
                        "(SELECT COUNT(*) FROM ingressos WHERE evento_id = eventos.id AND status = 'ATIVO') < capacidade_maxima)"
        );

        if (tipo != null)         sql.append(" AND tipo = ?");
        if (modalidade != null)   sql.append(" AND modalidade = ?");
        if (nomeParcial != null)  sql.append(" AND nome LIKE ?");

        sql.append(" ORDER BY data_inicio ASC");

        List<Evento> eventos = new ArrayList<>();

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (tipo != null)        stmt.setString(idx++, tipo.name());
            if (modalidade != null)  stmt.setString(idx++, modalidade.name());
            if (nomeParcial != null) stmt.setString(idx, "%" + nomeParcial + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                EventoRowMapper mapper = new EventoRowMapper();
                while (rs.next()) {
                    eventos.add(mapper.mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao buscar feed público de eventos: " + e.getMessage());
        }

        return eventos;
    }

    public List<Evento> findSubEventos(Long eventoPrincipalId) {
        String sql = "SELECT * FROM eventos WHERE evento_principal_id = ?";
        List<Evento> eventos = new ArrayList<>();

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, eventoPrincipalId);

            try (ResultSet rs = stmt.executeQuery()) {
                EventoRowMapper mapper = new EventoRowMapper();
                while (rs.next()) {
                    eventos.add(mapper.mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new OperacaoNaoPermitidaException("Erro ao buscar sub-eventos: " + e.getMessage());
        }

        return eventos;
    }


    // helpers internos
    private void preencherStatement(PreparedStatement stmt, Evento evento) throws SQLException {
        stmt.setString(1, evento.getNome());
        stmt.setString(2, evento.getDescricao());
        stmt.setString(3, evento.getPaginaEvento());
        stmt.setTimestamp(4, Timestamp.valueOf(evento.getDataInicio()));
        stmt.setTimestamp(5, Timestamp.valueOf(evento.getDataFinal()));
        stmt.setString(6, evento.getTipo().name());
        stmt.setString(7, evento.getModalidade().name());

        if (evento.getCapacidadeMaxima() != null) {
            stmt.setInt(8, evento.getCapacidadeMaxima());
        } else {
            stmt.setNull(8, Types.INTEGER);
        }

        stmt.setString(9, evento.getLocalAcesso());
        stmt.setString(10, evento.getStatus().name());

        if (evento.getPrecoIngresso() != null) {
            stmt.setBigDecimal(11, evento.getPrecoIngresso());
        } else {
            stmt.setNull(11, Types.DECIMAL);
        }

        if (evento.isPermiteEstorno() != null) {
            stmt.setBoolean(12, evento.isPermiteEstorno());
        } else {
            stmt.setNull(12, Types.BOOLEAN);
        }

        if (evento.getTaxaEstorno() != null) {
            stmt.setBigDecimal(13, evento.getTaxaEstorno());
        } else {
            stmt.setNull(13, Types.DECIMAL);
        }

        if (evento.getEventoPrincipal() != null) {
            stmt.setLong(14, evento.getEventoPrincipal().getId());
        } else {
            stmt.setNull(14, Types.BIGINT);
        }

        if (evento.getOrganizador() != null) {
            stmt.setString(15, evento.getOrganizador().getEmail());
        } else {
            stmt.setNull(15, Types.VARCHAR);
        }
    }

    private static class EventoRowMapper implements RowMapper<Evento> {

        @Override
        public Evento mapRow(ResultSet rs) throws SQLException {
            LocalDateTime dataInicio = rs.getTimestamp("data_inicio").toLocalDateTime();
            LocalDateTime dataFinal  = rs.getTimestamp("data_final").toLocalDateTime();

            TipoEvento tipo           = TipoEvento.valueOf(rs.getString("tipo"));
            ModalidadeEvento modalidade = ModalidadeEvento.valueOf(rs.getString("modalidade"));
            StatusEvento status       = StatusEvento.valueOf(rs.getString("status"));

            String capacidadeStr = rs.getString("capacidade_maxima");
            Integer capacidadeMaxima = capacidadeStr != null ? rs.getInt("capacidade_maxima") : null;

            BigDecimal precoIngresso = rs.getBigDecimal("preco_ingresso");
            BigDecimal taxaEstorno   = rs.getBigDecimal("taxa_estorno");

            boolean permiteEstornoRaw = rs.getBoolean("permite_estorno");
            Boolean permiteEstorno = rs.wasNull() ? null : permiteEstornoRaw;

            long eventoPrincipalId = rs.getLong("evento_principal_id");
            Evento eventoPrincipal = null;
            if (!rs.wasNull()) {
                eventoPrincipal = new Evento();
                eventoPrincipal.atribuirId(eventoPrincipalId);
            }

            Evento evento = new Evento(
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    rs.getString("pagina_evento"),
                    dataInicio,
                    dataFinal,
                    tipo,
                    modalidade,
                    capacidadeMaxima,
                    rs.getString("local_acesso"),
                    precoIngresso,
                    permiteEstorno,
                    taxaEstorno,
                    eventoPrincipal
            );

            evento.atribuirId(rs.getLong("id"));

            String emailOrganizador = rs.getString("organizador_email");
            if (emailOrganizador != null) {
                UsuarioOrganizador organizador = new UsuarioOrganizador(emailOrganizador);
                evento.atribuirOrganizador(organizador);
            }

            restaurarStatus(evento, status);

            return evento;
        }

        private void restaurarStatus(Evento evento, StatusEvento statusPersistido) {
            evento.restaurarStatus(statusPersistido);
        }
    }
}
package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.exceptions.DadosInvalidosException;
import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.softhouse.dende.repositories.util.CrudRepository;
import br.com.softhouse.dende.repositories.util.RowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpresaRepositoryImpl implements CrudRepository<Empresa, String> {

    public void save(Empresa empresa, Long organizadorId) {
        String sql = """
                INSERT INTO empresa (organizador_id, cnpj, razao_social, nome_fantasia)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, organizadorId);
            stmt.setString(2, empresa.getCnpj());
            stmt.setString(3, empresa.getRazaoSocial());
            stmt.setString(4, empresa.getNomeFantasia());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao salvar empresa: " + e.getMessage());
        }
    }

    @Override
    public void save(Empresa empresa) {
        throw new UnsupportedOperationException(
                "Use save(Empresa, Long organizadorId) pois o banco exige o id do organizador.");
    }

    @Override
    public Empresa findById(String cnpj) {
        String sql = "SELECT * FROM empresa WHERE cnpj = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cnpj);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return new EmpresaRowMapper().mapRow(rs);
            }
        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao buscar empresa: " + e.getMessage());
        }
        return null;
    }

    public Empresa findByOrganizadorId(Long organizadorId) {
        String sql = "SELECT * FROM empresa WHERE organizador_id = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, organizadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return new EmpresaRowMapper().mapRow(rs);
            }
        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao buscar empresa por organizador: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Empresa> findAll() {
        String sql = "SELECT * FROM empresa";
        List<Empresa> empresas = new ArrayList<>();
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            EmpresaRowMapper mapper = new EmpresaRowMapper();
            while (rs.next()) empresas.add(mapper.mapRow(rs));

        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao listar empresas: " + e.getMessage());
        }
        return empresas;
    }

    @Override
    public void update(Empresa empresa) {
        String sql = "UPDATE empresa SET razao_social = ?, nome_fantasia = ? WHERE cnpj = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empresa.getRazaoSocial());
            stmt.setString(2, empresa.getNomeFantasia());
            stmt.setString(3, empresa.getCnpj());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao atualizar empresa: " + e.getMessage());
        }
    }

    @Override
    public void delete(String cnpj) {
        String sql = "DELETE FROM empresa WHERE cnpj = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cnpj);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao deletar empresa: " + e.getMessage());
        }
    }

    private static class EmpresaRowMapper implements RowMapper<Empresa> {
        @Override
        public Empresa mapRow(ResultSet rs) throws SQLException {
            return new Empresa(
                    rs.getString("cnpj"),
                    rs.getString("razao_social"),
                    rs.getString("nome_fantasia")
            );
        }
    }
}
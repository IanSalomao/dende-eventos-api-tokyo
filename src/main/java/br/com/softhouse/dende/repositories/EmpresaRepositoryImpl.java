package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.exceptions.DadosInvalidosException;
import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.softhouse.dende.repositories.util.CrudRepository;
import br.com.softhouse.dende.repositories.util.RowMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpresaRepositoryImpl implements CrudRepository<Empresa, String> {

    @Override
    public void save(Empresa empresa) {
        String sql = "INSERT INTO empresas (cnpj, razao_social, nome_fantasia) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empresa.getCnpj());
            stmt.setString(2, empresa.getRazaoSocial());
            stmt.setString(3, empresa.getNomeFantasia());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao salvar a empresa no banco de dados: " + e.getMessage());
        }
    }

    @Override
    public Empresa findById(String cnpj) {
        String sql = "SELECT * FROM empresas WHERE cnpj = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cnpj);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new EmpresaRowMapper().mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao buscar a empresa: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Empresa> findAll() {
        String sql = "SELECT * FROM empresas";
        List<Empresa> empresas = new ArrayList<>();

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            EmpresaRowMapper mapper = new EmpresaRowMapper();
            while (rs.next()) {
                empresas.add(mapper.mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao listar empresas: " + e.getMessage());
        }
        return empresas;
    }

    @Override
    public void update(Empresa empresa) {
        String sql = "UPDATE empresas SET razao_social = ?, nome_fantasia = ? WHERE cnpj = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empresa.getRazaoSocial());
            stmt.setString(2, empresa.getNomeFantasia());
            stmt.setString(3, empresa.getCnpj());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao atualizar a empresa: " + e.getMessage());
        }
    }

    @Override
    public void delete(String cnpj) {
        String sql = "DELETE FROM empresas WHERE cnpj = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cnpj);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao deletar a empresa: " + e.getMessage());
        }
    }

    // RowMapper como classe interna estática
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
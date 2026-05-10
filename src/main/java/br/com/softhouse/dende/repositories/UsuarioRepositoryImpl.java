package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.*;
import br.com.softhouse.dende.model.enums.Sexo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import br.com.softhouse.dende.repositories.util.CrudRepository;
import br.com.softhouse.dende.repositories.util.RowMapper;
import br.com.softhouse.dende.repositories.util.ConnectionPool;


public class UsuarioRepositoryImpl implements CrudRepository<Usuario, String> {

    @Override
    public void save(Usuario usuario) {
        String sql = "INSERT INTO usuarios (email, nome, data_nascimento, sexo, senha, ativo, tipo_usuario, empresa_cnpj) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, usuario.getNome());
            stmt.setDate(3, java.sql.Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(4, usuario.getSexo().name());
            stmt.setString(5, usuario.getSenha());
            stmt.setBoolean(6, usuario.isAtivo());

            if (usuario instanceof UsuarioComum) {
                stmt.setString(7, "COMUM");
                stmt.setNull(8, java.sql.Types.VARCHAR);

            } else if (usuario instanceof UsuarioOrganizador organizador) {
                stmt.setString(7, "ORGANIZADOR");

                if (organizador.getEmpresa() != null) {
                    stmt.setString(8, organizador.getEmpresa().getCnpj());
                } else {
                    stmt.setNull(8, java.sql.Types.VARCHAR);
                }
            } else {
                throw new IllegalArgumentException("Tipo de utilizador não suportado.");
            }
            stmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new br.com.softhouse.dende.exceptions.EmailJaCadastradoException(usuario.getEmail());
            }
            throw new RuntimeException("Erro ao guardar o utilizador na base de dados: " + e.getMessage(), e);
        }
    }

    @Override
    public Usuario findById(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return new UsuarioRowMapper().mapRow(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar o utilizador com e-mail: " + email, e);
        }
        return null;
    }

    @Override
    public List<Usuario> findAll() {
        String sql = "SELECT * FROM usuarios";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            UsuarioRowMapper mapper = new UsuarioRowMapper();

            while (rs.next()) {
                usuarios.add(mapper.mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar os utilizadores: " + e.getMessage(), e);
        }

        return usuarios;
    }

    @Override
    public void update(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, data_nascimento = ?, sexo = ?, senha = ?, ativo = ?, tipo_usuario = ?, empresa_cnpj = ? WHERE email = ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setDate(2, java.sql.Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(3, usuario.getSexo().name());
            stmt.setString(4, usuario.getSenha());
            stmt.setBoolean(5, usuario.isAtivo());

            if (usuario instanceof UsuarioComum) {
                stmt.setString(6, "COMUM");
                stmt.setNull(7, java.sql.Types.VARCHAR);
            } else if (usuario instanceof UsuarioOrganizador organizador) {
                stmt.setString(6, "ORGANIZADOR");
                if (organizador.getEmpresa() != null) {
                    stmt.setString(7, organizador.getEmpresa().getCnpj());
                } else {
                    stmt.setNull(7, java.sql.Types.VARCHAR);
                }
            }

            stmt.setString(8, usuario.getEmail());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o utilizador: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String email) {
        String sql = "DELETE FROM usuarios WHERE email = ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao apagar o utilizador: " + e.getMessage(), e);
        }
    }

    private class UsuarioRowMapper implements RowMapper<Usuario> {

        @Override
        public Usuario mapRow(ResultSet rs) throws SQLException {
            String tipoUsuario = rs.getString("tipo_usuario");

            String nome = rs.getString("nome");
            LocalDate dataNascimento = rs.getDate("data_nascimento").toLocalDate();
            Sexo sexo = Sexo.valueOf(rs.getString("sexo"));
            String email = rs.getString("email");
            String senha = rs.getString("senha");
            boolean ativo = rs.getBoolean("ativo");

            if ("COMUM".equals(tipoUsuario)) {

                UsuarioComum comum = new UsuarioComum(nome, dataNascimento, sexo, email, senha);
                if (!ativo) comum.desativarUsuario();
                return comum;

            } else if ("ORGANIZADOR".equals(tipoUsuario)) {

                String cnpj = rs.getString("empresa_cnpj");
                Empresa empresa = null;

                UsuarioOrganizador organizador = new UsuarioOrganizador(nome, dataNascimento, sexo, email, senha, empresa);
                if (!ativo) organizador.desativarUsuario();
                return organizador;

            }
            throw new SQLException("Tipo de utilizador desconhecido: " + tipoUsuario);
        }
    }
}
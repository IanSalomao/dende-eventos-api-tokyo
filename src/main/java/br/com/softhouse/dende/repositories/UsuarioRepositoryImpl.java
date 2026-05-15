package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.exceptions.EmailJaCadastradoException;
import br.com.softhouse.dende.exceptions.DadosInvalidosException;
import br.com.softhouse.dende.model.*;
import br.com.softhouse.dende.model.enums.Sexo;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.softhouse.dende.repositories.util.CrudRepository;
import br.com.softhouse.dende.repositories.util.RowMapper;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepositoryImpl implements CrudRepository<Usuario, String> {

    private final EmpresaRepositoryImpl empresaRepository = new EmpresaRepositoryImpl();

    @Override
    public void save(Usuario usuario) {
        String sql = """
                INSERT INTO usuario (nome, data_nascimento, sexo, email, senha, tipo_usuario, ativo)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setDate(2, Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(3, usuario.getSexo().name());   // 'M', 'F' ou 'O'
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getSenha());
            stmt.setBoolean(6, usuario.isAtivo());

            if (usuario instanceof UsuarioComum) {
                stmt.setString(6, "COMUM");
            } else if (usuario instanceof UsuarioOrganizador) {
                stmt.setString(6, "ORGANIZADOR");
            } else {
                throw new IllegalArgumentException("Tipo de usuário não suportado.");
            }

            stmt.setBoolean(7, usuario.isAtivo());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    long idGerado = keys.getLong(1);
                    if (usuario instanceof UsuarioOrganizador organizador
                            && organizador.getEmpresa() != null) {
                        empresaRepository.save(organizador.getEmpresa(), idGerado);
                    }
                }
            }

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                throw new EmailJaCadastradoException(usuario.getEmail());
            }
            throw new DadosInvalidosException("Erro ao salvar usuário: " + e.getMessage());
        }
    }

    @Override
    public Usuario findById(String email) {
        String sql = "SELECT * FROM usuario WHERE email = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return new UsuarioRowMapper().mapRow(rs);
            }
        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao buscar usuário: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Usuario> findAll() {
        String sql = "SELECT * FROM usuario";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            UsuarioRowMapper mapper = new UsuarioRowMapper();
            while (rs.next()) usuarios.add(mapper.mapRow(rs));

        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao listar usuários: " + e.getMessage());
        }
        return usuarios;
    }

    @Override
    public void update(Usuario usuario) {
        String sql = """
                UPDATE usuario
                SET nome = ?, data_nascimento = ?, sexo = ?,
                    senha = ?, tipo_usuario = ?, ativo = ?
                WHERE email = ?
                """;
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setDate(2, Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(3, usuario.getSexo().name());
            stmt.setString(4, usuario.getSenha());
            stmt.setString(5, usuario instanceof UsuarioComum ? "COMUM" : "ORGANIZADOR");
            stmt.setBoolean(6, usuario.isAtivo());
            stmt.setString(7, usuario.getEmail());
            stmt.executeUpdate();

            if (usuario instanceof UsuarioOrganizador organizador
                    && organizador.getEmpresa() != null) {
                empresaRepository.update(organizador.getEmpresa());
            }

        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    @Override
    public void delete(String email) {
        String sql = "DELETE FROM usuario WHERE email = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DadosInvalidosException("Erro ao deletar usuário: " + e.getMessage());
        }
    }

    private class UsuarioRowMapper implements RowMapper<Usuario> {
        @Override
        public Usuario mapRow(ResultSet rs) throws SQLException {
            String tipoUsuario  = rs.getString("tipo_usuario");
            String nome         = rs.getString("nome");
            LocalDate nascimento = rs.getDate("data_nascimento").toLocalDate();
            Sexo sexo           = Sexo.valueOf(rs.getString("sexo"));
            String email        = rs.getString("email");
            String senha        = rs.getString("senha");
            boolean ativo       = rs.getBoolean("ativo");
            long id             = rs.getLong("id");

            if ("COMUM".equals(tipoUsuario)) {
                UsuarioComum comum = new UsuarioComum(nome, nascimento, sexo, email, senha);
                if (!ativo) comum.desativarUsuario();
                return comum;

            } else if ("ORGANIZADOR".equals(tipoUsuario)) {
                Empresa empresa = empresaRepository.findByOrganizadorId(id);
                UsuarioOrganizador organizador =
                        new UsuarioOrganizador(nome, nascimento, sexo, email, senha, empresa);
                if (!ativo) organizador.desativarUsuario();
                return organizador;
            }

            throw new SQLException("Tipo de usuário desconhecido: " + tipoUsuario);
        }
    }
}
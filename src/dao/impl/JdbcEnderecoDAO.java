package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jdbc.PostgreSQLJDBC;
import model.Endereco;

public class JdbcEnderecoDAO {

	private Connection con;
	Statement stmt = null;

	public JdbcEnderecoDAO() throws SQLException {
		this.con = PostgreSQLJDBC.getConnection();
		con.setAutoCommit(false);
	}

	public Endereco consultaEnedereco(int idEndereco) throws SQLException {

		Endereco endereco = new Endereco();

		stmt = con.createStatement();

		String sql = "SELECT * FROM ENDERECO WHERE ID_ENDERECO = ?";

		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1, idEndereco);
		ResultSet rs = ps.executeQuery();

		endereco.setCep(rs.getString("cep"));
		endereco.setLogradouro(rs.getString("logradouro"));
		endereco.setNumero(rs.getInt("numero"));

		ps.close();

		stmt.close();
		con.commit();
		con.close();

		return endereco;
	}

}

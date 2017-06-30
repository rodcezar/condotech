package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import jdbc.PostgreSQLJDBC;
import model.Cliente;

public class JdbcClienteDAO {

	private Connection con;
	Statement stmt = null;

	public JdbcClienteDAO() throws SQLException {
		this.con = PostgreSQLJDBC.getConnection();
		con.setAutoCommit(false);
	}

	public ArrayList<String> retornaListaClientes() throws SQLException {
		ArrayList<String> lista = new ArrayList<String>();

		stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM CLIENTE;");

		while (rs.next()) {

			Cliente cliente = new Cliente();

			cliente.setCPF(rs.getString("cpf"));
			cliente.setName(rs.getString("nome"));
			cliente.setTelefone(rs.getString("telefone"));
			cliente.setDataNascimento(rs.getString("data_nascimento"));

			lista.add(cliente.toString());

		}

		return lista;
	}

	public ArrayList<Cliente> retornaClientes() throws SQLException {
		ArrayList<Cliente> lista = new ArrayList<Cliente>();

		stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM CLIENTE;");

		while (rs.next()) {

			Cliente cliente = new Cliente();

			cliente.setCPF(rs.getString("cpf"));
			cliente.setName(rs.getString("nome"));
			cliente.setEndereco(null); // FALTA FAZER - Buscar o endereco
			cliente.setTelefone(rs.getString("telefone"));
			cliente.setDataNascimento(rs.getString("data_nascimento"));

			lista.add(cliente);

		}

		return lista;
	}

	public Cliente consulta(String cpf) throws SQLException {

		Cliente cliente = new Cliente();

		stmt = con.createStatement();

		String sql = "SELECT * FROM CLIENTE WHERE CPF = ?";

		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, cpf);

		ResultSet rs = ps.executeQuery();

		if (rs.next()) {
			cliente.setCPF(rs.getString("cpf"));
			cliente.setName(rs.getString("nome"));
			cliente.setEndereco(null); // FALTA FAZER - Buscar o endereco
			cliente.setTelefone(rs.getString("telefone"));
			cliente.setDataNascimento(rs.getString("data_nascimento"));
		}

		ps.close();

		stmt.close();
		con.commit();
		// con.close();

		return cliente;
	}

	public void insere(String nome, String CPF, String telefone, String dataNascimento) throws SQLException {

		stmt = con.createStatement();

		String sql = "INSERT INTO CLIENTE " + "(NOME, CPF, ID_ENDERECO, TELEFONE, DATA_NASCIMENTO) "
			       + "VALUES (?, ?, 1, ?, ?)";

		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, nome);
		ps.setString(2, CPF);
		ps.setString(3, telefone);
		ps.setString(4, dataNascimento);
		ps.executeUpdate();
		ps.close();

		stmt.close();
		con.commit();
		// con.close();
	}

	public void delete(String cpf) throws SQLException {

		stmt = con.createStatement();

		String sql = "DELETE FROM CLIENTE WHERE CPF = ?";

		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, cpf);
		ps.executeUpdate();
		ps.close();

		stmt.close();
		con.commit();
		// con.close();
	}

	public void update(String cpf, String telefone, String dataNascimento) throws SQLException {

		stmt = con.createStatement();

		// update telefone
		if (telefone != null) {
			String sql = "UPDATE CLIENTE SET TELEFONE = ? " + "WHERE CPF = ?";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, telefone);
			ps.setString(2, cpf);
			ps.executeUpdate();
			ps.close();
		}

		// update data_nascimento
		if (dataNascimento != null) {
			String sql = "UPDATE CLIENTE SET DATA_NASCIMENTO = ? " + "WHERE CPF = ?";

			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, dataNascimento);
			ps.setString(2, cpf);
			ps.executeUpdate();
			ps.close();
		}

		stmt.close();
		con.commit();
		// con.close();
	}
	
	public String setSindico(int idPredio, String cpf) throws SQLException {

		String proc = "SELECT UPDATE_SINDICO(?, ?);";
		String mensagem = "ERRO";

		PreparedStatement psProc = con.prepareStatement(proc);
		psProc.setInt(1, idPredio);
		psProc.setString(2, cpf);

		ResultSet rsProc = psProc.executeQuery();

		while (rsProc.next()) {
			mensagem = rsProc.getString("UPDATE_SINDICO");
		}

		con.commit();
		con.close();

		return mensagem;
	}
	
}

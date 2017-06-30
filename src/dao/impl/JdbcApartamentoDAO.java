package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import jdbc.PostgreSQLJDBC;
import model.Apartamento;
import model.Cota;
import model.Predio;

public class JdbcApartamentoDAO {

	private Connection con;
	Statement stmt = null;

	public JdbcApartamentoDAO() throws SQLException {
		this.con = PostgreSQLJDBC.getConnection();
		con.setAutoCommit(false);
	}

	public Apartamento consulta(int idApartamento) throws SQLException {

		Apartamento apartamento = new Apartamento();
		ArrayList<Cota> listaCotas = new ArrayList<Cota>();

		stmt = con.createStatement();

		String sql = "SELECT * FROM APARTAMENTO WHERE ID_APARTAMENTO = ?";

		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1, idApartamento);

		ResultSet rs = ps.executeQuery();

		if (rs.next()) {

			apartamento.setIdApartamento(rs.getInt("ID_APARTAMENTO"));
			apartamento.setNumero(rs.getInt("NUMERO"));
			apartamento.setIdPredio(rs.getInt("ID_PREDIO"));
		}

		listaCotas = getListaCotas(idApartamento, apartamento.getNumero());

		for (Cota cota : listaCotas) {
			apartamento.insereCota(cota);
		}

		return apartamento;
	}

	public ArrayList<Apartamento> getListaApartamento(int idPredio) throws SQLException {

		ArrayList<Apartamento> lista = new ArrayList<Apartamento>();

		String sql = "SELECT * FROM APARTAMENTO WHERE ID_PREDIO = ? ";

		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1, idPredio);

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {

			Apartamento apartamento = new Apartamento();

			apartamento.setIdPredio(rs.getInt("ID_PREDIO"));
			apartamento.setNumero(rs.getInt("NUMERO"));

			String proc = "select get_proprietario(?, ?)";

			PreparedStatement psProc = con.prepareStatement(proc);
			psProc.setInt(1, idPredio);
			psProc.setInt(2, apartamento.getNumero());

			ResultSet rsProc = psProc.executeQuery();

			while (rsProc.next()) {

				apartamento.setNomeProprietario(rsProc.getString("GET_PROPRIETARIO"));

			}

			lista.add(apartamento);

		}

		return lista;
	}

	public ArrayList<Cota> getListaCotas(int idPredio, int numero) throws SQLException {

		ArrayList<Cota> lista = new ArrayList<Cota>();

		String sql = "SELECT A.*, B.NOME AS NOME_COTISTA " + "FROM COTA A, CLIENTE B "
				+ "WHERE ID_PREDIO = ? AND NUMERO = ?" + " AND A.CPF = B.CPF ";

		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1, idPredio);
		ps.setInt(2, numero);

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {

			Cota cota = new Cota();

			cota.setCpf(rs.getString("CPF"));
			cota.setNomeCotista(rs.getString("NOME_COTISTA"));
			cota.setPercentual(rs.getInt("QUANTIDADE"));

			lista.add(cota);
		}

		return lista;
	}

	public String insereCota(String cpf, int idPredio, int numero, int quantidade) throws SQLException {

		String proc = "select insere_cota(?, ?, ?, ?);";
		String mensagem = "ERRO";

		PreparedStatement psProc = con.prepareStatement(proc);
		psProc.setString(1, cpf);
		psProc.setInt(2, idPredio);
		psProc.setInt(3, numero);
		psProc.setInt(4, quantidade);

		ResultSet rsProc = psProc.executeQuery();

		while (rsProc.next()) {
			mensagem = rsProc.getString("INSERE_COTA");
		}

		con.commit();

		return mensagem;
	}
	
	public Predio getPredio(int idPredio, int idCondominio) throws SQLException {

		Predio predio = new Predio();

		String sql = "SELECT P.*, C.NOME AS SINDICO FROM PREDIO P, CLIENTE C "
				+ " WHERE P.ID_PREDIO = ? "
				+ " AND P.ID_CONDOMINIO = ?"
				+ " AND P.CPF_SINDICO = C.CPF";;

		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1, idPredio);
		ps.setInt(2, idCondominio);

		ResultSet rs = ps.executeQuery();

		if (rs.next()) {

			predio.setIdPredio(rs.getInt("ID_PREDIO"));
			predio.setIdCondominio(rs.getInt("ID_CONDOMINIO"));
			predio.setNome(rs.getString("NOME"));
			predio.setCpfSindico(rs.getString("CPF_SINDICO"));
			predio.setNomeSindico(rs.getString("SINDICO"));
			predio.setNumero(rs.getInt("NUMERO"));
			predio.setIdEndereco(rs.getInt("ID_ENDERECO"));

		}

		return predio;
	}

	public boolean checkCpf(String cpf) throws SQLException {

		String proc = "SELECT CHECK_CPF(?);";
		boolean result = false;
		
		PreparedStatement psProc = con.prepareStatement(proc);
		psProc.setString(1, cpf);

		ResultSet rsProc = psProc.executeQuery();

		while (rsProc.next()) {
			result = rsProc.getBoolean("CHECK_CPF");
		}

		con.commit();

		return result;
	}
	
	public boolean deletePredio(int idPredio) throws SQLException {

		String proc = "SELECT DELETE_PREDIO(?)";
		boolean result = false;
		
		PreparedStatement psProc = con.prepareStatement(proc);
		psProc.setInt(1, idPredio);

		ResultSet rsProc = psProc.executeQuery();

		while (rsProc.next()) {
			result = rsProc.getBoolean("DELETE_PREDIO");
		}

		con.commit();

		return result;
	}
	
	
}

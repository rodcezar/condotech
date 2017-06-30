package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import jdbc.PostgreSQLJDBC;
import model.Condominio;
import model.Predio;

public class JdbcCondominioDAO {

	private Connection con;
	Statement stmt = null;

	public JdbcCondominioDAO() throws SQLException {
		this.con = PostgreSQLJDBC.getConnection();
		con.setAutoCommit(false);
	}

	public ArrayList<Condominio> retornaListaCondominios() throws SQLException {
		ArrayList<Condominio> lista = new ArrayList<Condominio>();

		stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM CONDOMINIO;");

		while (rs.next()) {

			Condominio condominio = new Condominio();

			condominio.setIdCondominio(rs.getInt("ID_CONDOMINIO"));
			condominio.setCpfResponsavel(rs.getString("CPF_RESPONSAVEL"));
			condominio.setNome(rs.getString("NOME"));

			lista.add(condominio);

		}

		return lista;
	}

	public Condominio consulta(int idCondominio) throws SQLException {

		Condominio condominio = new Condominio();
		ArrayList<Predio> listaPredio = new ArrayList<Predio>();

		stmt = con.createStatement();

		String sql = "SELECT * FROM CONDOMINIO WHERE ID_CONDOMINIO = ?";

		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1, idCondominio);

		ResultSet rs = ps.executeQuery();

		if (rs.next()) {

			condominio.setIdCondominio(rs.getInt("ID_CONDOMINIO"));
			condominio.setCpfResponsavel(rs.getString("CPF_RESPONSAVEL"));
			condominio.setNome(rs.getString("NOME"));
		}

		listaPredio = getListaPredio(idCondominio);

		for (Predio predio : listaPredio) {
			condominio.addPredio(predio);
		}

		return condominio;
	}

	public ArrayList<Predio> getListaPredio(int idCondominio) throws SQLException {

		ArrayList<Predio> lista = new ArrayList<Predio>();

		String sql = "SELECT P.*, C.NOME AS SINDICO FROM PREDIO P, CLIENTE C " + " WHERE P.ID_CONDOMINIO = ? "
				+ " AND P.CPF_SINDICO = C.CPF";

		PreparedStatement ps = con.prepareStatement(sql);
		ps.setInt(1, idCondominio);

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {

			Predio predio = new Predio();

			predio.setIdPredio(rs.getInt("ID_PREDIO"));
			predio.setIdCondominio(rs.getInt("ID_CONDOMINIO"));
			predio.setNome(rs.getString("NOME"));
			predio.setCpfSindico(rs.getString("CPF_SINDICO"));
			predio.setNomeSindico(rs.getString("SINDICO"));
			predio.setNumero(rs.getInt("NUMERO"));
			predio.setIdEndereco(rs.getInt("ID_ENDERECO"));

			lista.add(predio);

		}

		return lista;
	}
	
	public int get_percentual_vendido(int id_Condominio) throws SQLException {

		String proc = "SELECT GET_PERCENTUAL_VENDIDO(?);";
		int percentual = 0;
		
		PreparedStatement psProc = con.prepareStatement(proc);
		psProc.setInt(1, id_Condominio);

		ResultSet rsProc = psProc.executeQuery();

		while (rsProc.next()) {
			percentual = rsProc.getInt("GET_PERCENTUAL_VENDIDO");
		}

		con.commit();

		return percentual;
	}
	
	
}

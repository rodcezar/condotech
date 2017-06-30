package model;

import java.util.ArrayList;

public class Apartamento {
	
	private int idApartamento;
	private int numero;
	private int idPredio;
	private String nomeProprietario;
	private ArrayList<Cota> cotas;
	
	public Apartamento(){
		cotas = new ArrayList<Cota>();
	}
	
	public void setIdApartamento(int idApartamento){
		this.idApartamento = idApartamento;
	}
	
	public int  getIdApartamento(){
		return this.idApartamento;
	}
	
	public void setNumero(int numero){
		this.numero = numero;
	}
	
	public int  getNumero(){
		return this.numero;
	}
	
	public void setIdPredio(int idPredio){
		this.idPredio = idPredio;
	}
	
	public int getIdPredio(){
		return this.idPredio;
	}
	
	public void insereCota(Cota cota){
		this.cotas.add(cota);
	}

	public String getNomeProprietario() {
		return nomeProprietario;
	}

	public void setNomeProprietario(String nomeProprietario) {
		this.nomeProprietario = nomeProprietario;
	}
	
}

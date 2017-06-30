package model;

public class Cota {
	
	private int idPredio;
	private int numero;
	private String cpf;
	private String nomeCotista;
	private int percentual;
	
	public Cota(){
		cpf = "";
	}
	
	public void setCpf(String cpf){
		this.cpf = cpf;
	}
	
	public String getCpf(){
		return this.cpf;
	}
	
	public void setPercentual(int percentual){
		this.percentual = percentual;
	}
	
	public int  getPercentual(){
		return this.percentual;
	}

	public int getIdPredio() {
		return idPredio;
	}

	public void setIdPredio(int idPredio) {
		this.idPredio = idPredio;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getNomeCotista() {
		return nomeCotista;
	}

	public void setNomeCotista(String nomeCotista) {
		this.nomeCotista = nomeCotista;
	}
	
}

package model;

public class Endereco {
	
	int idEndereco;
	String logradouro;
	String cep;
	int numero;
	
	public Endereco() {
		this.logradouro = "";
		this.cep = "";
	}
	
	public void setLogradouro(String logradouro){
		this.logradouro = logradouro;
	}
	
	public String getLogradouro(){
		return this.logradouro;
	}
	
	public void setCep(String cep){
		this.cep = cep;
	}
	
	public String getCep(){
		return this.cep;
	}
	
	public void setNumero(int numero){
		this.numero = numero;
	}
	
	public int getNumero(){
		return this.numero;
	}
	
	@Override
	public String toString() {
		return " [  " + logradouro.trim() + ", " + 
				"CEP: " + cep + ", " + 
				"Numero: " + numero + "     ]";
	}
	

}

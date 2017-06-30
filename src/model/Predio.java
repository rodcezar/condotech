package model;

import java.util.ArrayList;

public class Predio {
	
	private int idPredio;
	private int idCondominio;
	private String nome;
	private String cpfSindico;
	private String nomeSindico;
	private int numero;
	private int idEndereco;
	private ArrayList<Apartamento> apartamentos;
	
	public Predio(){
		nome = "";
		apartamentos = new ArrayList<Apartamento>();
	}
	
	public void setIdPredio(int idPredio){
		this.idPredio = idPredio;
	}
	
	public int  getIdPredio(){
		return this.idPredio;
	}
	
	public void setNome(String nome){
		this.nome = nome;
	}
	
	public String getNome(){
		return this.nome;
	}
	
	public void setCpfSindico(String cpfSindico){
		this.cpfSindico = cpfSindico;
	}
	
	public String getCpfSindico(){
		return this.cpfSindico;
	}
	
	public void setNumero(int numero){
		this.numero = numero;
	}
	
	public int  getNumero(){
		return this.numero;
	}
	
	public void setIdCondominio(int idCondominio){
		this.idCondominio = idCondominio;
	}
	
	public int  getIdComdominio(){
		return this.idCondominio;
	}
	
	public void insereApartamento(Apartamento apartamento){
		this.apartamentos.add(apartamento);
	}
	
	public Apartamento getApartamentoAt(int index){
		return apartamentos.get(index);
	}

	public int getIdEndereco() {
		return idEndereco;
	}

	public void setIdEndereco(int idEndereco) {
		this.idEndereco = idEndereco;
	}

	public String getNomeSindico() {
		return nomeSindico;
	}

	public void setNomeSindico(String nomeSindico) {
		this.nomeSindico = nomeSindico;
	}
	
}

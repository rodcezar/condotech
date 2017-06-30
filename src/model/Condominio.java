package model;

import java.util.ArrayList;

public class Condominio {

	private int idCondominio;
	private String nome;
	private String cpfResponsavel;
	private ArrayList<Predio> predios;
	
	public Condominio(){
		cpfResponsavel = "";
		predios = new ArrayList<Predio>();
	}
	
	public void setIdCondominio(int idCondominio){
		this.idCondominio = idCondominio;
	}
	
	public int  getIdCondominio(){
		return this.idCondominio;
	}
	
	public void setNome(String nome){
		this.nome = nome;
	}
	
	public String getNome(){
		return this.nome;
	}
	
	public void setCpfResponsavel(String cpfResponsavel){
		this.cpfResponsavel = cpfResponsavel;
	}
	
	public String getCpfResponsavel(){
		return this.cpfResponsavel;
	}
	
	public void addPredio(Predio predio){
		this.predios.add(predio);
	}
	
	public ArrayList<Predio> listaPredios(){
		return predios;
	}
	
	public Predio getPredioAt(int index){
		return predios.get(index);
	}
	
}

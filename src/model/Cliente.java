package model;

public class Cliente {

	String cpf;
	String nome;
	Endereco endereco;
	String telefone;
	String dataNascimento;
	
	public Cliente() {
		this.cpf = "";
		this.nome = "";
		this.telefone = "";
		this.dataNascimento = "";
		this.endereco = new Endereco();
	}
	
	public String getCPF() {
		return cpf;
	}
	public void setCPF(String cpf) {
		this.cpf = cpf;
	}
	public String getName() {
		return nome;
	}
	public void setName(String name) {
		this.nome = name;
	}
	public Endereco getEndereco() {
		return endereco;
	}
	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}
	
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	public String getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
	@Override
	public String toString() {
		return " [   Nome: " + nome.trim() + ", " + 
			     	"CPF: " + cpf + ", " + 
				    "Telefone: " + telefone.trim() + ", " + 
			 	    "Data. Nasc.:  " + dataNascimento.trim() + "     ]";
	}
	
	
}

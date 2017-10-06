package modelo;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import interfaces.ILeiloavel;
import interfaces.IVendido;

public class Test_Usuario {

	private static long nextCPF = 0; 
	static String geraCPF() {
		return String.format('%'+"1$"+'0'+11+'d', nextCPF++);
	}
	static Usuario criaUsuario(String nome) {
		String cpf = geraCPF();
		return new Usuario(cpf, nome);
	}

	private static String cpf;
	private static String nome;
	@BeforeClass
	public static void newUsuario() {
		cpf = "12345678901";
		nome = "Fulano";
		Usuario sosia = new Usuario("98765432101", nome);
		Usuario clone = new Usuario(cpf, "Ciclano");
		
		Usuario usuario = new Usuario(cpf, nome);
		
		assertEquals(cpf, usuario.getCpf());
		assertEquals(nome, usuario.getNome());
		assertNotNull(usuario.toString());
		assertNotEquals(sosia, usuario);
		assertEquals(clone, usuario);
	}

	private Usuario usuario;
	@Before
	public void fixtureSetup() throws Exception {
		usuario = new Usuario(cpf, nome);
	}
	
	@Test
	public void setEndereco() {
		String endereco = "endereço";
		
		usuario.setEndereco(endereco);
		
		assertEquals(endereco, usuario.getEndereco());
	}
	
	@Test
	public void setEmail() {
		String email = "email";
		
		usuario.setEmail(email);
		
		assertEquals(email, usuario.getEmail());
	}

	private Produto newProduto() {
		Usuario leiloador = new Usuario("cpf", "nome");
		return new Produto("nome", "descricao", 1.0, leiloador);
	}
	
	@Test
	public void setBemComprado() {
		Produto produto = newProduto();
		
		usuario.setBemComprado(produto);

		List<? extends IVendido> bensFinais = usuario.getBensComprados();
		assertEquals(1, bensFinais.size());
		assertTrue(bensFinais.contains(produto));
	}

	@Test
	public void setBemOfertado() {
		Produto produto = newProduto();
		
		usuario.setBemOfertado(produto);

		List<? extends ILeiloavel> bensFinais = usuario.getBensOfertados();
		assertEquals(1, bensFinais.size());
		assertTrue(bensFinais.contains(produto));
	}
}
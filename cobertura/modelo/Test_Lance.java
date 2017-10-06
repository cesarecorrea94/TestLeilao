package modelo;

import static org.junit.Assert.*;
import static modelo.Test_Usuario.criaUsuario;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Test_Lance {

	private static Double valorDoLance;
	@BeforeClass
	public static void newLance() {
		valorDoLance = 10.0;
		Usuario donoDoLance = criaUsuario("Fulano");
		
		Lance lance = new Lance(valorDoLance, donoDoLance);
		
		assertEquals(valorDoLance, lance.getValorDoLance());
		assertEquals(donoDoLance.getCpf(), lance.getCpfDonoDoLance());
		assertEquals(donoDoLance.getNome(), lance.getNomeDonoDoLance());
		//assertNotNull(lance.toString());
	}

	private Usuario donoDoLance;
	private Lance lance;
	@Before
	public void fixtureSetup() {
		donoDoLance = criaUsuario("Fulano");
		lance = new Lance(valorDoLance, donoDoLance);
	}
	
	@Test
	public void getProdutoQueRecebeuOLance() {
		Usuario leiloador = criaUsuario("Ciclano");
		Produto caderno = new Produto("nome", "descricao", 5.0, leiloador);
		caderno.recebaLance(lance);
		lance.setProdutoQueRecebeuOLance(caderno);
		
		Produto produto = lance.getProdutoQueRecebeuOLance();
		
		assertEquals(caderno, produto);
	}
	
	@Test
	public void getNomeProdutoQueRecebeuOLance() {
		Usuario leiloador = criaUsuario("Ciclano");
		Produto caderno = new Produto("nome", "descricao", 5.0, leiloador);
		caderno.recebaLance(lance);
		lance.setProdutoQueRecebeuOLance(caderno); // Não seria melhor estar incluido no "Produto.recebaLance"? Tratar isso como falha?
		
		String nomeProduto = lance.getNomeProdutoQueRecebeuOLance();
		
		assertEquals(caderno.getNome(), nomeProduto);
	}
	
	@Test
	public void setProdutoQueRecebeuOLance() {
		Usuario leiloador = criaUsuario("Ciclano");
		Produto produto = new Produto("nome", "descricao", 5.0, leiloador);
		
		lance.setProdutoQueRecebeuOLance(produto);
		
		assertEquals(produto, lance.getProdutoQueRecebeuOLance());
	}
}

package modelo;

import static org.junit.Assert.*;
import static modelo.Test_Usuario.criaUsuario;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Test_Produto  {
	
	private static String nome, descricao;
	private static Double lanceMinimo;
	private static Usuario leiloador;
	@BeforeClass
	public static void newProduto() {
		nome = "caderno";
		descricao = "amontoado de folhas";
		lanceMinimo = 5.0;
		leiloador = criaUsuario("Fulano");
		
		Produto produto = new Produto(nome, descricao, lanceMinimo, leiloador);
		
		assertEquals(nome, produto.getNome());
		assertEquals(descricao, produto.getDescricao());
		assertEquals(lanceMinimo, produto.getLanceMinimo());
		assertEquals(leiloador.getCpf(), produto.getCpfLeiloador());
		assertTrue(produto.retornaTodosOsLancesFeitosNesseProduto().isEmpty());
		assertNotNull(produto.toString());
	}
	
	private Produto produto;
	@Before
	public void fixtureSetup() throws Exception {
		produto = new Produto(nome, descricao, lanceMinimo, leiloador);
	}
	
	@Test
	public void setNome() {
		String nome = "nome";
		
		produto.setNome(nome);
		
		assertEquals(nome, produto.getNome());
	}
	
	@Test
	public void setDescricao() {
		String descricao = "descricao";
		
		produto.setDescricao(descricao);
		
		assertEquals(descricao, produto.getDescricao());
	}
	
	private static final int minutoEmMs = 60*1000;
	private static final int diaEmMs = 24*60*minutoEmMs;
	@Test
	public void setDataLimite() {
		Date data = new Date(System.currentTimeMillis() + 7*diaEmMs);
		
		produto.setDataLimite(data);
		
		assertEquals(data, produto.getDataLimite());
	}
	
	@Test
	public void recebaLance() {
		Usuario donoDoLance = criaUsuario("Ciclano");
		Lance lance = new Lance(lanceMinimo+1, donoDoLance);
		
		produto.recebaLance(lance);
		
		assertTrue(produto.retornaTodosOsLancesFeitosNesseProduto().contains(lance));
	}
	
	// dataDoProdutoExpirou
	
	@Test
	public void dataDoProdutoExpirou_False() {
		produto.setDataLimite(new Date(System.currentTimeMillis()+minutoEmMs));
		
		boolean expirado = produto.dataDoProdutoExpirou();
		
		assertFalse(expirado);
	}

	@Test
	public void dataDoProdutoExpirou_True() {
		produto.setDataLimite(new Date(System.currentTimeMillis()-1));
		
		boolean expirado = produto.dataDoProdutoExpirou();
		
		assertTrue(expirado);
	}
	
	//
	
	@Test
	public void lancesEfetuadosPorUmUsuario() {
		Usuario ciclano = criaUsuario("Ciclano"),
				beltrano = criaUsuario("Beltrano");
		double iterador = lanceMinimo+1;
		List<Lance> lancesCiclano = new LinkedList<Lance>(),
				lancesBeltrano = new LinkedList<Lance>();
		Lance lance;
		lancesCiclano.add(lance = new Lance(iterador++, ciclano));
		produto.recebaLance(lance);
		lancesBeltrano.add(lance = new Lance(iterador++, beltrano));
		produto.recebaLance(lance);
		lancesCiclano.add(lance = new Lance(iterador++, ciclano));
		produto.recebaLance(lance);
		
		List<Lance> listaCiclano = produto.lancesEfetuadosPorUmUsuario(ciclano.getCpf());
		List<Lance> listaBeltrano = produto.lancesEfetuadosPorUmUsuario(beltrano.getCpf());
		
		assertArrayEquals(lancesCiclano.toArray(), listaCiclano.toArray());
		assertArrayEquals(lancesBeltrano.toArray(), listaBeltrano.toArray());
	}

	// getValorUltimoLance
	
	@Test
	public void getValorUltimoLance_sucesso(){
		Usuario comprador = criaUsuario("Ciclano");
		Lance lance = new Lance(lanceMinimo+1, comprador);
		produto.recebaLance(lance);
		
		Double ultimo = produto.getValorUltimoLance();
		
		assertEquals(ultimo, lance.getValorDoLance());
	}

	@Test(expected=Exception.class)
	public void getValorUltimoLance_naoHouveLances(){	
		//Usuario comprador = criaUsuario("Ciclano");
		//Lance lance = new Lance(lanceMinimo+1, comprador);
		//produto.recebaLance(lance);
		
		Double ultimo = produto.getValorUltimoLance();
	}
	
	// getLanceMaisRecente
	
	@Test
	public void getLanceMaisRecente_sucesso() {
		Usuario comprador = criaUsuario("Ciclano");
		Lance lance = new Lance(lanceMinimo+1, comprador);
		produto.recebaLance(lance);
		
		Lance ultimo = produto.getLanceMaisRecente();
		
		assertEquals(ultimo, lance);
	}

	@Test(expected=Exception.class)
	public void getLanceMaisRecente_naoHouveLances() {
		//Usuario comprador = criaUsuario("Ciclano");
		//Lance lance = new Lance(lanceMinimo+1, comprador);
		//produto.recebaLance(lance);
		
		Lance ultimo = produto.getLanceMaisRecente();
	}
	
	//
	
	@Test
	public void setComprador() {
		Usuario comprador = criaUsuario("Ciclano");
		
		produto.setComprador(comprador);
		
		assertEquals(comprador.getCpf(), produto.getCpfComprador());
	}
}
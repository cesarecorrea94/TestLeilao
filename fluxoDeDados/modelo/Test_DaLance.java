package modelo;

import static org.junit.Assert.*;
//import static org.hamcrest.Matchers.*;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import interfaces.ILeiloavel;
import interfaces.IUsuario;
import interfaces.IVendido;

public class Test_DaLance {
	private MercadoLeilao mercado;
	private IUsuario leiloador;
	private ILeiloavel produto;
	
	private IUsuario mercadoCadastrarUsuario(String nome, String cpf)
			throws Exception {
		mercado.cadastrarUsuario(nome, "endereco", "email", cpf);
		return mercado.getUsuario(cpf);
	}
	private ILeiloavel mercadoCadastrarProduto(String nome, String descricao, double lanceMinimo, String cpfLeiloador)
			throws Exception {
		mercado.cadastrarProduto(nome, descricao, lanceMinimo, cpfLeiloador, Date.valueOf("2018-1-1"));
		List<? extends ILeiloavel> emLeilao = mercado.getProdutosEmLeilao();
		return emLeilao.get(emLeilao.size()-1);
	}

	private void assertLanceHas(Lance lance, Double valor, ILeiloavel produto, IUsuario dono) {
		assertEquals(valor,					lance.getValorDoLance());
		assertEquals(produto,				lance.getProdutoQueRecebeuOLance());
		if(produto != null)
			assertEquals(produto.getNome(),	lance.getNomeProdutoQueRecebeuOLance());
		if(dono != null) {
			assertEquals(dono.getNome(),	lance.getNomeDonoDoLance());
			assertEquals(dono.getCpf(),		lance.getCpfDonoDoLance());
		}
	}

	@Before
	public void fixtureSetup() throws Exception {
		mercado = new MercadoLeilao();
		leiloador = mercadoCadastrarUsuario("Fulano", "12345678901");
		produto = mercadoCadastrarProduto(
				"Caderno", "Amontoado de Folhas", 5.0, leiloador.getCpf());
	}
	
	/** Testes com nomes no estilo x_i_j, tal que x, i, e j, seguem a regra do critério de seleção All-du-paths:
	 * Para cada variável 'x':
	 * 		Para cada nodo 'i', tal que 'x' tenha definição global:
	 * 			Para cada def-clear path a partir:
	 * 									do nodo 'i' ao nodo 'j', tal que 'j' tenha um global c-use da variável 'x'; e
	 * 									do nodo 'i' à aresta 'j', tal que 'j' tenha um p-use da variável 'x'.
	 */

	// nomeProduto
	
	@Test
	public void nomeProduto_Initialize_nodoPosInitialize() throws Exception {
		lance_PosInitialize_nodoIfTrue();
	}

	// cpfComprador
	
	@Test
	public void cpfComprador_Initialize_nodoPosInitialize() throws Exception {
		lance_PosInitialize_nodoIfTrue();
	}

	@Test
	public void cpfComprador_Initialize_arestaIfTrue() throws Exception {
		lance_PosInitialize_nodoIfTrue();
	}
	
	@Test
	public void cpfComprador_Initialize_arestaIfFalse() throws Exception {
		IUsuario comprador = new Usuario("98765432101", "Ciclano"); // não cadastrado (não existe) no mercado
		Double ultimoLance = produto.getValorUltimoLance(),
				valorLance = 7.0;
		boolean excecao = false;
		
		try {
			mercado.daLance(produto.getNome(), comprador.getCpf(), valorLance);
		} catch (Exception e) {
			excecao = true;
		}
		
		assertTrue(excecao);
		Lance lance = ((Produto) produto).getLanceMaisRecente();
		assertLanceHas(lance, ultimoLance, null, null);
		assertEquals(ultimoLance, produto.getValorUltimoLance());
	}

	// valorLance

	@Test
	public void valorLance_Initialize_nodoPosInitialize() throws Exception {
		lance_PosInitialize_nodoIfTrue();
	}

	@Test
	public void valorLance_Initialize_arestaIfTrue() throws Exception {
		lance_PosInitialize_nodoIfTrue();
	}

	@Test
	public void valorLance_Initialize_arestaIfFalseNaoMaiorQueMinimo() throws Exception {
		lanceMinimo_PosInitialize_arestaIfFalse();
	}

	@Test
	public void valorLance_Initialize_arestaIfFalseNaoMaiorQueAtual() throws Exception {
		lanceAtual_PosInitialize_arestaIfFalse();
	}

	// lance

	@Test
	public void lance_PosInitialize_nodoIfTrue() throws Exception {
		lanceMinimo_PosInitialize_arestaIfTrue();
	}

	// produto

	@Test
	public void produto_PosInitialize_nodoIfTrue() throws Exception {
		lanceMinimo_PosInitialize_arestaIfTrue();
	}

	// lanceMinimo

	@Test
	public void lanceMinimo_PosInitialize_arestaIfTrue() throws Exception {
		IUsuario comprador = mercadoCadastrarUsuario("Ciclano", "98765432101");
		Double valorLance = 7.0;
		
		mercado.daLance(produto.getNome(), comprador.getCpf(), valorLance);
		
		Lance lance = ((Produto) produto).getLanceMaisRecente();
		assertLanceHas(lance, valorLance, produto, comprador);
		assertEquals(valorLance, produto.getValorUltimoLance());
	}

	@Test
	public void lanceMinimo_PosInitialize_arestaIfFalse() throws Exception {
		IUsuario comprador = mercadoCadastrarUsuario("Ciclano", "98765432101");
		Double ultimoLance = produto.getValorUltimoLance(),
				valorLance = 4.0;
		boolean excecao = false;
		
		try {
			mercado.daLance(produto.getNome(), comprador.getCpf(), valorLance);
		} catch (Exception e) {
			excecao = true;
		}
		
		assertTrue(excecao);
		Lance lance = ((Produto) produto).getLanceMaisRecente();
		assertLanceHas(lance, ultimoLance, null, null);
		assertEquals(ultimoLance, produto.getValorUltimoLance());
	}

	// lanceAtual

	@Test
	public void lanceAtual_PosInitialize_arestaIfTrue() throws Exception {
		IUsuario beltrano = mercadoCadastrarUsuario("Beltrano", "12345123451");
		mercado.daLance(produto.getNome(), beltrano.getCpf(), 6.0);
		IUsuario comprador = mercadoCadastrarUsuario("Ciclano", "98765432101");
		Double valorLance = 7.0;
		
		mercado.daLance(produto.getNome(), comprador.getCpf(), valorLance);
		
		Lance lance = ((Produto) produto).getLanceMaisRecente();
		assertLanceHas(lance, valorLance, produto, comprador);
		assertEquals(valorLance, produto.getValorUltimoLance());
	}

	@Test
	public void lanceAtual_PosInitialize_arestaIfFalse() throws Exception {
		IUsuario beltrano = mercadoCadastrarUsuario("Beltrano", "12345123451");
		mercado.daLance(produto.getNome(), beltrano.getCpf(), 7.0);
		IUsuario comprador = mercadoCadastrarUsuario("Ciclano", "98765432101");
		Double ultimoLance = produto.getValorUltimoLance(),
				valorLance = 6.0;
		boolean excecao = false;
		
		try {
			mercado.daLance(produto.getNome(), comprador.getCpf(), valorLance);
		} catch (Exception e) {
			excecao = true;
		}
		
		assertTrue(excecao);
		Lance lance = ((Produto) produto).getLanceMaisRecente();
		assertLanceHas(lance, ultimoLance, produto, beltrano);
		assertEquals(ultimoLance, produto.getValorUltimoLance());
	}

}

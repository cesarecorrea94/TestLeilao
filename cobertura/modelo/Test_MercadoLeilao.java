package modelo;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static modelo.Test_Usuario.geraCPF;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import interfaces.ILeiloavel;
import interfaces.IUsuario;
import modelo.Test_MercadoLeilao.MyFixtureSetup.MyUsuario;

public class Test_MercadoLeilao {

	private IUsuario cadastrarUsuario(String nome) throws Exception {
		String endereco = "endereco",
				email = "email",
				cpf = geraCPF();
		mercado.cadastrarUsuario(nome, endereco, email, cpf);
		return mercado.getUsuario(cpf);
	}
	private ILeiloavel cadastrarProduto(String nome, IUsuario leiloador, Double lanceMinimo) throws Exception {
		String descricao = "descricao",
				cpfLeiloador = leiloador.getCpf();
		Date dataLimite = new Date(System.currentTimeMillis() + 7*diaEmMs);
		mercado.cadastrarProduto(nome, descricao, lanceMinimo, cpfLeiloador, dataLimite);
		return ultimoProduto();
	}
	private ILeiloavel ultimoProduto() {
		List<? extends ILeiloavel> emLeilao = mercado.getProdutosEmLeilao();
		return emLeilao.get(emLeilao.size()-1);
	}
	private static final int minutoEmMs = 60*1000;
	private static final int diaEmMs = 24*60*minutoEmMs;
	
	@BeforeClass
	public static void newMercadoLeilao() {
		MercadoLeilao mercado = new MercadoLeilao();
		
		assertTrue(mercado.getUsuariosCadastrados().isEmpty());
		assertTrue(mercado.getProdutosEmLeilao().isEmpty());
		assertTrue(mercado.getProdutosVendidos().isEmpty());
		assertTrue(mercado.getProdutosVencidosENaoVendidos().isEmpty());
	}
	
	private MercadoLeilao mercado;
	@Before
	public void fixtureSetup() throws Exception {
		mercado = new MercadoLeilao();
	}
	
	@Test
	public void cadastrarUsuario_sucesso() throws Exception {
		String nome = "Fulano",
				endereco = "endereco",
				email = "email",
				cpf = geraCPF();
		
		mercado.cadastrarUsuario(nome, endereco, email, cpf);
		
		assertEquals(1, mercado.getUsuariosCadastrados().size());
		IUsuario usuario = mercado.getUsuario(cpf);
		assertEquals(nome, usuario.getNome());
		assertEquals(endereco, usuario.getEndereco());
		assertEquals(email, usuario.getEmail());
		assertEquals(cpf, usuario.getCpf());
	}

	@Test(expected=Exception.class)
	public void cadastrarUsuario_duplicata() throws Exception {
		String nome = "Fulano",
				endereco = "endereco",
				email = "email",
				cpf = geraCPF();
		mercado.cadastrarUsuario(nome, endereco, email, cpf);

		mercado.cadastrarUsuario(nome, endereco, email, cpf);
	}
	
	// cadastrarProduto
	
	class cadastrarProduto_fixtureSetup {
		IUsuario leiloador;
		String nome,
				descricao,
				cpfLeiloador;
		Double lanceMinimo;
		Date dataLimite;
		cadastrarProduto_fixtureSetup() throws Exception{
			nome = "caderno";
			descricao = "amontoado de folhas";
			cpfLeiloador = cadastrarUsuario("Ciclano").getCpf();
			lanceMinimo = 5.0;
			dataLimite = new Date(System.currentTimeMillis() + 7*diaEmMs);
		}
	}
	
	@Test
	public void cadastrarProduto_sucesso() throws Exception {
		cadastrarProduto_fixtureSetup fs = new cadastrarProduto_fixtureSetup();
		
		mercado.cadastrarProduto(fs.nome, fs.descricao, fs.lanceMinimo, fs.cpfLeiloador, fs.dataLimite);
		
		assertEquals(1, mercado.getProdutosEmLeilao().size());
		ILeiloavel produto = ultimoProduto();
		assertEquals(fs.nome, produto.getNome());
		assertEquals(fs.descricao, produto.getDescricao());
		assertEquals(fs.cpfLeiloador, produto.getCpfLeiloador());
	}

	@Test(expected=Exception.class)
	public void cadastrarProduto_duplicataEmLeilao() throws Exception {
		cadastrarProduto_fixtureSetup fs = new cadastrarProduto_fixtureSetup();
		mercado.cadastrarProduto(fs.nome, fs.descricao, fs.lanceMinimo, fs.cpfLeiloador, fs.dataLimite);
		
		mercado.cadastrarProduto(fs.nome, fs.descricao, fs.lanceMinimo, fs.cpfLeiloador, fs.dataLimite);
	}

	@Test(expected=Exception.class)
	public void cadastrarProduto_duplicataVencida() throws Exception {
		cadastrarProduto_fixtureSetup fs = new cadastrarProduto_fixtureSetup();
		mercado.cadastrarProduto(fs.nome, fs.descricao, fs.lanceMinimo, fs.cpfLeiloador, fs.dataLimite);
		((Produto)ultimoProduto()).setDataLimite(new Date(System.currentTimeMillis()-1));

		mercado.cadastrarProduto(fs.nome, fs.descricao, fs.lanceMinimo, fs.cpfLeiloador, fs.dataLimite);
	}

	@Test(expected=Exception.class)
	public void cadastrarProduto_duplicataVendida() throws Exception {
		cadastrarProduto_fixtureSetup fs = new cadastrarProduto_fixtureSetup();
		mercado.cadastrarProduto(fs.nome, fs.descricao, fs.lanceMinimo, fs.cpfLeiloador, fs.dataLimite);
		Produto produto = (Produto) ultimoProduto();
		IUsuario comprador = cadastrarUsuario("Beltrano");
		mercado.daLance(produto.getNome(), comprador.getCpf(), 7.0);
		produto.setDataLimite(new Date(System.currentTimeMillis()-1));

		mercado.cadastrarProduto(fs.nome, fs.descricao, fs.lanceMinimo, fs.cpfLeiloador, fs.dataLimite);
	}

	@Test
	public void cadastrarProduto_lanceMinimoInvalido() throws Exception {
		cadastrarProduto_fixtureSetup fs = new cadastrarProduto_fixtureSetup();
		fs.lanceMinimo = -1.0;
		
		mercado.cadastrarProduto(fs.nome, fs.descricao, fs.lanceMinimo, fs.cpfLeiloador, fs.dataLimite);
	}

	@Test(expected=Exception.class)
	public void cadastrarProduto_usuarioInexistente() throws Exception {
		cadastrarProduto_fixtureSetup fs = new cadastrarProduto_fixtureSetup();
		fs.cpfLeiloador = geraCPF();
		
		mercado.cadastrarProduto(fs.nome, fs.descricao, fs.lanceMinimo, fs.cpfLeiloador, fs.dataLimite);
	}

	@Test(expected=Exception.class)
	public void cadastrarProduto_dataInvalida() throws Exception {
		cadastrarProduto_fixtureSetup fs = new cadastrarProduto_fixtureSetup();
		fs.dataLimite = new Date(System.currentTimeMillis() - diaEmMs);
		
		mercado.cadastrarProduto(fs.nome, fs.descricao, fs.lanceMinimo, fs.cpfLeiloador, fs.dataLimite);
	}
	
	// daLance
	
	class daLance_fixtureSetup {
		IUsuario leiloador;
		ILeiloavel produto;
		IUsuario comprador;
		Double valorLance;
		public daLance_fixtureSetup() throws Exception {
			leiloador = cadastrarUsuario("Ciclano");
			produto = cadastrarProduto("caderno", leiloador, 5.0);
			comprador = cadastrarUsuario("Beltrano");
			valorLance = 7.0;
		}
	}
	
	@Test
	public void daLance() throws Exception {
		IUsuario leiloador = cadastrarUsuario("Ciclano");
		ILeiloavel produto = cadastrarProduto("caderno", leiloador, 5.0);
		IUsuario comprador = cadastrarUsuario("Beltrano");
		Double valorLance = 7.0;
		
		mercado.daLance(produto.getNome(), comprador.getCpf(), valorLance);
		
		Lance lance = ((Produto) produto).getLanceMaisRecente();
		assertTrue(mercado.retornaLancesDeUmUsuario(comprador.getCpf()).contains(lance));
		assertTrue(mercado.getProdutosQueDeuLance(comprador.getCpf()).contains(produto));
	}

	@Test(expected=Exception.class)
	public void daLance_produtoInexistente() throws Exception {
		//IUsuario leiloador = cadastrarUsuario("Ciclano");
		//ILeiloavel produto = cadastrarProduto("caderno", leiloador, 5.0);
		IUsuario comprador = cadastrarUsuario("Beltrano");
		Double valorLance = 7.0;
		
		mercado.daLance("caderno", comprador.getCpf(), valorLance);
	}
	
	@Test(expected=Exception.class)
	public void daLance_compradorInexistente() throws Exception {
		IUsuario leiloador = cadastrarUsuario("Ciclano");
		ILeiloavel produto = cadastrarProduto("caderno", leiloador, 5.0);
		//IUsuario comprador = cadastrarUsuario("Beltrano");
		Double valorLance = 7.0;
		
		mercado.daLance(produto.getNome(), geraCPF(), valorLance);
	}
	
	@Test(expected=Exception.class)
	public void daLance_valorInferiorAoMinimo() throws Exception {
		IUsuario leiloador = cadastrarUsuario("Ciclano");
		ILeiloavel produto = cadastrarProduto("caderno", leiloador, 5.0);
		IUsuario comprador = cadastrarUsuario("Beltrano");
		//Double valorLance = 7.0;
		
		mercado.daLance(produto.getNome(), comprador.getCpf(), 4.0);
	}

	@Test(expected=Exception.class)
	public void daLance_valorInferiorAoUltimo() throws Exception {
		IUsuario leiloador = cadastrarUsuario("Ciclano");
		ILeiloavel produto = cadastrarProduto("caderno", leiloador, 5.0);
		IUsuario comprador = cadastrarUsuario("Beltrano");
		Double valorLance = 7.0;
		mercado.daLance(produto.getNome(), comprador.getCpf(), valorLance);
		
		mercado.daLance(produto.getNome(), comprador.getCpf(), 6.0);
	}

	//
	
	class MyFixtureSetup {
		class MyUsuario {
			IUsuario user;
			List<Lance> lancesEfetuados;
			List<ILeiloavel> produtosLeiloados;
			List<ILeiloavel> produtosQueDeuLance;
			public MyUsuario(String nome) throws Exception {
				this.user = Test_MercadoLeilao.this.cadastrarUsuario(nome);
				this.lancesEfetuados = new LinkedList<>();
				this.produtosLeiloados = new LinkedList<>();
				this.produtosQueDeuLance = new LinkedList<>();
			}
			void daLance(ILeiloavel leiloavel) throws Exception {
				Produto produto = (Produto) leiloavel;
				Double valorDoLance = Math.max(
						produto.getLanceMinimo(),
						produto.getValorUltimoLance()+1);
				Test_MercadoLeilao.this.mercado.daLance(produto.getNome(), this.user.getCpf(), valorDoLance);
				this.lancesEfetuados.add(produto.getLanceMaisRecente());
				this.produtosQueDeuLance.add(produto);
			}
		}
		class Fulano extends MyUsuario {
			public Fulano() throws Exception {
				super("Fulano");
				this.produtosLeiloados.add(Test_MercadoLeilao.this.cadastrarProduto("caderno", this.user, 5.0));
				this.produtosLeiloados.add(Test_MercadoLeilao.this.cadastrarProduto("agenda", this.user, 3.0));
				this.produtosLeiloados.add(Test_MercadoLeilao.this.cadastrarProduto("livro", this.user, 10.0));
				this.produtosLeiloados.add(Test_MercadoLeilao.this.cadastrarProduto("mochila", this.user, 25.0));
			}
		}
		class Ciclano extends MyUsuario {
			public Ciclano() throws Exception {
				super("Ciclano");
				this.produtosLeiloados.add(Test_MercadoLeilao.this.cadastrarProduto("lapis", this.user, 1.0));
				this.produtosLeiloados.add(Test_MercadoLeilao.this.cadastrarProduto("borracha", this.user, 1.0));
				this.produtosLeiloados.add(Test_MercadoLeilao.this.cadastrarProduto("apontador", this.user, 1.0));
				this.produtosLeiloados.add(Test_MercadoLeilao.this.cadastrarProduto("papiseira", this.user, 2.0));
			}
		}
		class Beltrano extends MyUsuario {
			public Beltrano() throws Exception {
				super("Beltrano");
				this.produtosLeiloados.add(Test_MercadoLeilao.this.cadastrarProduto("caneta", this.user, 2.0));
				this.produtosLeiloados.add(Test_MercadoLeilao.this.cadastrarProduto("corretivo", this.user, 3.0));
				this.produtosLeiloados.add(Test_MercadoLeilao.this.cadastrarProduto("marcador", this.user, 3.0));
				this.produtosLeiloados.add(Test_MercadoLeilao.this.cadastrarProduto("penal", this.user, 3.0));
			}
		}
		MyUsuario fulano, ciclano, beltrano;
		public MyFixtureSetup() throws Exception {
			this.fulano = new Fulano();
			this.ciclano = new Ciclano();
			this.beltrano = new Beltrano();
			this.gerarLances();
			this.expirarProdutos();
		}
		byte expirar, expirou = expirar = 0b10;
		byte darLance, deuLance = darLance = 0b01;
		byte nao = 0b00;
		public void gerarLances() throws Exception {
			MyUsuario lista[] = { this.fulano, this.ciclano, this.beltrano };
			for(MyUsuario user : lista) for(MyUsuario other : lista) if(user != other)
				for(byte iprod = 0; iprod < 4; iprod++)//ILeiloavel prod : other.produtosLeiloados)
					if((iprod & darLance) == darLance) {
						ILeiloavel prod = other.produtosLeiloados.get(iprod);
						user.daLance(prod);
					}
		}
		public void expirarProdutos() {
			Date passado = new Date(System.currentTimeMillis()-1);
			for(MyUsuario user : new MyUsuario[] { this.fulano, this.ciclano, this.beltrano })
				for(byte iprod = 0; iprod < 4; iprod++)//ILeiloavel prod : user.produtosLeiloados)
					if((iprod & expirar) == expirar) {
						ILeiloavel prod = user.produtosLeiloados.get(iprod);
						((Produto)prod).setDataLimite(passado);
					}
		}
		List<? extends ILeiloavel> getEmLeilao() {
			List<ILeiloavel> lista = new LinkedList<>();
			for(MyUsuario user : new MyUsuario[] { this.fulano, this.ciclano, this.beltrano })
				for(byte iprod = 0; iprod < 4; iprod++)
					if((iprod & expirou) == nao)
						lista.add(user.produtosLeiloados.get(iprod));
			return lista;
		}
		List<? extends ILeiloavel> getVencidos() {
			List<ILeiloavel> lista = new LinkedList<>();
			for(MyUsuario user : new MyUsuario[] { this.fulano, this.ciclano, this.beltrano })
				for(byte iprod = 0; iprod < 4; iprod++)
					if((iprod & expirou) == expirou && (iprod & deuLance) == nao)
						lista.add(user.produtosLeiloados.get(iprod));
			return lista;
		}
		List<? extends ILeiloavel> getVendidos() {
			List<ILeiloavel> lista = new LinkedList<>();
			for(MyUsuario user : new MyUsuario[] { this.fulano, this.ciclano, this.beltrano })
				for(byte iprod = 0; iprod < 4; iprod++)
					if((iprod & expirou) == expirou && (iprod & deuLance) == deuLance)
						lista.add(user.produtosLeiloados.get(iprod));
			return lista;
		}
	}
	
	@Test
	public void getProdutosEmLeilao() throws Exception {
		MyFixtureSetup myfs = new MyFixtureSetup();
		List<? extends ILeiloavel> emLeilao = myfs.getEmLeilao();

		// mercado.getProdutosEmLeilao(); // se "atualizarListasDeProdutos", não ocorrem falhas
		List<? extends ILeiloavel> produtosEmLeilao = mercado.getProdutosEmLeilao();
		
		assertTrue(produtosEmLeilao.containsAll(emLeilao));
		assertTrue(emLeilao.containsAll(produtosEmLeilao));
	}

	@Test
	public void getProdutosVencidos() throws Exception {
		MyFixtureSetup myfs = new MyFixtureSetup();
		List<? extends ILeiloavel> vencidos = myfs.getVencidos();
		
		List<? extends ILeiloavel> produtosVencidos = mercado.getProdutosVencidosENaoVendidos();
		
		assertTrue(produtosVencidos.containsAll(vencidos));
		assertTrue(vencidos.containsAll(produtosVencidos));
	}

	@Test
	public void getProdutosVendidos() throws Exception {
		MyFixtureSetup myfs = new MyFixtureSetup();
		List<? extends ILeiloavel> vendidos = myfs.getVendidos();

		// mercado.getProdutosEmLeilao(); // se "atualizarListasDeProdutos", não ocorrem falhas
		List<? extends ILeiloavel> produtosVendidos = mercado.getProdutosVendidos();
		
		assertTrue(produtosVendidos.containsAll(vendidos));
		assertTrue(vendidos.containsAll(produtosVendidos));
	}

	@Test
	public void retornaTodosOsLancesEfetuados() throws Exception {
		MyFixtureSetup myfs = new MyFixtureSetup();
		List<Lance> lancesEfetuados = new LinkedList<>();
		lancesEfetuados.addAll(myfs.fulano.lancesEfetuados);
		lancesEfetuados.addAll(myfs.ciclano.lancesEfetuados);
		lancesEfetuados.addAll(myfs.beltrano.lancesEfetuados);
		
		List<Lance> todosOsLancesEfetuados = mercado.retornaTodosOsLancesEfetuados();
		
		assertTrue(lancesEfetuados.containsAll(todosOsLancesEfetuados));
		assertTrue(todosOsLancesEfetuados.containsAll(lancesEfetuados));
	}
	
	// retornaLancesDeUmUsuario
	
	public void retornaLancesDeUmUsuario(MyUsuario myUser) throws Exception {
		List<Lance> lancesEfetuados = mercado.retornaLancesDeUmUsuario(myUser.user.getCpf());
		
		assertTrue(myUser.lancesEfetuados.containsAll(lancesEfetuados));
		assertTrue(lancesEfetuados.containsAll(myUser.lancesEfetuados));
	}
	@Test
	public void retornaLancesDeUmUsuario_fulano() throws Exception {
		MyFixtureSetup myfs = new MyFixtureSetup();
		retornaLancesDeUmUsuario(myfs.fulano);
	}
	@Test
	public void retornaLancesDeUmUsuario_ciclano() throws Exception {
		MyFixtureSetup myfs = new MyFixtureSetup();
		retornaLancesDeUmUsuario(myfs.ciclano);
	}
	@Test
	public void retornaLancesDeUmUsuario_beltrano() throws Exception {
		MyFixtureSetup myfs = new MyFixtureSetup();
		retornaLancesDeUmUsuario(myfs.beltrano);
	}
	@Test(expected=Exception.class)
	public void retornaLancesDeUmUsuario_usuarioNaoCadastrado() throws Exception {
		Usuario naoCadastrado = new Usuario(geraCPF(), "nome");
		List<Lance> lancesEfetuados = mercado.retornaLancesDeUmUsuario(naoCadastrado.getCpf());
	}
	
	// retornaProdutosDeUmLeiloador
	
	public void retornaProdutosDeUmLeiloador(MyUsuario myUser) throws Exception {
		List<Produto> produtosLeiloados = mercado.retornaProdutosDeUmLeiloador(myUser.user.getCpf());

		assertTrue(myUser.produtosLeiloados.containsAll(produtosLeiloados));
		assertTrue(produtosLeiloados.containsAll(myUser.produtosLeiloados));
	}
	@Test
	public void retornaProdutosDeUmLeiloador_fulano() throws Exception {
		MyFixtureSetup myfs = new MyFixtureSetup();
		retornaProdutosDeUmLeiloador(myfs.fulano);
	}
	@Test
	public void retornaProdutosDeUmLeiloador_ciclano() throws Exception {
		MyFixtureSetup myfs = new MyFixtureSetup();
		retornaProdutosDeUmLeiloador(myfs.ciclano);
	}
	@Test
	public void retornaProdutosDeUmLeiloador_beltrano() throws Exception {
		MyFixtureSetup myfs = new MyFixtureSetup();
		retornaProdutosDeUmLeiloador(myfs.beltrano);
	}
	@Test(expected=Exception.class)
	public void retornaProdutosDeUmLeiloador_usuarioNaoCadastrado() throws Exception {
		Usuario naoCadastrado = new Usuario(geraCPF(), "nome");
		List<Produto> produtosLeiloados = mercado.retornaProdutosDeUmLeiloador(naoCadastrado.getCpf());
	}
	
	// getProdutosQueDeuLance
	
	public void getProdutosQueDeuLance(MyUsuario myUser) throws Exception {
		// Exercise
		List<? extends ILeiloavel> produtosQueDeuLance = mercado.getProdutosQueDeuLance(myUser.user.getCpf());
		// Test
		assertTrue(myUser.produtosQueDeuLance.containsAll(produtosQueDeuLance));
		assertTrue(produtosQueDeuLance.containsAll(myUser.produtosQueDeuLance));
	}
	@Test
	public void getProdutosQueDeuLance_fulano() throws Exception {
		MyFixtureSetup myfs = new MyFixtureSetup();
		getProdutosQueDeuLance(myfs.fulano);
	}
	@Test
	public void getProdutosQueDeuLance_ciclano() throws Exception {
		MyFixtureSetup myfs = new MyFixtureSetup();
		getProdutosQueDeuLance(myfs.ciclano);
	}
	@Test
	public void getProdutosQueDeuLance_beltrano() throws Exception {
		MyFixtureSetup myfs = new MyFixtureSetup();
		getProdutosQueDeuLance(myfs.beltrano);
	}
	@Test(expected=Exception.class)
	public void getProdutosQueDeuLance_usuarioNaoCadastrado() throws Exception {
		Usuario naoCadastrado = new Usuario(geraCPF(), "nome");
		// Exercise
		List<? extends ILeiloavel> produtosQueDeuLance = mercado.getProdutosQueDeuLance(naoCadastrado.getCpf());
	}
}
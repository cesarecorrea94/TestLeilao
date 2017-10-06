package modelo;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestControle_LancesEfetuadosPorUmUsuario {

	private Usuario leiloador;
	private Produto produto;

	@Before
	public void fixtureSetup() {
		leiloador = new Usuario("12345678901", "Fulano");
		produto = new Produto("Caderno", "Amontoado de Folhas", 5.0, leiloador);
	}

	@Test
	public void forFalse() throws Exception {

		List<Lance> lista = produto.lancesEfetuadosPorUmUsuario(leiloador.getCpf());

		assertEquals(0, lista.size());
	}

	@Test
	public void forTrue_ifFalse() throws Exception {
		Usuario donoDoLance = new Usuario("98765432101", "Ciclano");
		Lance lance = new Lance(7.0, donoDoLance);
		produto.recebaLance(lance);

		List<Lance> lista = produto.lancesEfetuadosPorUmUsuario(leiloador.getCpf());

		assertEquals(0, lista.size());
		assertFalse(lista.contains(lance));
		}

	@Test
	public void forTrue_ifTrue() throws Exception {
		Usuario donoDoLance = new Usuario("98765432101", "Ciclano");
		Lance lance = new Lance(7.0, donoDoLance);
		produto.recebaLance(lance);

		List<Lance> lista = produto.lancesEfetuadosPorUmUsuario(donoDoLance.getCpf());

		assertEquals(1, lista.size());
		assertTrue(lista.contains(lance));
	}

}

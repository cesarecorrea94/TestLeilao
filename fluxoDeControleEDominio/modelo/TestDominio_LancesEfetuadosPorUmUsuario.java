package modelo;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestDominio_LancesEfetuadosPorUmUsuario {

	private Usuario leiloador;
	private Produto produto;
	private Usuario donoDoLance;
	private Lance lance;

	@Before
	public void fixtureSetup() {
		leiloador = new Usuario("12345678901", "Fulano");
		produto = new Produto("Caderno", "Amontoado de Folhas", 5.0, leiloador);
		donoDoLance = new Usuario("98765432101", "Ciclano");
		lance = new Lance(7.0, donoDoLance);
		produto.recebaLance(lance);
	}

	@Test
	public void cpfRemoveLast() throws Exception {
		String cpf = donoDoLance.getCpf();
		cpf = cpf.substring(0, cpf.length()-1);

		List<Lance> lista = produto.lancesEfetuadosPorUmUsuario(cpf);

		assertEquals(0, lista.size());
		assertFalse(lista.contains(lance));
	}

	@Test
	public void cpfReplaceLast_add1() throws Exception {
		String cpf = donoDoLance.getCpf();
		String last = cpf.substring(cpf.length()-1);
		last = String.valueOf( (Integer.valueOf(last)+1) % 10);
		cpf = cpf.substring(0, cpf.length()-1).concat(last);

		List<Lance> lista = produto.lancesEfetuadosPorUmUsuario(cpf);

		assertEquals(0, lista.size());
		assertFalse(lista.contains(lance));
	}

	@Test
	public void cpfReplaceLast_sub1() throws Exception {
		String cpf = donoDoLance.getCpf();
		String last = cpf.substring(cpf.length()-1);
		last = String.valueOf( (Integer.valueOf(last)-1 +10) % 10);
		cpf = cpf.substring(0, cpf.length()-1).concat(last);

		List<Lance> lista = produto.lancesEfetuadosPorUmUsuario(cpf);

		assertEquals(0, lista.size());
		assertFalse(lista.contains(lance));
	}

	@Test
	public void cpfReplace_add1() throws Exception {
		String cpf = donoDoLance.getCpf();
		cpf = String.valueOf(Long.valueOf(cpf)+1);

		List<Lance> lista = produto.lancesEfetuadosPorUmUsuario(cpf);

		assertEquals(0, lista.size());
		assertFalse(lista.contains(lance));
	}

	@Test
	public void cpfReplace_sub1() throws Exception {
		String cpf = donoDoLance.getCpf();
		cpf = String.valueOf(Long.valueOf(cpf)-1);

		List<Lance> lista = produto.lancesEfetuadosPorUmUsuario(cpf);

		assertEquals(0, lista.size());
		assertFalse(lista.contains(lance));
	}

	@Test
	public void cpfConcat0() throws Exception {
		String cpf = donoDoLance.getCpf();
		cpf = cpf.concat("0");

		List<Lance> lista = produto.lancesEfetuadosPorUmUsuario(cpf);

		assertEquals(0, lista.size());
		assertFalse(lista.contains(lance));
	}

	
}

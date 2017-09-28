package modelo;

import interfaces.ILeiloavel;
import interfaces.IMercadoLeilao;
import interfaces.IUsuario;
import interfaces.IVendido;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MercadoLeilao implements IMercadoLeilao, Serializable {
	
	
	private static final long serialVersionUID = 1L;
	private Map<String, Usuario> usuarios;
	private List<Produto> produtosEmLeilao;
	private List<Produto> produtosVendidos;
	private List<Produto> produtosVencidosENaoVendidos;
	
	public MercadoLeilao() {
		this.usuarios = new HashMap<String, Usuario>();
		this.produtosEmLeilao = new ArrayList<Produto>();
		this.produtosVendidos = new ArrayList<Produto>();
		this.produtosVencidosENaoVendidos = new ArrayList<Produto>();
	}
	
	public void cadastrarUsuario(String nome, String endereco, String email, String cpf) throws Exception {
		if(usuarioExiste(cpf)) {
			throw new Exception("O usuario ja existe.");
		}
		else {
			Usuario usuario = new Usuario(cpf, nome);
			usuario.setEndereco(endereco);
			usuario.setEmail(email);
			this.usuarios.put(cpf, usuario);
		}
	}
	
	public void cadastrarProduto(String nome, String descricao, Double lanceMinimo, String cpfLeiloador, Date dataLimite) throws Exception {
		if(!produtoJaExiste(nome) && usuarioExiste(cpfLeiloador)) {
			Usuario leiloador = usuarios.get(cpfLeiloador);
			Produto produto = new Produto(nome, descricao, lanceMinimo, leiloador);
			produto.setDataLimite(dataLimite);
			produtosEmLeilao.add(produto);
			leiloador.setBemOfertado(produto);
		}
		else
			throw new Exception("O produto ja existe ou o leiloador nao esta cadastrado.");
	}
	
	public List<? extends ILeiloavel> getProdutosEmLeilao() {
		atualizarListasDeProdutos();
		List<ILeiloavel> retornoProdutosEmLeilao = new ArrayList<ILeiloavel>();
		retornoProdutosEmLeilao.addAll(this.produtosEmLeilao);
		return retornoProdutosEmLeilao;
	}
	
	public List<? extends ILeiloavel> getProdutosVencidosENaoVendidos() {
		atualizarListasDeProdutos();
		List<ILeiloavel> retornoProdutosVencidos = new ArrayList<ILeiloavel>();
		retornoProdutosVencidos.addAll(this.produtosVencidosENaoVendidos);
		return retornoProdutosVencidos;
	}
	
	public List<? extends IVendido> getProdutosVendidos() {
		atualizarListasDeProdutos();
		List<IVendido> retornoProdutosVendidos = new ArrayList<IVendido>();
		retornoProdutosVendidos.addAll(this.produtosVendidos);
		return retornoProdutosVendidos;
	}
	
	public List<IUsuario> getUsuariosCadastrados() {
		List<IUsuario> retornoUsuariosCadastrados = new ArrayList<IUsuario>();
		retornoUsuariosCadastrados.addAll(this.usuarios.values());
		return retornoUsuariosCadastrados;
	}

	public void daLance(String nomeProduto, String cpfComprador, Double valorLance) throws Exception {
		atualizarListasDeProdutos();
		Lance lance = new Lance(valorLance, (Usuario)this.getUsuario(cpfComprador));
		Produto produto = produtosEmLeilao.get(getIndexProdutoEmLeilaoViaNome(nomeProduto));
		Double lanceMinimo = produto.getLanceMinimo();
		Double lanceAtual = produto.getValorUltimoLance();
		if(usuarioExiste(cpfComprador) && valorLance >= lanceMinimo && valorLance > lanceAtual) {
			produto.recebaLance(lance);
			lance.setProdutoQueRecebeuOLance(produto);
		}
		else
			throw new Exception("O valor do lance eh inferior ao necessario ou o comprador nao esta cadastrado.");
	}
	
	public List<Lance> retornaTodosOsLancesEfetuados() {
		List<Lance> retornoLances = new ArrayList<Lance>();
		retornoLances.addAll(getTodosOsLancesEfetuadosEmProdutosEmLeilao());
		retornoLances.addAll(getTodosOsLancesEfetuadosEmProdutosVendidos());
		return retornoLances;
	}
	
	public List<Lance> retornaLancesDeUmUsuario(String cpfUsuario) throws Exception {
		if(!usuarioExiste(cpfUsuario))
			throw new Exception("O usuario nao esta cadastrado.");
		List<Lance> retornoLances = new ArrayList<Lance>();
		retornoLances.addAll(getLancesDeUmUsuarioEmProdutosAindaEmLeilao(cpfUsuario));
		retornoLances.addAll(getLancesDeUmUsuarioEmProdutosVendidos(cpfUsuario));
		return retornoLances;
	}
	
	public List<Produto> retornaProdutosDeUmLeiloador(String cpfUsuario) throws Exception {
		atualizarListasDeProdutos();
		if(!usuarioExiste(cpfUsuario))
			throw new Exception("O usuario nao esta cadastrado.");
		List<Produto> retornoProdutos = new ArrayList<Produto>();
		retornoProdutos.addAll(getProdutosEmLeilaoPorUmUsuario(cpfUsuario));
		retornoProdutos.addAll(getProdutosVendidosPorUmUsuario(cpfUsuario));
		retornoProdutos.addAll(getProdutosVencidosMasNaoVendidosPorUmUsuario(cpfUsuario));
		return retornoProdutos;
	}
	
	public List<? extends ILeiloavel> getProdutosQueDeuLance(String cpf) throws Exception {
		atualizarListasDeProdutos();
		if(!usuarioExiste(cpf))
			throw new Exception("O usuario nao esta cadastrado.");
		else {
			List<ILeiloavel> produtosQueDeuLance = new ArrayList<ILeiloavel>();
			produtosQueDeuLance.addAll(getProdutosEmLeilaoQueDeuLance(cpf));
			produtosQueDeuLance.addAll(getProdutosVendidosQueDeuLance(cpf));
			return produtosQueDeuLance;
		}
	}
	
	public IUsuario getUsuario(String cpf) throws Exception {
		return this.usuarios.get(cpf);
	}
	
	
///////////////////////////////////   METODOS PRIVADOS   ///////////////////////////////////
	
	
	private void atualizarListasDeProdutos() {
		for(int i=0; i<produtosEmLeilao.size(); i++) {
			Produto produto = produtosEmLeilao.get(i);
			atualizaSeFoiVendido(produto);
			atualizaSeVenceuENaoFoiVendido(produto);
		}
	}
	
	private void atualizaSeFoiVendido(Produto produto) {
		int qtdLances = produto.retornaTodosOsLancesFeitosNesseProduto().size();
		if(produto.dataDoProdutoExpirou() && qtdLances > 0) {
			produtosVendidos.add(produto);
			String cpfDonoDoLance = produto.getLanceMaisRecente().getCpfDonoDoLance();
			Usuario comprador = usuarios.get(cpfDonoDoLance);
			comprador.setBemComprado(produto);
			produto.setComprador(comprador);
			produtosEmLeilao.remove(produto);
		}
	}
	
	private void atualizaSeVenceuENaoFoiVendido(Produto produto) {
		int qtdLances = produto.retornaTodosOsLancesFeitosNesseProduto().size();
		if(produto.dataDoProdutoExpirou() && qtdLances == 0) {
			produtosVencidosENaoVendidos.add(produto);
			produtosEmLeilao.remove(produto);
		}
	}
	
	private boolean produtoJaExiste(String nome) {
		return produtoExisteEntreOsEmLeilao(nome) || produtoExisteEntreOsVendidos(nome) || 
				produtoExisteEntreOsVencidosENaoVendidos(nome);
	}
	
	private boolean produtoExisteEntreOsEmLeilao(String nome) {
		for(int i=0; i<produtosEmLeilao.size(); i++) {
			if(nome.equalsIgnoreCase(produtosEmLeilao.get(i).getNome())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean produtoExisteEntreOsVendidos(String nome) {
		for(int i=0; i<produtosVendidos.size(); i++) {
			if(nome.equalsIgnoreCase(produtosVendidos.get(i).getNome())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean produtoExisteEntreOsVencidosENaoVendidos(String nome) {
		for(int i=0; i<produtosVencidosENaoVendidos.size(); i++) {
			if(nome.equalsIgnoreCase(produtosVencidosENaoVendidos.get(i).getNome())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean usuarioExiste(String cpfUsuario) {
		return (usuarios.containsKey(cpfUsuario));
	}
	
	private Integer getIndexProdutoEmLeilaoViaNome(String nomeProduto) throws Exception {
		for(int i=0; i<produtosEmLeilao.size(); i++) {
			if(nomeProduto.equalsIgnoreCase(produtosEmLeilao.get(i).getNome()))
				return i;
		}
		throw new Exception("Nao existe produto cadastrado com esse nome.");
	}
	
	private List<Lance> getLancesDeUmUsuarioEmProdutosAindaEmLeilao(String cpfUsuario) {
		List<Lance> retornoLances = new ArrayList<Lance>();
		for(int i=0; i<produtosEmLeilao.size(); i++) {
			retornoLances.addAll(produtosEmLeilao.get(i).lancesEfetuadosPorUmUsuario(cpfUsuario));
		}
		return retornoLances;
	}
	
	private List<Lance> getLancesDeUmUsuarioEmProdutosVendidos(String cpfUsuario) {
		List<Lance> retornoLances = new ArrayList<Lance>();
		for(int i=0; i<produtosVendidos.size(); i++) {
			retornoLances.addAll(produtosVendidos.get(i).lancesEfetuadosPorUmUsuario(cpfUsuario));
		}
		return retornoLances;
	}
	
	private List<Lance> getTodosOsLancesEfetuadosEmProdutosEmLeilao() {
		List<Lance> retornoLances = new ArrayList<Lance>();
		for(int i=0; i<produtosEmLeilao.size(); i++) {
			retornoLances.addAll(produtosEmLeilao.get(i).retornaTodosOsLancesFeitosNesseProduto());
		}
		return retornoLances;
	}
	
	private List<Lance> getTodosOsLancesEfetuadosEmProdutosVendidos() {
		List<Lance> retornoLances = new ArrayList<Lance>();
		for(int i=0; i<produtosVendidos.size(); i++) {
			retornoLances.addAll(produtosVendidos.get(i).retornaTodosOsLancesFeitosNesseProduto());
		}
		return retornoLances;
	}
	
	private List<Produto> getProdutosEmLeilaoPorUmUsuario(String cpfUsuario) {
		List<Produto> retornoProdutos = new ArrayList<Produto>();
		for(int i=0; i<produtosEmLeilao.size(); i++) {
			if(cpfUsuario.equals(produtosEmLeilao.get(i).getCpfLeiloador()))
				retornoProdutos.add(produtosEmLeilao.get(i));
		}
		return retornoProdutos;
	}
	
	private List<Produto> getProdutosVendidosPorUmUsuario(String cpfUsuario) {
		List<Produto> retornoProdutos = new ArrayList<Produto>();
		for(int i=0; i<produtosVendidos.size(); i++) {
			if(cpfUsuario.equals(produtosVendidos.get(i).getCpfLeiloador()))
				retornoProdutos.add(produtosVendidos.get(i));
		}
		return retornoProdutos;
	}
	
	private List<Produto> getProdutosVencidosMasNaoVendidosPorUmUsuario(String cpfUsuario) {
		List<Produto> retornoProdutos = new ArrayList<Produto>();
		for(int i=0; i<produtosVencidosENaoVendidos.size(); i++) {
			if(cpfUsuario.equals(produtosVencidosENaoVendidos.get(i).getCpfLeiloador()))
				retornoProdutos.add(produtosVencidosENaoVendidos.get(i));
		}
		return retornoProdutos;
	}
	
	
	private boolean compradorDeuLanceNesseProduto(String cpfComprador, Produto produto) {
		List<Lance> lances = produto.retornaTodosOsLancesFeitosNesseProduto();
		for(int i=0; i<lances.size(); i++) {
			if(lances.get(i).getCpfDonoDoLance().equalsIgnoreCase(cpfComprador))
				return true;
		}
		return false;
	}
	
	
	private List<? extends ILeiloavel> getProdutosEmLeilaoQueDeuLance(String cpf) {
		List<ILeiloavel> retornoProdutos = new ArrayList<ILeiloavel>();
		for(int i=0; i<produtosEmLeilao.size(); i++) {
			Produto produto = produtosEmLeilao.get(i);
			if(compradorDeuLanceNesseProduto(cpf, produto))
				retornoProdutos.add(produto);
		}
		return retornoProdutos;
	}
	
	private List<? extends ILeiloavel> getProdutosVendidosQueDeuLance(String cpf) {
		List<ILeiloavel> retornoProdutos = new ArrayList<ILeiloavel>();
		for(int i=0; i<produtosVendidos.size(); i++) {
			Produto produto = produtosVendidos.get(i);
			if(compradorDeuLanceNesseProduto(cpf, produto))
				retornoProdutos.add(produto);
		}
		return retornoProdutos;
	}
}
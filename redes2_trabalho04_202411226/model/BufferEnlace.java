/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/05/2026
* Ultima alteracao.: 27/05/2026
* Nome.............: BufferEnlace
* Funcao...........: Classe que gerencia as operacoes de cada buffer contendo os pacotes
                     de estado de enlace.
                     
*************************************************************** */

package model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class BufferEnlace {
	// Variaveis e instancias
	private Roteador r;
	private CopyOnWriteArrayList<EntradaBuffer> entradas;
	private CopyOnWriteArrayList<Roteador> listaRoteadores;

	public BufferEnlace(Roteador r, CopyOnWriteArrayList<Roteador> listaRoteadores) {
		entradas = new CopyOnWriteArrayList<>();
		this.r = r;
		this.listaRoteadores = listaRoteadores;
	}

	public void criarEntradasIniciais() {
		for (Roteador r : listaRoteadores) {
			if (r.getNome().equals(this.r.getNome())) continue;
			EntradaBuffer e = new EntradaBuffer(r, null);
			e.carregarFlags();
			entradas.add(e);
		}
	}

	public void alterarEntrada(EntradaBuffer e) {
		for (int i = 0; i < entradas.size(); i++) {
      EntradaBuffer atual = entradas.get(i);
			Roteador rot = atual.getRoteadorEntrada();
			String rotAtual = rot.getNome();

			if (rotAtual.equals(e.getRoteadorEntrada().getNome())) {
				entradas.set(i, e);
				break;
			}
		}
	}

	public void removerEntrada(EntradaBuffer e) {
		for (EntradaBuffer ent : entradas) {
			String rotAtual = ent.getRoteadorEntrada().getNome();

			if (rotAtual.equals(e.getRoteadorEntrada().getNome())) {
				entradas.remove(ent);
				break;
			}
		}
	}

	public EntradaBuffer obterEntrada(Roteador r) {
		if (r == null) return null;

		for (EntradaBuffer e : entradas) {
			String nomeRoteadorEntrada = e.getRoteadorEntrada().getNome();
			if (nomeRoteadorEntrada.equals(r.getNome())) return e;
		}

		return null;
	}

	public void alterarFlagConfirmacao(Roteador origem, Roteador v, boolean valor) {
		EntradaBuffer entrada = obterEntrada(origem);
		HashMap<Roteador, Boolean> flagsConfirmacao = entrada.getFlagsConfirmacao();

		for (Map.Entry<Roteador, Boolean> flags : flagsConfirmacao.entrySet()) {
			Roteador rotAtual = flags.getKey();

			if (rotAtual.getNome().equals(v.getNome())) {
				flagsConfirmacao.replace(rotAtual, valor);
				break;
			}
		}
	}

	public void alterarFlagTransmissao(Roteador origem, Roteador v, boolean valor) {
		EntradaBuffer entrada = obterEntrada(origem);
		HashMap<Roteador, Boolean> flagsTransmissao = entrada.getFlagsTransmissao();

		for (Map.Entry<Roteador, Boolean> flags : flagsTransmissao.entrySet()) {
			Roteador rotAtual = flags.getKey();

			if (rotAtual.getNome().equals(v.getNome())) {
				flagsTransmissao.replace(rotAtual, valor);
				break;
			}
		}
	}

	public void setRoteador(Roteador r) {
		this.r = r;
	}

	public Roteador getRoteador() {
		return r;
	}

	public void setEntradas(CopyOnWriteArrayList<EntradaBuffer> entradas) {
		this.entradas = entradas;
	}

	public CopyOnWriteArrayList<EntradaBuffer> getEntradas() {
		return entradas;
	}

	public void setListaRoteadores(CopyOnWriteArrayList<Roteador> listaRoteadores) {
		this.listaRoteadores = listaRoteadores;
	}

	public CopyOnWriteArrayList<Roteador> getListaRoteadores() {
		return listaRoteadores;
	}
}
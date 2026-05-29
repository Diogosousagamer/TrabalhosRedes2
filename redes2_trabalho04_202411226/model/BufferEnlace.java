/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/05/2026
* Ultima alteracao.: 29/05/2026
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

  /*
   * ***************************************************************
   * Metodo: BufferEnlace
   * Funcao: inicializa uma nova instancia da classe BufferEnlace
   * Parametros: Roteador r - roteador do buffer
                 CopyOnWriteArrayList<Roteador> listaRoteadores - lista de roteadores
                 presentes na sub rede
   * Retorno: nenhum
   ****************************************************************/

	public BufferEnlace(Roteador r, CopyOnWriteArrayList<Roteador> listaRoteadores) {
		entradas = new CopyOnWriteArrayList<>();
		this.r = r;
		this.listaRoteadores = listaRoteadores;
	}

  /*
   * ***************************************************************
   * Metodo: criarEntradasIniciais
   * Funcao: inicializa as entradas do buffer de enlace
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void criarEntradasIniciais() {
		// Inicio do bloco for
		for (Roteador r : listaRoteadores) {
			// Pula para outro roteador caso o roteador atual for correspondente
			// ao roteador do buffer
			if (r.getNome().equals(this.r.getNome())) continue;

			// Cria uma nova entrada para o roteador, carrega suas flags
			// e a adiciona na lista de entradas do buffer
			EntradaBuffer e = new EntradaBuffer(r, null);
			e.carregarFlags();
			entradas.add(e);
		} // Fim do bloco for
	}

  /*
   * ***************************************************************
   * Metodo: alterarEntrada
   * Funcao: modifica os valores de uma entrada no buffer de enlace
   * Parametros: EntradaBuffer e - entrada a ser modificada
   * Retorno: void
   ****************************************************************/

	public void alterarEntrada(EntradaBuffer e) {
		// Inicio do bloco for
		for (int i = 0; i < entradas.size(); i++) {
			// Obtem o roteador da entrada atual
      EntradaBuffer atual = entradas.get(i);
			Roteador rot = atual.getRoteadorEntrada();
			String rotAtual = rot.getNome();

      // Inicio do bloco if
			if (rotAtual.equals(e.getRoteadorEntrada().getNome())) {
				// Substitui a entrada atual pela modificada e interrompe o laco
				// se o roteador atual corresponder ao da entrada a ser alterada
				entradas.set(i, e);
				break;
			} // Fim do bloco if
		} // Fim do bloco for
	}

  /*
   * ***************************************************************
   * Metodo: removerEntrada
   * Funcao: remove uma entrada do buffer de enlace
   * Parametros: EntradaBuffer e - entrada a ser removida
   * Retorno: void
   ****************************************************************/

	public void removerEntrada(EntradaBuffer e) {
		// Inicio do bloco for
		for (EntradaBuffer ent : entradas) {
			// Obtem o roteador da entrada atual
			String rotAtual = ent.getRoteadorEntrada().getNome();
  
      // Inicio do bloco if
			if (rotAtual.equals(e.getRoteadorEntrada().getNome())) {
				// Remove a entrada atual e interrompe o laco se o roteador atual 
				// corresponder ao da entrada a ser alterada
				entradas.remove(ent);
				break;
			} // Fim do bloco if
		} // Fim do bloco for
	}

  /*
   * ***************************************************************
   * Metodo: obterEntrada
   * Funcao: obtem e retorna uma entrada do buffer de enlace
   * Parametros: Roteador r - roteador da entrada a ser buscada
   * Retorno: EntradaBuffer
   ****************************************************************/

	public EntradaBuffer obterEntrada(Roteador r) {
		// Retorna nulo se o roteador for nulo
		if (r == null) return null;

    // Inicio do bloco for
		for (EntradaBuffer e : entradas) {
			// Retorna a entrada atual se o roteador de entrada desta corresponder
			// ao roteador passado como parametro
			String nomeRoteadorEntrada = e.getRoteadorEntrada().getNome();
			if (nomeRoteadorEntrada.equals(r.getNome())) return e;
		} // Fim do bloco for

    // Retorna nulo caso nenhuma correspondencia for encontrada
		return null;
	}

  /*
   * ***************************************************************
   * Metodo: alterarFlagConfirmacao
   * Funcao: altera o valor de uma flag de confirmacao dentro do buffer
   * Parametros: Roteador origem - roteador da entrada a ser buscada
                 Roteador v - vizinho da flag a ser alterada
                 boolean valor - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void alterarFlagConfirmacao(Roteador origem, Roteador v, boolean valor) {
		// Obtem a entrada do buffer correspondente ao roteador de origem
		EntradaBuffer entrada = obterEntrada(origem);

    // Inicio do bloco if
		if (entrada == null) {
			// Cria uma nova entrada se ela for nula
			entrada = new EntradaBuffer(origem, null);
			entrada.carregarFlags();
			entradas.add(entrada);
		} // Fim do bloco for

    // Obtem as flags de confirmacao da entrada
		HashMap<Roteador, Boolean> flagsConfirmacao = entrada.getFlagsConfirmacao();

    // Inicio do bloco for
		for (Map.Entry<Roteador, Boolean> flags : flagsConfirmacao.entrySet()) {
			// Obtem o roteador da flag atual
			Roteador rotAtual = flags.getKey();

      // Inicio do bloco if
			if (rotAtual.getNome().equals(v.getNome())) {
				// Altera o valor da flag atual se o roteador da flag corresponder ao roteador buscado
				flagsConfirmacao.replace(rotAtual, valor);

				// Interrompe o laco
				break;
			} // Fim do bloco if
		} // Fim do bloco for
	}

  /*
   * ***************************************************************
   * Metodo: alterarFlagTransmissao
   * Funcao: altera o valor de uma flag de transmissao dentro do buffer
   * Parametros: Roteador origem - roteador da entrada a ser buscada
                 Roteador v - vizinho da flag a ser alterada
                 boolean valor - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void alterarFlagTransmissao(Roteador origem, Roteador v, boolean valor) {
		// Obtem a entrada do buffer correspondente ao roteador de origem
		EntradaBuffer entrada = obterEntrada(origem);

    // Inicio do bloco if
		if (entrada == null) {
			// Cria uma nova entrada se ela for nula
			entrada = new EntradaBuffer(origem, null);
			entrada.carregarFlags();
			entradas.add(entrada);
		} // Fim do bloco for

    // Obtem as flags de transmissao da entrada
		HashMap<Roteador, Boolean> flagsTransmissao = entrada.getFlagsTransmissao();

    // Inicio do bloco for
		for (Map.Entry<Roteador, Boolean> flags : flagsTransmissao.entrySet()) {
			// Obtem o roteador da flag atual
			Roteador rotAtual = flags.getKey();

      // Inicio do bloco if
			if (rotAtual.getNome().equals(v.getNome())) {
				// Altera o valor da flag atual se o roteador da flag corresponder ao roteador buscado
				flagsTransmissao.replace(rotAtual, valor);

				// Interrompe o laco
				break;
			} // Fim do bloco if
		} // Fim do bloco for
	}

  /*
   * ***************************************************************
   * Metodo: setRoteador
   * Funcao: define o roteador do buffer
   * Parametros: Roteador r - roteador a ser definido
   * Retorno: void
   ****************************************************************/

	public void setRoteador(Roteador r) {
		this.r = r;
	}

  /*
   * ***************************************************************
   * Metodo: getRoteador
   * Funcao: retorna o roteador do buffer
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getRoteador() {
		return r;
	}
 
  /*
   * ***************************************************************
   * Metodo: setEntradas
   * Funcao: define o conjunto de entradas do buffer
   * Parametros: CopyOnWriteArrayList<EntradaBuffer> entradas - lista a ser definida
   * Retorno: void
   ****************************************************************/

	public void setEntradas(CopyOnWriteArrayList<EntradaBuffer> entradas) {
		this.entradas = entradas;
	}

  /*
   * ***************************************************************
   * Metodo: getEntradas
   * Funcao: retorna o conjunto de entradas do buffer
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: CopyOnWriteArrayList<EntradaBuffer>
   ****************************************************************/

	public CopyOnWriteArrayList<EntradaBuffer> getEntradas() {
		return entradas;
	}

  /*
   * ***************************************************************
   * Metodo: setListaRoteadores
   * Funcao: define a lista de roteadores presentes na sub rede
   * Parametros: CopyOnWriteArrayList<EntradaBuffer> listaRoteadores - lista a ser definida
   * Retorno: void
   ****************************************************************/

	public void setListaRoteadores(CopyOnWriteArrayList<Roteador> listaRoteadores) {
		this.listaRoteadores = listaRoteadores;
	}

  /*
   * ***************************************************************
   * Metodo: getListaRoteadores
   * Funcao: retorna a lista de roteadores presentes na sub rede
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: CopyOnWriteArrayList<Roteador>
   ****************************************************************/

	public CopyOnWriteArrayList<Roteador> getListaRoteadores() {
		return listaRoteadores;
	}
}
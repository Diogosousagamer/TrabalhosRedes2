/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/05/2026
* Ultima alteracao.: 29/05/2026
* Nome.............: EntradaBuffer
* Funcao...........: Classe que gerencia as entradas dos buffers contendo
                     os pacotes de estado de enlace.
                     
*************************************************************** */

package model;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EntradaBuffer {
	// Variaveis e instancias
	private Roteador roteadorEntrada;
	private PacoteEstadoEnlace pacoteAtual;
	private HashMap<Roteador, Boolean> flagsTransmissao;
	private HashMap<Roteador, Boolean> flagsConfirmacao;

  /*
   * ***************************************************************
   * Metodo: EntradaBuffer
   * Funcao: inicializa uma nova instancia da classe EntradaBuffer
   * Parametros: Roteador roteadorEntrada - roteador de entrada do buffer
                 PacoteEstadoEnlace atual - pacote de estado de enlace recebido
   * Retorno: nenhum
   ****************************************************************/

	public EntradaBuffer(Roteador roteadorEntrada, PacoteEstadoEnlace pacoteAtual) {
		this.roteadorEntrada = roteadorEntrada;
		this.pacoteAtual = pacoteAtual;
		flagsTransmissao = new HashMap<>();
		flagsConfirmacao = new HashMap<>();
	}

  /*
   * ***************************************************************
   * Metodo: carregarFlags
   * Funcao: carrega as flags de transmissao/confirmacao do buffer
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void carregarFlags() {
		// Obtem os vizinhos do roteador de entrada
		CopyOnWriteArrayList<Roteador> vizinhos = roteadorEntrada.getVizinhos();

    // Inicio do bloco for
		for (Roteador v : vizinhos) {
			// Cria uma flag de transmissao/confirmacao para cada vizinho
			flagsTransmissao.put(v, false);
			flagsConfirmacao.put(v, false);
		} // Fim do bloco for
	}

  /*
   * ***************************************************************
   * Metodo: setRoteadorEntrada
   * Funcao: define o roteador da entrada do buffer
   * Parametros: Roteador r - roteador a ser definido
   * Retorno: void
   ****************************************************************/

	public void setRoteadorEntrada(Roteador r) {
		this.roteadorEntrada = r;
	}

  /*
   * ***************************************************************
   * Metodo: getRoteadorEntrada
   * Funcao: retorna o roteador da entrada do buffer
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getRoteadorEntrada() {
		return roteadorEntrada;
	}

  /*
   * ***************************************************************
   * Metodo: setPacoteAtual
   * Funcao: define o pacote de estado de enlace mantido pela entrada
   * Parametros: PacoteEstadoEnlace p - pacote a ser definido
   * Retorno: void
   ****************************************************************/

	public void setPacoteAtual(PacoteEstadoEnlace p) {
		this.pacoteAtual = p;
	}

  /*
   * ***************************************************************
   * Metodo: getPacoteAtual
   * Funcao: retorna o pacote de estado de enlace mantido pela entrada
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: PacoteEstadoEnlace
   ****************************************************************/

	public PacoteEstadoEnlace getPacoteAtual() {
		return pacoteAtual;
	}

  /*
   * ***************************************************************
   * Metodo: getFlagsTransmissao
   * Funcao: define as flags de transmissao da entrada
   * Parametros: HashMap<Roteador, Boolean> ft - lista a ser definida
   * Retorno: void
   ****************************************************************/

	public void setFlagsTransmissao(HashMap<Roteador, Boolean> ft) {
		this.flagsTransmissao = ft;
	}

  /*
   * ***************************************************************
   * Metodo: getFlagsTransmissao
   * Funcao: retorna as flags de transmissao da entrada
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: HashMap<Roteador, Boolean>
   ****************************************************************/

	public HashMap<Roteador, Boolean> getFlagsTransmissao() {
		return flagsTransmissao;
	}

  /*
   * ***************************************************************
   * Metodo: getFlagsConfirmacao
   * Funcao: define as flags de transmissao da entrada
   * Parametros: HashMap<Roteador, Boolean> ft - lista a ser definida
   * Retorno: void
   ****************************************************************/

	public void setFlagsConfirmacao(HashMap<Roteador, Boolean> fc) {
		this.flagsConfirmacao = fc;
	}

  /*
   * ***************************************************************
   * Metodo: getFlagsConfirmacao
   * Funcao: retorna as flags de confirmacao da entrada
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: HashMap<Roteador, Boolean>
   ****************************************************************/

	public HashMap<Roteador, Boolean> getFlagsConfirmacao() {
		return flagsConfirmacao;
	}
}
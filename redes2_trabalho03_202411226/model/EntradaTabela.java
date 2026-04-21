/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 18/04/2026
* Ultima alteracao.: 21/04/2026
* Nome.............: EntradaTabela
* Funcao...........: Classe que gerencia as operacoes de cada entrada das tabelas de roteamento.
                     
*************************************************************** */

package model;

public class EntradaTabela {
	// Variaveis e instancias
	private String destino;
	private String linhaSaida;
	private String retardo;

  /*
   * ***************************************************************
   * Metodo: EntradaTabela
   * Funcao: inicializa uma nova instancia da classe EntradaTabela
   * Parametros: String destino - linha de destino
                 String linhaSaida - linha de saida
                 String retardo - retardo do caminho percorrido ate o destino
   * Retorno: nenhum
   ****************************************************************/

	public EntradaTabela(String destino, String linhaSaida, String retardo) {
		this.destino = destino;
		this.linhaSaida = linhaSaida;
		this.retardo = retardo;
	}

  /*
   * ***************************************************************
   * Metodo: setDestino
   * Funcao: define a linha de destino
   * Parametros: String destino - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setDestino(String destino) {
		this.destino = destino;
	}

  /*
   * ***************************************************************
   * Metodo: getDestino
   * Funcao: retorna a linha de destino
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

	public String getDestino() {
		return destino;
	}

  /*
   * ***************************************************************
   * Metodo: setLinhaSaida
   * Funcao: define a linha de saida
   * Parametros: String linhaSaida - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setLinhaSaida(String linhaSaida) {
		this.linhaSaida = linhaSaida;
	}

  /*
   * ***************************************************************
   * Metodo: getLinhaSaida
   * Funcao: retorna a linha de saida
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

	public String getLinhaSaida() {
		return linhaSaida;
	}

  /*
   * ***************************************************************
   * Metodo: setRetardo
   * Funcao: define o retardo do caminho a ser percorrido para chegar
             ate o destino
   * Parametros: String retardo - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setRetardo(String retardo) {
		this.retardo = retardo;
	}
 
  /*
   * ***************************************************************
   * Metodo: getRetardo
   * Funcao: retorna o retardo do caminho a ser percorrido para chegar
             ate o destino
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

	public String getRetardo() {
		return retardo;
	}
}
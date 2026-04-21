/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 16/04/2026
* Ultima alteracao.: 21/04/2026
* Nome.............: Aresta
* Funcao...........: Classe que gerencia as operacoes de cada aresta.
                     
*************************************************************** */

package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Aresta {
	// Variaveis e instancias
	private Line linha;
	private Roteador r1;
	private Roteador r2;
	private long tempoIda;
	private long tempoVolta;

	public Aresta(Line linha, Roteador r1, Roteador r2, long tempoIda, long tempoVolta) {
		this.linha = linha;
		this.r1 = r1;
		this.r2 = r2;
		this.tempoIda = tempoIda;
		this.tempoVolta = tempoVolta;
	}

	/*
   * ***************************************************************
   * Metodo: marcarPermanente
   * Funcao: marca a linha da aresta com a cor verde
             para sinalizar que ela faz parte do caminho
             a ser percorrido pelo pacote
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void marcarPermanente() {
		linha.setStroke(Color.web("#9da1ad"));
	}

	/*
   * ***************************************************************
   * Metodo: marcarVisitando
   * Funcao: marca a linha da aresta com a cor vermelha para sinalizar
             que ela esta sendo visitada
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  public void marcarVisitando() {
    linha.setStroke(Color.web("#3d7996"));
  }

  /*
   * ***************************************************************
   * Metodo: resetarLinha
   * Funcao: reseta a cor original da linha
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void resetarLinha() {
		linha.setStroke(Color.WHITE);
	}

	/*
   * ***************************************************************
   * Metodo: setLinha
   * Funcao: define a linha da aresta
   * Parametros: Line linha - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setLinha(Line linha) {
		this.linha = linha;
	}

  /*
   * ***************************************************************
   * Metodo: getLinha
   * Funcao: retorna a linha da aresta
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Line
   ****************************************************************/

	public Line getLinha() {
		return linha;
	}

  /*
   * ***************************************************************
   * Metodo: setR1
   * Funcao: define o primeiro roteador
   * Parametros: Roteador r1 - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setR1(Roteador r1) {
		this.r1 = r1;
	}

  /*
   * ***************************************************************
   * Metodo: getR1
   * Funcao: retorna o primeiro roteador
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getR1() {
		return r1;
	}

  /*
   * ***************************************************************
   * Metodo: setR2
   * Funcao: define o segundo roteador
   * Parametros: Roteador r2 - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setR2(Roteador r2) {
		this.r2 = r2;
	}

  /*
   * ***************************************************************
   * Metodo: getR2
   * Funcao: retorna o segundo roteador
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getR2() {
		return r2;
	}

  /*
   * ***************************************************************
   * Metodo: setIda
   * Funcao: define o retardo de ida da aresta
   * Parametros: long ida - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setIda(long ida) {
		this.tempoIda = ida;
	}

  /*
   * ***************************************************************
   * Metodo: getIda
   * Funcao: retorna o retardo de ida da aresta
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: long
   ****************************************************************/

	public long getIda() {
		return tempoIda;
	}

  /*
   * ***************************************************************
   * Metodo: setVolta
   * Funcao: define o retardo de volta da aresta
   * Parametros: long volta - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setVolta(long volta) {
		this.tempoVolta = volta;
	}

  /*
   * ***************************************************************
   * Metodo: getVolta
   * Funcao: retorna o retardo de volta da aresta
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: long
   ****************************************************************/

	public long getVolta() {
		return tempoVolta;
	}
}
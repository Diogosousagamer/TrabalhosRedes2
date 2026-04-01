/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 29/03/2026
* Ultima alteracao.: 31/03/2026
* Nome.............: Aresta
* Funcao...........: Este trabalho tem como objetivo simular o roteamento de pacotes dentro da camada de rede 
                     atraves do algoritmo do caminho mais curto.
                     
*************************************************************** */

package model;

import javafx.scene.shape.Line;
import javafx.scene.paint.Color;

public class Aresta {
	// Variaveis e instancias
	private Line linha;
	private Roteador r1;
	private Roteador r2;
	private int peso;

  /*
   * ***************************************************************
   * Metodo: Aresta
   * Funcao: inicializa uma nova instancia da classe Aresta
   * Parametros: Linha linha - linha que representa a aresta na interface
                 Roteador r1 - roteador de origem
                 Roteador r2 - roteador de destino
                 int peso - peso da aresta
   * Retorno: nenhum
   ****************************************************************/

	public Aresta(Line linha, Roteador r1, Roteador r2, int peso) {
		this.linha = linha;
		this.r1 = r1;
		this.r2 = r2;
		this.peso = peso;
	}

  /*
   * ***************************************************************
   * Metodo: marcarPermanente
   * Funcao: marca a linha da aresta para destacar o caminho a ser
             percorrido pelo pacote
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void marcarPermanente() {
		// A linha tera a cor verde
		linha.setStroke(Color.web("#1fdb18"));
	}

  public void marcarIntermediario() {
    linha.setStroke(Color.web("#f5d11d"));
  }

  public void marcarVisitando() {
    linha.setStroke(Color.web("#d60b18"));
  }

  /*
   * ***************************************************************
   * Metodo: resetarLinha
   * Funcao: reseta a cor original da linha
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void resetarLinha() {
		// A linha volta a ser branca
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
   * Metodo: setPeso
   * Funcao: define o peso da aresta
   * Parametros: int peso - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setPeso(int peso) {
		this.peso = peso;
	}

  /*
   * ***************************************************************
   * Metodo: getPeso
   * Funcao: retorna o peso da aresta
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: int
   ****************************************************************/

	public int getPeso() {
		return peso;
	}
}
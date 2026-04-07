/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 29/03/2026
* Ultima alteracao.: 07/04/2026
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
  private boolean intermediario;
  private boolean permanente;

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
   * Funcao: marca a linha da aresta com a cor verde
             para sinalizar que ela faz parte do caminho
             a ser percorrido pelo pacote
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void marcarPermanente() {
		linha.setStroke(Color.web("#1fdb18"));
	}

  /*
   * ***************************************************************
   * Metodo: setIntermediario
   * Funcao: define a aresta como permanente (parte do caminho final)
   * Parametros: boolean p - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setPermanente(boolean p) {
    this.permanente = p;
  }

  /*
   * ***************************************************************
   * Metodo: isPermanente
   * Funcao: retorna se a aresta eh permanente ou nao
   * Parametros: nenhum valor foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  public boolean isPermanente() {
    return permanente;
  }

  /*
   * ***************************************************************
   * Metodo: marcarIntermediario
   * Funcao: altera a cor da linha da aresta para amarelo 
             para classifica-la como intermediaria (considerada para o caminho final)
   * Parametros: nenhum valor foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  public void marcarIntermediario() {
    linha.setStroke(Color.web("#f5d11d"));
  }

  /*
   * ***************************************************************
   * Metodo: setIntermediario
   * Funcao: define a aresta como intermediaria (considerada para o caminho final)
   * Parametros: boolean i - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setIntermediario(boolean i) {
    this.intermediario = i;
  }

  /*
   * ***************************************************************
   * Metodo: isIntermediario
   * Funcao: retorna se a aresta eh intermediaria ou nao
   * Parametros: nenhum valor foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  public boolean isIntermediario() {
    return intermediario;
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
		linha.setStroke((intermediario) ? Color.web("#f5d11d") : Color.WHITE);
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
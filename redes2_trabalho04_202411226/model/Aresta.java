/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 02/05/2026
* Ultima alteracao.: 27/05/2026
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
	private long ida;
  private long volta;

  /*
   * ***************************************************************
   * Metodo: Aresta
   * Funcao: inicializa uma nova instancia da classe Aresta
   * Parametros: Line linha - linha da aresta
                 Roteador r1 - roteador 1 da aresta
                 Roteador r2 - roteador 2 da aresta
   * Retorno: void
   ****************************************************************/

	public Aresta(Line linha, Roteador r1, Roteador r2) {
		this.linha = linha;
		this.r1 = r1;
		this.r2 = r2;
	}

	/*
   * ***************************************************************
   * Metodo: marcarParteCaminho
   * Funcao: marca a linha da aresta com a cor verde
             para sinalizar que ela faz parte do caminho
             a ser percorrido pelo pacote
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void marcarParteCaminho() {
		linha.setStroke(Color.web("#9da1ad"));
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
   * Metodo: ativarAresta
   * Funcao: ativa a linha da aresta e reseta a opacidade
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  public void ativarAresta() {
    linha.setDisable(false);
    linha.setOpacity(1);
  }

  /*
   * ***************************************************************
   * Metodo: desativarAresta
   * Funcao: desativa a linha da aresta e torna-a opaca
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  public void desativarAresta() {
    linha.setDisable(true);
    linha.setOpacity(0.3);
  }

  /*
   * ***************************************************************
   * Metodo: estaDesativada
   * Funcao: verifica se a linha da aresta esta desativada ou nao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  public boolean estaDesativada() {
    return linha.isDisable();
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
   * Funcao: define o tempo de ida da aresta
   * Parametros: long ida - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setIda(long ida) {
		this.ida = ida;
	}

  /*
   * ***************************************************************
   * Metodo: getIda
   * Funcao: retorna o tempo de ida da aresta
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: long
   ****************************************************************/

	public long getIda() {
		return ida;
	}

  /*
   * ***************************************************************
   * Metodo: setVolta
   * Funcao: define o tempo de volta da aresta
   * Parametros: long volta - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setVolta(long volta) {
    this.volta = volta;
  }

  /*
   * ***************************************************************
   * Metodo: getVolta
   * Funcao: retorna o tempo de volta da aresta
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: long
   ****************************************************************/

  public long getVolta() {
    return volta;
  }
}
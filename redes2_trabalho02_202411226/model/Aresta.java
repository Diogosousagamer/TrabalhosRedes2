/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 29/03/2026
* Ultima alteracao.: 30/03/2026
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

	public void marcarLinha() {
		linha.setStroke(Color.web("#1fdb18"));
	}

	public void resetarLinha() {
		linha.setStroke(Color.WHITE);
	}

	public Line getLinha() {
		return linha;
	}

	public Roteador getR1() {
		return r1;
	}

	public Roteador getR2() {
		return r2;
	}

	public int getPeso() {
		return peso;
	}
}
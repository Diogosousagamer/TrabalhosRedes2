/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 16/04/2026
* Ultima alteracao.: 18/04/2026
* Nome.............: Aresta
* Funcao...........: Classe que gerencia as operacoes de cada aresta.
                     
*************************************************************** */

package model;

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

	public void setLinha(Line linha) {
		this.linha = linha;
	}

	public Line getLinha() {
		return linha;
	}

	public void setR1(Roteador r1) {
		this.r1 = r1;
	}

	public Roteador getR1() {
		return r1;
	}

	public void setR2(Roteador r2) {
		this.r2 = r2;
	}

	public Roteador getR2() {
		return r2;
	}

	public void setIda(long ida) {
		this.tempoIda = ida;
	}

	public long getIda() {
		return tempoIda;
	}

	public void setVolta(long volta) {
		this.tempoVolta = volta;
	}

	public long getVolta() {
		return tempoVolta;
	}
}
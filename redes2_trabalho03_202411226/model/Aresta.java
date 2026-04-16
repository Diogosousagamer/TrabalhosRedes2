/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 16/04/2026
* Ultima alteracao.: 16/04/2026
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
}
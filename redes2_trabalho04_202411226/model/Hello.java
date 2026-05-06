/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 07/05/2026
* Ultima alteracao.: 07/05/2026
* Nome.............: Hello
* Funcao...........: Thread que gerencia os pacotes Hello, usados para conhecer 
                     os vizinhos de cada roteador.
                     
*************************************************************** */

package model;

public class Hello {
	private ImageView hello;
	private Roteador origem;
	private Roteador destino;
	private double posX;
	private double posY;

	public Hello(ImageView hello, Roteador origem, Roteador destino) {
		this.hello = hello;
		this.origem = origem;
		this.destino = destino;		
	}
}
/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/05/2026
* Ultima alteracao.: 06/05/2026
* Nome.............: PacoteEstadoEnlace
* Funcao...........: Thread que gerencia os pacotes de estado de enlace.
                     
*************************************************************** */

package model;

import java.lang.Thread;
import javafx.scene.image.ImageView;

public class PacoteEstadoEnlace extends Thread {
	private ImageView link;
	private int numeroSequencia;
	private int idade;
	private Roteador origem;
	private Roteador destino;
	private double posX;
	private double posY;

	public PacoteEstadoEnlace(ImageView link, int numeroSequencia, int idade, Roteador origem, Roteador destino) {
		this.link = link;
		this.numeroSequencia = numeroSequencia;
		this.idade = idade;
		this.origem = origem;
		this.destino = destino;
	}
}
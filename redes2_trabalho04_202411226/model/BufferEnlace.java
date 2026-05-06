/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 07/05/2026
* Ultima alteracao.: 07/05/2026
* Nome.............: BufferEnlace
* Funcao...........: Classe que gerencia as operacoes de cada buffer contendo os pacotes
                     de estado de enlace.
                     
*************************************************************** */

package model;

import java.util.concurrent.CopyOnWriteArrayList;

public class BufferEnlace {
	private CopyOnWriteArrayList<EntradaBuffer> entradas;
	private CopyOnWriteArrayList<Roteador> listaRoteadores;

	public BufferEnlace(CopyOnWriteArrayList<Roteador> listaRoteadores) {
		entradas = new CopyOnWriteArrayList<>();
		this.listaRoteadores = listaRoteadores;
	}
}
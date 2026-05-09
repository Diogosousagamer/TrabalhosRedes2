/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 07/05/2026
* Ultima alteracao.: 08/05/2026
* Nome.............: BufferEnlace
* Funcao...........: Classe que gerencia as operacoes de cada buffer contendo os pacotes
                     de estado de enlace.
                     
*************************************************************** */

package model;

import java.util.concurrent.CopyOnWriteArrayList;

public class BufferEnlace {
	private Roteador r;
	private CopyOnWriteArrayList<EntradaBuffer> entradas;
	private CopyOnWriteArrayList<Roteador> listaRoteadores;

	public BufferEnlace(Roteador r, CopyOnWriteArrayList<Roteador> listaRoteadores) {
		entradas = new CopyOnWriteArrayList<>();
		this.r = r;
		this.listaRoteadores = listaRoteadores;
	}

	public void criarEntradasIniciais() {
		for (Roteador r : listaRoteadores) {
			if (r.getNome().equals(r.getNome())) continue;
			int tamanhoVizinhos = this.r.getVizinhos().size();
			entradas.add(new EntradaBuffer(r, null));
		}
	}
}
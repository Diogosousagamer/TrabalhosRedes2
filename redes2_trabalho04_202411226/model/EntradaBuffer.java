/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 07/05/2026
* Ultima alteracao.: 07/05/2026
* Nome.............: EntradaBuffer
* Funcao...........: Classe que gerencia as entradas dos buffers contendo
                     os pacotes de estado de enlace.
                     
*************************************************************** */

package model;

public class EntradaBuffer {
	private Roteador roteadorEntrada;
	private PacoteEstadoEnlace pacoteAtual;

	public EntradaBuffer(Roteador roteadorEntrada, PacoteEstadoEnlace pacoteAtual) {
		this.roteadorEntrada = roteadorEntrada;
		this.pacoteAtual = pacoteAtual;
	}
}
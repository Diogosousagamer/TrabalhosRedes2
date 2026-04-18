/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 18/04/2026
* Ultima alteracao.: 18/04/2026
* Nome.............: EntradaTabela
* Funcao...........: Classe que gerencia as operacoes de cada entrada das tabelas de roteamento.
                     
*************************************************************** */

package model;

public class EntradaTabela {
	private String destino;
	private String linhaSaida;
	private long retardo;

	public EntradaTabela(String destino, String linhaSaida, long retardo) {
		this.destino = destino;
		this.linhaSaida = linhaSaida;
		this.retardo = retardo;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public String getDestino() {
		return destino;
	}

	public void setLinhaSaida(String linhaSaida) {
		this.linhaSaida = linhaSaida;
	}

	public String getLinhaSaida() {
		return linhaSaida;
	}

	public void setRetardo(long retardo) {
		this.retardo = retardo;
	}

	public long getRetardo() {
		return retardo;
	}
}
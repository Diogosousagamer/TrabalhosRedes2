/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/05/2026
* Ultima alteracao.: 25/05/2026
* Nome.............: EntradaBuffer
* Funcao...........: Classe que gerencia as entradas dos buffers contendo
                     os pacotes de estado de enlace.
                     
*************************************************************** */

package model;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EntradaBuffer {
	private Roteador roteadorEntrada;
	private PacoteEstadoEnlace pacoteAtual;
	private HashMap<Roteador, Boolean> flagsTransmissao;
	private HashMap<Roteador, Boolean> flagsConfirmacao;

	public EntradaBuffer(Roteador roteadorEntrada, PacoteEstadoEnlace pacoteAtual) {
		this.roteadorEntrada = roteadorEntrada;
		this.pacoteAtual = pacoteAtual;
		flagsTransmissao = new HashMap<>();
		flagsConfirmacao = new HashMap<>();
	}

	public void carregarFlags() {
		CopyOnWriteArrayList<Roteador> vizinhos = roteadorEntrada.getVizinhos();

		for (Roteador v : vizinhos) {
			flagsTransmissao.put(v, false);
			flagsConfirmacao.put(v, false);
		}
	}

	public void setRoteadorEntrada(Roteador r) {
		this.roteadorEntrada = r;
	}

	public Roteador getRoteadorEntrada() {
		return roteadorEntrada;
	}

	public void setPacoteAtual(PacoteEstadoEnlace p) {
		this.pacoteAtual = p;
	}

	public PacoteEstadoEnlace getPacoteAtual() {
		return pacoteAtual;
	}

	public HashMap<Roteador, Boolean> getFlagsTransmissao() {
		return flagsTransmissao;
	}

	public HashMap<Roteador, Boolean> getFlagsConfirmacao() {
		return flagsConfirmacao;
	}
}
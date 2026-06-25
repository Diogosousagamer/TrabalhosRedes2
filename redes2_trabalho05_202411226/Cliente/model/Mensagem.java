/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 21/06/2026
* Ultima alteracao.: 24/06/2026
* Nome.............: Mensagem
* Funcao...........: Classe que controla as operacoes das mensagens enviadas no chat do aplicativo.
                     
*************************************************************** */

package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mensagem {
	private String texto;
	private Usuario autor;
	private LocalDateTime tempoEnvio;

	public Mensagem(String texto, Usuario autor) {
		this.texto = texto;
		this.autor = autor;
		tempoEnvio = LocalDateTime.now();
	}

	public Mensagem(String texto, Usuario autor, LocalDateTime tempoEnvio) {
		this.texto = texto;
		this.autor = autor;
		this.tempoEnvio = tempoEnvio;
	}

	public void setTexto(String t) {
		this.texto = t;
	}

	public String getTexto() {
		return texto;
	}

	public void setAutor(Usuario autor) {
		this.autor = autor;
	}

	public Usuario getAutor() {
		return autor;
	}

	public void setTempoEnvio(LocalDateTime tempoEnvio) {
		this.tempoEnvio = tempoEnvio;
	}

	public LocalDateTime getTempoEnvio() {
		return tempoEnvio;
	}

	public String formatarTempoEnvio() {
		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		String dataEnvio = tempoEnvio.format(formatador);

		return dataEnvio;
	}
}
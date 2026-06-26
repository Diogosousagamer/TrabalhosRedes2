/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 21/06/2026
* Ultima alteracao.: 26/06/2026
* Nome.............: Mensagem
* Funcao...........: Classe que controla as operacoes das mensagens enviadas no chat do aplicativo.
                     
*************************************************************** */

package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mensagem {
	// Variaveis e instancias
	private String texto;
	private String autor;
	private LocalDateTime tempoEnvio;

	public Mensagem(String texto, String autor) {
		this.texto = texto;
		this.autor = autor;
		tempoEnvio = LocalDateTime.now();
	}

	public Mensagem(String texto, String autor, LocalDateTime tempoEnvio) {
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

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getAutor() {
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

	public String formatarHora() {
		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm");
		String horaEnvio = tempoEnvio.format(formatador);

		return horaEnvio;
	}
}
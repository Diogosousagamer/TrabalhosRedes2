/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 17/06/2026
* Ultima alteracao.: 26/06/2026
* Nome.............: APDU
* Funcao...........: Classe que gerencia as operacoes das APDUs.
                     
*************************************************************** */

package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class APDU {
	private String tipo;
	private String usuario;
	private String grupo;
	private String mensagem;
	private LocalDateTime tempoEnvio;

	private static final String DELIMITADOR = "*";
	private static final String ESCAPE = "\\";

	public APDU(String tipo, String usuario, String grupo) {
		this.tipo = tipo;
		this.usuario = usuario;
		this.grupo = grupo;
		this.mensagem = null;
		this.tempoEnvio = null;
	}

	public APDU(String tipo, String usuario, String grupo, String mensagem, LocalDateTime tempoEnvio) {
		this.tipo = tipo;
		this.usuario = usuario;
		this.grupo = grupo;
		this.mensagem = mensagem;
		this.tempoEnvio = tempoEnvio;
	}

	public String enviarMensagem() {
		return (this.mensagem == null && this.tempoEnvio == null) ? codificarMensagem(this.tipo, this.usuario, this.grupo)
		       : codificarMensagem(this.tipo, this.usuario, this.grupo, this.mensagem, formatarTempoEnvio());
	}

	public String codificarMensagem(String tipo, String usuario, String grupo) {
		tipo = tipo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		usuario = usuario.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		grupo = grupo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);

		return tipo + DELIMITADOR + usuario + DELIMITADOR + grupo;
	}

	public String codificarMensagem(String tipo, String usuario, String grupo, String mensagem, String tempoEnvio) {
		tipo = tipo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		usuario = usuario.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		grupo = grupo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		mensagem = mensagem.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		tempoEnvio = tempoEnvio.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);

		return tipo + DELIMITADOR + usuario + DELIMITADOR + grupo + DELIMITADOR + mensagem + DELIMITADOR + tempoEnvio;
	}

	public static APDU decodificarMensagem(String msg) {
		msg = msg.trim().replace("\0", "");
		String[] partes = msg.split("\\*", -1);

		String tipoLocal = partes[0].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		String usuarioLocal = partes[1].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		String grupoLocal = partes[2].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		String msgLocal = "";
		LocalDateTime envioLocal = null;

		if (partes.length > 3 && !partes[3].isEmpty() && !partes[4].isEmpty()) {
			msgLocal = partes[3].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
			String tempoStr = partes[4].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);

			if (tempoStr.length() > 16) {
				tempoStr.substring(0, 16);
			}

			DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			envioLocal = LocalDateTime.parse(tempoStr, formato);
		}

		return (msgLocal == null) ? new APDU(tipoLocal, usuarioLocal, grupoLocal) 
		       : new APDU(tipoLocal, usuarioLocal, grupoLocal, msgLocal, envioLocal);
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setTempoEnvio(LocalDateTime tempoEnvio) {
		this.tempoEnvio = tempoEnvio;
	}

	public LocalDateTime getTempoEnvio() {
		return tempoEnvio;
	}

	private String formatarTempoEnvio() {
		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		String dataEnvio = tempoEnvio.format(formatador);

		return dataEnvio;
	}
}
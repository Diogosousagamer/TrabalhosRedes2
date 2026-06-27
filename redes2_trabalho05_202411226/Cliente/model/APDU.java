/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 17/06/2026
* Ultima alteracao.: 27/06/2026
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
	private String status;
	private LocalDateTime tempoEnvio;

	private static final String DELIMITADOR = "*";
	private static final String ESCAPE = "\\";

	public APDU(String tipo, String usuario, String grupo) {
		this.tipo = tipo;
		this.usuario = usuario;
		this.grupo = grupo;
		this.mensagem = null;
		this.tempoEnvio = null;
		this.status = null;
	}

	public APDU(String tipo, String usuario, String grupo, String mensagem, LocalDateTime tempoEnvio) {
		this.tipo = tipo;
		this.usuario = usuario;
		this.grupo = grupo;
		this.mensagem = mensagem;
		this.tempoEnvio = tempoEnvio;
		this.status = null;
	}

	public APDU(String tipo, String usuario, String grupo, String mensagem, LocalDateTime tempoEnvio, String status) {
		this.tipo = tipo;
		this.usuario = usuario;
		this.grupo = grupo;
		this.mensagem = mensagem;
		this.tempoEnvio = tempoEnvio;
		this.status = status;
	}

	public String enviarMensagem() {
		return (this.mensagem == null && this.tempoEnvio == null) ? codificarMensagem(this.tipo, this.usuario, this.grupo)
		       : ((this.status == null) ? codificarMensagem(this.tipo, this.usuario, this.grupo, this.mensagem, formatarTempoEnvio()) : 
		     		 codificarMensagem(this.tipo, this.usuario, this.grupo, this.mensagem, formatarTempoEnvio(), this.status));
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

	public String codificarMensagem(String tipo, String usuario, String grupo, String mensagem, String tempoEnvio, String status) {
		tipo = tipo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		usuario = usuario.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		grupo = grupo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		mensagem = mensagem.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		tempoEnvio = tempoEnvio.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		status = status.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);

		return tipo + DELIMITADOR + usuario + DELIMITADOR + grupo + DELIMITADOR + mensagem + DELIMITADOR + tempoEnvio + DELIMITADOR + status;
	}

	public static APDU decodificarMensagem(String msg) {
		msg = msg.trim().replace("\0", "");
		String[] partes = msg.split("\\*", -1);

		String tipoLocal = partes[0].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		String usuarioLocal = partes[1].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		String grupoLocal = partes[2].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		String msgLocal = null;
		LocalDateTime envioLocal = null;
		String statusLocal = null;

		if (partes.length >= 5 && !partes[3].isEmpty() && !partes[4].isEmpty()) {
			msgLocal = partes[3].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
			String tempoStr = partes[4].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);

			if (tempoStr.length() > 19) {
				tempoStr = tempoStr.substring(0, 19);
			}

			DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
			envioLocal = LocalDateTime.parse(tempoStr, formato);

			if (partes.length >= 6 && !partes[5].isEmpty()) {
				statusLocal = partes[5].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
			}
		}

		return (msgLocal == null && envioLocal == null) ? new APDU(tipoLocal, usuarioLocal, grupoLocal) 
		       : ((statusLocal == null) ? new APDU(tipoLocal, usuarioLocal, grupoLocal, msgLocal, envioLocal) : 
		                                  new APDU(tipoLocal, usuarioLocal, grupoLocal, msgLocal, envioLocal, statusLocal));
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

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public String formatarTempoEnvio() {
		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		String dataEnvio = tempoEnvio.format(formatador);

		return dataEnvio;
	}
}
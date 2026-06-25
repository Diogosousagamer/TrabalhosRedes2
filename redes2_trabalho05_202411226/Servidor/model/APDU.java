/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 17/06/2026
* Ultima alteracao.: 25/06/2026
* Nome.............: APDU
* Funcao...........: Classe que gerencia as operacoes das APDUs.
                     
*************************************************************** */

package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class APDU {
	private String tipo;
	private String usuario;
	private String grupo;
	private String mensagem;
	private LocalDateTime tempoEnvio;
	private byte[] perfilUsuario;
	private String ipServidor;

	private static final String DELIMITADOR = "*";
	private static final String ESCAPE = "\\";

	public APDU(String tipo, String usuario, String grupo, byte[] perfilUsuario, String ipServidor) {
		this.tipo = tipo;
		this.usuario = usuario;
		this.grupo = grupo;
		this.perfilUsuario = perfilUsuario;
		this.ipServidor = ipServidor;
	}

	public APDU(String tipo, String usuario, String grupo, byte[] perfilUsuario, String ipServidor, String mensagem, LocalDateTime tempoEnvio) {
		this.tipo = tipo;
		this.usuario = usuario;
		this.grupo = grupo;
		this.mensagem = mensagem;
		this.tempoEnvio = tempoEnvio;
		this.perfilUsuario = perfilUsuario;
		this.ipServidor = ipServidor;
	}

	public String enviarMensagem() {
		return (this.mensagem != null && this.mensagem.isEmpty()) ? codificarMensagem(this.tipo, this.usuario, this.grupo, this.perfilUsuario, this.ipServidor)
		       : codificarMensagem(this.tipo, this.usuario, this.grupo, this.mensagem, formatarTempoEnvio(), this.perfilUsuario, this.ipServidor);
	}

	public String codificarMensagem(String tipo, String usuario, String grupo, byte[] perfilUsuario, String ipServidor) {
		tipo = tipo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		usuario = usuario.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		grupo = grupo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		String foto = Base64.getEncoder().encodeToString(perfilUsuario);
		ipServidor = ipServidor.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);

		return tipo + DELIMITADOR + usuario + DELIMITADOR + grupo + DELIMITADOR + foto + DELIMITADOR + ipServidor;
	}

	public String codificarMensagem(String tipo, String usuario, String grupo, String mensagem, String tempoEnvio, byte[] perfilUsuario, String ipServidor) {
		tipo = tipo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		usuario = usuario.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		grupo = grupo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		mensagem = mensagem.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		tempoEnvio = tempoEnvio.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		String foto = Base64.getEncoder().encodeToString(perfilUsuario);
		ipServidor = ipServidor.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);

		return tipo + DELIMITADOR + usuario + DELIMITADOR + grupo + DELIMITADOR + mensagem + DELIMITADOR + tempoEnvio + DELIMITADOR + foto + DELIMITADOR + ipServidor;
	}

	public static APDU decodificarMensagem(String msg) {
		String[] partes = msg.split("\\*");

		String tipoLocal = partes[0].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		String usuarioLocal = partes[1].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		String grupoLocal = partes[2].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		byte[] perfilUsuario = Base64.getDecoder().decode(partes[3]);
		String ipServidor = partes[4].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		String msgLocal = "";
		LocalDateTime envioLocal = null;

		if (partes.length > 5 && !partes[5].isEmpty() && !partes[6].isEmpty())  {
			msgLocal = partes[3].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
			DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			envioLocal = LocalDateTime.parse(partes[4], formato);
		}

		return (partes.length == 5) ? new APDU(tipoLocal, usuarioLocal, grupoLocal, perfilUsuario, ipServidor) 
		       : new APDU(tipoLocal, usuarioLocal, grupoLocal, perfilUsuario, ipServidor, msgLocal, envioLocal);
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

	public byte[] getPerfilUsuario() {
		return perfilUsuario;
	}

	public String getIpServidor() {
		return ipServidor;
	}

	private String formatarTempoEnvio() {
		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		String dataEnvio = tempoEnvio.format(formatador);

		return dataEnvio;
	}
}
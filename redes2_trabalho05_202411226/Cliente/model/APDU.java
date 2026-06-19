/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 17/06/2026
* Ultima alteracao.: 19/06/2026
* Nome.............: APDU
* Funcao...........: Classe que gerencia as operacoes das APDUs.
                     
*************************************************************** */

package model;

public class APDU {
	private String tipo;
	private String usuario;
	private String grupo;
	private String mensagem;

	private static final String DELIMITADOR = "*";
	private static final String ESCAPE = "\\";

	public APDU(String tipo, String usuario, String grupo) {
		this.tipo = tipo;
		this.usuario = usuario;
		this.grupo = grupo;
	}

	public APDU(String tipo, String usuario, String grupo, String mensagem) {
		this.tipo = tipo;
		this.usuario = usuario;
		this.grupo = grupo;
		this.mensagem = mensagem;
	}

	public String enviarMensagem() {
		return (this.mensagem.isEmpty()) ? codificarMensagem(this.tipo, this.usuario, this.grupo)
		       : codificarMensagem(this.tipo, this.usuario, this.grupo, this.mensagem);
	}

	public String codificarMensagem(String tipo, String usuario, String grupo) {
		tipo = tipo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		usuario = usuario.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		grupo = grupo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);

		return tipo + DELIMITADOR + usuario + DELIMITADOR + grupo;
	}

	public String codificarMensagem(String tipo, String usuario, String grupo, String mensagem) {
		tipo = tipo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		usuario = usuario.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		grupo = grupo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		mensagem = mensagem.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);

		return tipo + DELIMITADOR + usuario + DELIMITADOR + grupo + DELIMITADOR + mensagem;
	}

	public static APDU decodificarMensagem(String msg) {
		String[] partes = msg.split("//*");

		String tipoLocal = partes[0].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		String usuarioLocal = partes[1].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		String grupoLocal = partes[2].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		String msgLocal = "";

		if (partes.length > 3 && !partes[3].isEmpty()) {
			msgLocal = partes[3].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		}

		return (msgLocal.isEmpty()) ? new APDU(tipoLocal, usuarioLocal, grupoLocal) 
		       : new APDU(tipoLocal, usuarioLocal, grupoLocal, msgLocal);
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
}
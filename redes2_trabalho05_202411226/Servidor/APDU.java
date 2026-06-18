/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 17/06/2026
* Ultima alteracao.: 18/06/2026
* Nome.............: APDU
* Funcao...........: Classe que gerencia as operacoes das APDUs.
                     
*************************************************************** */

public class APDU {
	private static String tipo;
	private static String usuario;
	private static String grupo;
	private static String mensagem;

	private static final String DELIMITADOR = "*";
	private static final String ESCAPE = "\\";

	public String enviarSend(String usuario, String grupo, String mensagem) {
		this.usuario = usuario;
		this.grupo = grupo;
		this.mensagem = mensagem;
		this.tipo = "SEND";

		return codificarMensagem(this.tipo, this.usuario, this.grupo, this.mensagem);
	}

	public String enviarJoin(String usuario, String grupo) {
		this.usuario = usuario;
		this.grupo = grupo;
		this.tipo = "JOIN";

		return codificarMensagem(this.tipo, this.usuario, this.grupo);
	}

	public String enviarLeave(String usuario, String grupo) {
		this.usuario = usuario;
		this.grupo = grupo;
		this.tipo = "LEAVE";
		
		return codificarMensagem(this.tipo, this.usuario, this.grupo);
	}

	private String codificarMensagem(String tipo, String usuario, String grupo) {
		tipo = tipo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		usuario = usuario.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		grupo = grupo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);

		return tipo + DELIMITADOR + usuario + DELIMITADOR + grupo;
	}

	private String codificarMensagem(String tipo, String usuario, String grupo, String mensagem) {
		tipo = tipo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		usuario = usuario.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		grupo = grupo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		mensagem = mensagem.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);

		return tipo + DELIMITADOR + usuario + DELIMITADOR + grupo + DELIMITADOR + mensagem;
	}

	public static APDU decodificarMensagem(String msg) {
		String[] partes = msg.split("//*");

		this.tipo = partes[0].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		this.usuario = partes[1].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		this.grupo = partes[2].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		if (partes.length > 3 && !partes[3].isEmpty()) this.mensagem = partes[3].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);

		return (this.mensagem.isEmpty()) ? new APDU(this.tipo, this.usuario, this.grupo) : new APDU(this.tipo, this.usuario, this.grupo, this.mensagem);
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
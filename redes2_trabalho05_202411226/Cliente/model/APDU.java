/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 17/06/2026
* Ultima alteracao.: 28/06/2026
* Nome.............: APDU
* Funcao...........: Classe que gerencia as operacoes das APDUs.
                     
*************************************************************** */

package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class APDU {
	// Variaveis e instancias
	private String tipo;
	private String usuario;
	private String grupo;
	private String mensagem;
	private String status;
	private LocalDateTime tempoEnvio;
	private static final String DELIMITADOR = "*";
	private static final String ESCAPE = "\\";

  /*
   * ***************************************************************
   * Metodo: APDU
   * Funcao: inicializa uma nova instancia da classe APDU (para a APDU REGISTER)
   * Parametros: String tipo - tipo da APDU
                 String usuario - usuario autor da solicitacao
   * Retorno: nenhum
   ****************************************************************/

	public APDU(String tipo, String usuario) {
		this.tipo = tipo;
		this.usuario = usuario;
		this.grupo = null;
		this.mensagem = null;
		this.status = null;
		this.tempoEnvio = null;
	}

  /*
   * ***************************************************************
   * Metodo: APDU
   * Funcao: inicializa uma nova instancia da classe APDU (para as APDUs
             JOIN e LEAVE)
   * Parametros: String tipo - tipo da APDU
                 String usuario - usuario autor da solicitacao
								 String grupo - grupo ao qual o usuario deseja se juntar/sair
   * Retorno: nenhum
   ****************************************************************/

	public APDU(String tipo, String usuario, String grupo) {
		this.tipo = tipo;
		this.usuario = usuario;
		this.grupo = grupo;
		this.mensagem = null;
		this.tempoEnvio = null;
		this.status = null;
	}

  /*
   * ***************************************************************
   * Metodo: APDU
   * Funcao: inicializa uma nova instancia da classe APDU (para a APDU SEND)
   * Parametros: String tipo - tipo da APDU
                 String usuario - usuario autor da mensagem
								 String grupo - grupo onde a mensagem foi enviada
								 String mensagem - conteudo da mensagem
								 LocalDateTime tempoEnvio - tempo de envio da mensagem
   * Retorno: nenhum
   ****************************************************************/

	public APDU(String tipo, String usuario, String grupo, String mensagem, LocalDateTime tempoEnvio) {
		this.tipo = tipo;
		this.usuario = usuario;
		this.grupo = grupo;
		this.mensagem = mensagem;
		this.tempoEnvio = tempoEnvio;
		this.status = null;
	}

  /*
   * ***************************************************************
   * Metodo: APDU
   * Funcao: inicializa uma nova instancia da classe APDU (para a APDU CONFIRM)
   * Parametros: String tipo - tipo da APDU
                 String usuario - usuario autor da mensagem
								 String grupo - grupo onde a mensagem foi enviada
								 String mensagem - conteudo da mensagem
								 LocalDateTime tempoEnvio - tempo de envio da mensagem
								 String status - status da mensagem (DELIVERED ou READ)
   * Retorno: nenhum
   ****************************************************************/

	public APDU(String tipo, String usuario, String grupo, String mensagem, LocalDateTime tempoEnvio, String status) {
		this.tipo = tipo;
		this.usuario = usuario;
		this.grupo = grupo;
		this.mensagem = mensagem;
		this.tempoEnvio = tempoEnvio;
		this.status = status;
	}

  /*
   * ***************************************************************
   * Metodo: enviarMensagem
   * Funcao: extrai a mensagem codificada da APDU
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

	public String enviarMensagem() {
		if (this.grupo == null) {
			return codificarMensagem(this.tipo, this.usuario);
		}
		else {
			if (this.mensagem == null && this.tempoEnvio == null) {
				return codificarMensagem(this.tipo, this.usuario, this.grupo);
			}
			else if (this.status == null) {
				return codificarMensagem(this.tipo, this.usuario, this.grupo, this.mensagem, formatarTempoEnvio());
			}
			else {
				return codificarMensagem(this.tipo, this.usuario, this.grupo, this.mensagem, formatarTempoEnvio(), this.status);
			}
		}
	}

	/*
   * ***************************************************************
   * Metodo: codificarMensagem
   * Funcao: codifica a mensagem da APDU (para a APDU REGISTER)
   * Parametros: String tipo - tipo da APDU
                 String usuario - usuario autor da solicitacao
   * Retorno: String
   ****************************************************************/

	public String codificarMensagem(String tipo, String usuario) {
		// Inclui caracteres de escape caso o delimitador ou o escape fizerem parte do conteudo dos itens
		// mencionados
		tipo = tipo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		usuario = usuario.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);

    // Retorna a mensagem codificada, com cada item sendo separado por um asterisco para facilitar
    // a decodificacao
		return tipo + DELIMITADOR + usuario;
	}

  /*
   * ***************************************************************
   * Metodo: codificarMensagem
   * Funcao: codifica a mensagem da APDU (para APDUs JOIN e LEAVE)
   * Parametros: String tipo - tipo da APDU
                 String usuario - usuario autor da solicitacao
								 String grupo - grupo ao qual o usuario deseja se juntar/sair
   * Retorno: String
   ****************************************************************/

	public String codificarMensagem(String tipo, String usuario, String grupo) {
		// Inclui caracteres de escape caso o delimitador ou o escape fizerem parte do conteudo dos itens
		// mencionados
		tipo = tipo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		usuario = usuario.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		grupo = grupo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);

    // Retorna a mensagem codificada, com cada item sendo separado por um asterisco para facilitar
    // a decodificacao
		return tipo + DELIMITADOR + usuario + DELIMITADOR + grupo;
	}

  /*
   * ***************************************************************
   * Metodo: codificarMensagem
   * Funcao: codifica a mensagem da APDU (para a APDU SEND)
   * Parametros: String tipo - tipo da APDU
                 String usuario - usuario autor da mensagem
								 String grupo - grupo onde a mensagem foi enviada
								 String mensagem - conteudo da mensagem
								 String tempoEnvio - tempo de envio da mensagem
   * Retorno: String
   ****************************************************************/

	public String codificarMensagem(String tipo, String usuario, String grupo, String mensagem, String tempoEnvio) {
		// Inclui caracteres de escape caso o delimitador ou o escape fizerem parte do conteudo dos itens
		// mencionados
		tipo = tipo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		usuario = usuario.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		grupo = grupo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		mensagem = mensagem.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		tempoEnvio = tempoEnvio.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);

		// Retorna a mensagem codificada, com cada item sendo separado por um asterisco para facilitar
    // a decodificacao
		return tipo + DELIMITADOR + usuario + DELIMITADOR + grupo + DELIMITADOR + mensagem + DELIMITADOR + tempoEnvio;
	}

  /*
   * ***************************************************************
   * Metodo: codificarMensagem
   * Funcao: codifica a mensagem da APDU (para a APDU CONFIRM)
   * Parametros: String tipo - tipo da APDU
                 String usuario - usuario autor da mensagem
								 String grupo - grupo onde a mensagem foi enviada
								 String mensagem - conteudo da mensagem
								 String tempoEnvio - tempo de envio da mensagem
								 String status - status da mensagem
   * Retorno: String
   ****************************************************************/

	public String codificarMensagem(String tipo, String usuario, String grupo, String mensagem, String tempoEnvio, String status) {
		// Inclui caracteres de escape caso o delimitador ou o escape fizerem parte do conteudo dos itens
		// mencionados
		tipo = tipo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		usuario = usuario.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		grupo = grupo.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		mensagem = mensagem.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		tempoEnvio = tempoEnvio.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);
		status = status.replace(ESCAPE, ESCAPE + ESCAPE).replace(DELIMITADOR, ESCAPE + DELIMITADOR);

    // Retorna a mensagem codificada, com cada item sendo separado por um asterisco para facilitar
    // a decodificacao
		return tipo + DELIMITADOR + usuario + DELIMITADOR + grupo + DELIMITADOR + mensagem + DELIMITADOR + tempoEnvio + DELIMITADOR + status;
	}

  /*
   * ***************************************************************
   * Metodo: decodificarMensagem
   * Funcao: decodifica uma mensagem e a converte em uma APDU
   * Parametros: String msg - mensagem a ser decodificada
   * Retorno: APDU
   ****************************************************************/

	public static APDU decodificarMensagem(String msg) {
		// Elimina quaisquer lixo de caracteres
		msg = msg.trim().replace("\0", "");

		// Divide a mensagem em partes, delimitadas pelo asterisco
		String[] partes = msg.split("\\*", -1);

    // Decodifica o tipo, o usuario e o grupo citados na mensagem (variaveis globais)
		String tipoLocal = partes[0].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
		String usuarioLocal = partes[1].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);

		// Inicializa as variaveis exclusivas das APDUs SEND e CONFIRM
		String grupoLocal = null;
		String msgLocal = null;
		LocalDateTime envioLocal = null;
		String statusLocal = null;

		// Inicio do bloco if
		// Se a quantidade de partes da mensagem for maior ou igual a 3 e a ultima parte nao for 
		if (partes.length >= 3 && !partes[2].isEmpty()) {
			// Decodifica o conteudo e o tempo de envio da mensagem
			grupoLocal = partes[2].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);

      // Se a quantidade de partes da mensagem for maior ou igual a 5 e as duas ultimas partes
			// nao forem vazias
			if (partes.length >= 5 && !partes[3].isEmpty() && !partes[4].isEmpty()) {
				msgLocal = partes[3].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
				String tempoStr = partes[4].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);

	      // Inicio do bloco if
	      // Se o tamanho da String for maior que o formato esperado de data/hora
				if (tempoStr.length() > 19) {
					// Exclui o lixo gerado
					tempoStr = tempoStr.substring(0, 19);
				} // Fim do bloco if

	      // Converte a String em LocalDateTime
				DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
				envioLocal = LocalDateTime.parse(tempoStr, formato);

	      // Inicio do bloco if
				if (partes.length >= 6 && !partes[5].isEmpty()) {
					// Decodifica o status da mensagem caso ele existir (a quantidade de partes for maior ou igual a 6
				  // e a ultima parte nao for vazia)
					statusLocal = partes[5].replace(ESCAPE + DELIMITADOR, DELIMITADOR).replace(ESCAPE + ESCAPE, ESCAPE);
				} // Fim do bloco if
			} // Fim do bloco if
		} // Fim do bloco if

    // Retorna uma nova APDU formada a partir das informacoes decodificadas
		return (grupoLocal == null) ? new APDU(tipoLocal, usuarioLocal) : 
		       ((msgLocal == null && envioLocal == null) ? new APDU(tipoLocal, usuarioLocal, grupoLocal) : 
		       	 ((statusLocal == null) ? new APDU(tipoLocal, usuarioLocal, grupoLocal, msgLocal, envioLocal) : 
		                                  new APDU(tipoLocal, usuarioLocal, grupoLocal, msgLocal, envioLocal, statusLocal)));
	}

  /*
   * ***************************************************************
   * Metodo: setTipo
   * Funcao: define o tipo da APDU
   * Parametros: String tipo - tipo a ser definido
   * Retorno: void
   ****************************************************************/

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

  /*
   * ***************************************************************
   * Metodo: getTipo
   * Funcao: retorna o tipo da APDU
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

	public String getTipo() {
		return tipo;
	}

  /*
   * ***************************************************************
   * Metodo: setUsuario
   * Funcao: define o usuario citado na APDU
   * Parametros: String usuario - usuario a ser definido
   * Retorno: void
   ****************************************************************/

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

  /*
   * ***************************************************************
   * Metodo: getUsuario
   * Funcao: retorna o usuario citado na APDU
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

	public String getUsuario() {
		return usuario;
	}

  /*
   * ***************************************************************
   * Metodo: setGrupo
   * Funcao: define o grupo citado na APDU
   * Parametros: String grupo - grupo a ser definido
   * Retorno: void
   ****************************************************************/

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

  /*
   * ***************************************************************
   * Metodo: getGrupo
   * Funcao: retorna o grupo citado na APDU
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

	public String getGrupo() {
		return grupo;
	}

  /*
   * ***************************************************************
   * Metodo: setMensagem
   * Funcao: define o conteudo da mensagem
   * Parametros: String mensagem - conteudo a ser definido
   * Retorno: void
   ****************************************************************/

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

  /*
   * ***************************************************************
   * Metodo: getMensagem
   * Funcao: retorna o conteudo da mensagem
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

	public String getMensagem() {
		return mensagem;
	}

  /*
   * ***************************************************************
   * Metodo: setTempoEnvio
   * Funcao: define o tempo de envio da mensagem
   * Parametros: LocalDateTime tempoEnvio - tempo de envio a ser definido
   * Retorno: void
   ****************************************************************/

	public void setTempoEnvio(LocalDateTime tempoEnvio) {
		this.tempoEnvio = tempoEnvio;
	}

  /*
   * ***************************************************************
   * Metodo: getTempoEnvio
   * Funcao: define o tempo de envio da mensagem
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: LocalDateTime
   ****************************************************************/

	public LocalDateTime getTempoEnvio() {
		return tempoEnvio;
	}

  /*
   * ***************************************************************
   * Metodo: setStatus
   * Funcao: define o status da mensagem
   * Parametros: String status - status a ser definido
   * Retorno: void
   ****************************************************************/

	public void setStatus(String status) {
		this.status = status;
	}

  /*
   * ***************************************************************
   * Metodo: getStatus
   * Funcao: retorna o status da mensagem
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

	public String getStatus() {
		return status;
	}

  /*
   * ***************************************************************
   * Metodo: formatarTempoEnvio
   * Funcao: converte o tempo de envio em String
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

	public String formatarTempoEnvio() {
		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		String dataEnvio = tempoEnvio.format(formatador);

		return dataEnvio;
	}
}
/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 21/06/2026
* Ultima alteracao.: 30/06/2026
* Nome.............: Mensagem
* Funcao...........: Classe que controla as operacoes das mensagens enviadas no chat do aplicativo.
                     
*************************************************************** */

package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.image.Image;

public class Mensagem {
	// Variaveis e instancias
	private String texto;
	private String autor;
	private Image status;
  private boolean read;
	private LocalDateTime tempoEnvio;

  /*
   * ***************************************************************
   * Metodo: Mensagem
   * Funcao: inicializa uma nova instancia da classe Mensagem
   * Parametros: String texto - conteudo da mensagem
                 String autor - autor da mensagem
                 Image status - status da mensagem
   * Retorno: nenhum
   ****************************************************************/

	public Mensagem(String texto, String autor, Image status) {
		this.texto = texto;
		this.autor = autor;
		this.status = status;
		tempoEnvio = LocalDateTime.now();
    read = false;
	}

  /*
   * ***************************************************************
   * Metodo: Mensagem
   * Funcao: inicializa uma nova instancia da classe Mensagem
   * Parametros: String texto - conteudo da mensagem
                 String autor - autor da mensagem
                 LocalDateTime tempoEnvio - tempo de envio da mensagem
   * Retorno: nenhum
   ****************************************************************/

	public Mensagem(String texto, String autor, LocalDateTime tempoEnvio) {
		this.texto = texto;
		this.autor = autor;
		this.tempoEnvio = tempoEnvio;
	}

  /*
   * ***************************************************************
   * Metodo: setTexto
   * Funcao: define o conteudo da mensagem
   * Parametros: String t - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setTexto(String t) {
		this.texto = t;
	}

  /*
   * ***************************************************************
   * Metodo: getTexto
   * Funcao: retorna o conteudo da mensagem
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

	public String getTexto() {
		return texto;
	}


  /*
   * ***************************************************************
   * Metodo: setAutor
   * Funcao: define o autor da mensagem
   * Parametros: String autor - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setAutor(String autor) {
		this.autor = autor;
	}

  /*
   * ***************************************************************
   * Metodo: getAutor
   * Funcao: retorna o autor da mensagem
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

	public String getAutor() {
		return autor;
	}

  /*
   * ***************************************************************
   * Metodo: setStatus
   * Funcao: define o status da mensagem
   * Parametros: Image status - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setStatus(Image status) {
		this.status = status;
	}

  /*
   * ***************************************************************
   * Metodo: getStatus
   * Funcao: retorna o status da mensagem
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Image
   ****************************************************************/

	public Image getStatus() {
		return status;
	}

  /*
   * ***************************************************************
   * Metodo: setTempoEnvio
   * Funcao: define o tempo de envio da mensagem
   * Parametros: LocalDateTime tempoEnvio - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setTempoEnvio(LocalDateTime tempoEnvio) {
		this.tempoEnvio = tempoEnvio;
	}

  /*
   * ***************************************************************
   * Metodo: getTempoEnvio
   * Funcao: retorna o tempo de envio da mensagem
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: LocalDateTime
   ****************************************************************/

	public LocalDateTime getTempoEnvio() {
		return tempoEnvio;
	}

  /*
   * ***************************************************************
   * Metodo: formatarTempoEnvio
   * Funcao: converte o tempo de envio da mensagem para String
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

  public String formatarTempoEnvio() {
    // Formata o tempo de envio em String (Dia/Mes/Ano Horas:Minutos)
    DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    String dataEnvio = tempoEnvio.format(formatador);

    // Retorna a data convertida em String
    return dataEnvio;
  }

  /*
   * ***************************************************************
   * Metodo: formatarTempoHoraEnvio
   * Funcao: converte o tempo de envio da mensagem para String (incluindo segundos)
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

  public String formatarTempoHoraEnvio() {
    // Formata o tempo de envio em String (Dia/Mes/Ano Horas:Minutos)
    DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    String dataEnvio = tempoEnvio.format(formatador);

    // Retorna a data convertida em String
    return dataEnvio;
  }

  /*
   * ***************************************************************
   * Metodo: formatarHora
   * Funcao: retorna apenas as horas de envio da mensagem em String
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

	public String formatarHora() {
		// Formata as horas de envio da mensagem em String (Horas:Minutos)
		DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm");
		String horaEnvio = tempoEnvio.format(formatador);
 
    // Retorna o valor obtido
		return horaEnvio;
	}

  /*
   * ***************************************************************
   * Metodo: setRead
   * Funcao: define se a mensagem foi lida ou nao
   * Parametros: boolean r - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setRead(boolean r) {
    this.read = r;
  }

  /*
   * ***************************************************************
   * Metodo: getRead
   * Funcao: retorna se a mensagem foi lida ou nao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  public boolean isRead() {
    return read;
  }
}
/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 24/06/2026
* Ultima alteracao.: 27/06/2026
* Nome.............: TelaPrincipalController
* Funcao...........: Classe que controla os eventos da TelaPrincipal.
					 
*************************************************************** */

package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import model.*;

public class TelaPrincipalController implements Initializable {
	// Componentes da interface
	@FXML private TextArea txtUDP;
	@FXML private TextArea txtTCP;

  // Variaveis e instancias
  public static volatile TelaPrincipalController controller;
	private servidorUDP serverUDP;
	private servidorTCP serverTCP;

	/*
   * ***************************************************************
   * Metodo: initialize
   * Funcao: executa um conjunto de instrucoes durante a inicializacao da aplicacao
   * Parametros: URL url: endereco do programa
				 ResourceBundle rb: recursos para inicializacao
   * Retorno: void
   ****************************************************************/
  
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Inicializa a instancia volatil do controller
		controller = this;

		// Inicializa as Threads UDP e TCP do servidor
		serverUDP = new servidorUDP();
		serverTCP = new servidorTCP();
		serverUDP.setDaemon(true);
		serverTCP.setDaemon(true);
		serverUDP.start();
		serverTCP.start();
	}

  /*
   * ***************************************************************
   * Metodo: logUDP
   * Funcao: registra uma operacao do protocolo UDP
   * Parametros: String texto - mensagem a ser registrada
   * Retorno: void
   ****************************************************************/
  
	public void logUDP(String texto) {
		Platform.runLater(() -> txtUDP.appendText(texto + "\n"));
	}

  /*
   * ***************************************************************
   * Metodo: logTCP
   * Funcao: registra uma operacao do protocolo TCP
   * Parametros: String texto - mensagem a ser registrada
   * Retorno: void
   ****************************************************************/

	public void logTCP(String texto) {
		Platform.runLater(() -> txtTCP.appendText(texto + "\n"));
	}
}
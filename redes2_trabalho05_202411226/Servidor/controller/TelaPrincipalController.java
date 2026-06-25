/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 24/06/2026
* Ultima alteracao.: 24/06/2026
* Nome.............: TelaPrincipalController
* Funcao...........: Classe que controla os eventos da TelaPrincipal.
                     
*************************************************************** */

package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import model.*;

public class TelaPrincipalController implements Initializable {
	// Componentes da interface
	@FXML private TextArea txtUDP;
	@FXML private TextArea txtTCP;

  public static volatile TelaPrincipalController controller;
	private servidorUDP serverUDP;
	private servidorTCP serverTCP;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		controller = this;

		serverUDP = new servidorUDP();
		serverTCP = new servidorTCP();

		serverUDP.setDaemon(true);
		serverTCP.setDaemon(true);

		serverUDP.start();
		serverTCP.start();
	}

	public void logUDP(String texto) {
		txtUDP.appendText(texto + "\n");
	}

	public void logTCP(String texto) {
		txtTCP.appendText(texto + "\n");
	}
}
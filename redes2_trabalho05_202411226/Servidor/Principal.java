/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/06/2026
* Ultima alteracao.: 24/06/2026
* Nome.............: DsgChat (Principal/Servidor)
* Funcao...........: Aplicativo de Instant Messaging desenvolvido utilizando protocolos
                     da camada de transporte TCP/UDP (Servidor).
                     
*************************************************************** */

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import controller.*;

public class Principal extends Application {
	@Override
	public void start(Stage stage) {
		try {
			TelaPrincipalController TelaPrincipalController = new TelaPrincipalController();

			Parent root = FXMLLoader.load(getClass().getResource("/view/TelaPrincipal.fxml"));
			Scene scene = new Scene(root);

			Font.loadFont(getClass().getResourceAsStream("/util/VCR_OSD_MONO_1.001.ttf"), 18);

			stage.setScene(scene);
			stage.setTitle("DsgChat: Server Log");
			stage.setResizable(false);
			stage.show();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

   /*
   * ***************************************************************
   * Metodo: main
   * Funcao: inicializa a aplicacao
   * Parametros: String[] args - vetor contendo argumentos necessarios 
                                 para a inicializacao do programa
   * Retorno: void
   ****************************************************************/

	public static void main(String[] args) {
		launch(args);
	}
}
/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/06/2026
* Ultima alteracao.: 02/07/2026
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
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import controller.*;

public class Principal extends Application {

  /*
   * ***************************************************************
   * Metodo: start
   * Funcao: configura a aplicacao
   * Parametros: Stage stage - janela do programa
   * Retorno: void
   ****************************************************************/

	@Override
	public void start(Stage stage) {
		// Inicio do bloco try/catch
		try {
			// Inicializa os controllers de cada tela
			TelaPrincipalController TelaPrincipalController = new TelaPrincipalController();

			// Carrega o arquivo FXML e gera uma nova cena
			Parent root = FXMLLoader.load(getClass().getResource("/view/TelaPrincipal.fxml"));
			Scene scene = new Scene(root);

      // Carrega a fonte dentro da interface do programa
			Font.loadFont(getClass().getResourceAsStream("/util/VCR_OSD_MONO_1.001.ttf"), 18);

			// Configura o icone da janela
			Image icone = new Image(getClass().getResource("/img/log.png").toExternalForm());
			stage.getIcons().add(icone);

			// Configura a janela
			stage.setScene(scene);
			stage.setTitle("DsgChat: Server Log");
			stage.setResizable(false);
			stage.show();
		}
		catch (IOException e) {
			// Em caso de excecao, emite a pilha de execucao para rastrear a sua origem
			e.printStackTrace();
		} // Fim do bloco try/catch
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
/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 11/04/2026
* Ultima alteracao.: 11/04/2026
* Nome.............: Vetor A Distancia (Principal)
* Funcao...........: Este trabalho tem como objetivo simular o roteamento de pacotes dentro da camada de rede 
                     atraves do algoritmo de vetor a distancia.
                     
*************************************************************** */

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.application.Application;

public class Principal extends Application {

  /*
   * ***************************************************************
   * Metodo: start
   * Funcao: configura a aplicacao
   * Parametros: Stage stage - janela do programa
   * Retorno: void
   ****************************************************************/

	@Override 
	public void start(Stage stage) throws IOException {
		// Carrega o arquivo FXML para gerar uma nova cena (tela)
		Parent root = FXMLLoader.load(getClass().getResource("/view/TelaPrincipal.fxml"));
		Scene scene = new Scene(root);

		// Carrega a fonte
		Font.loadFont(getClass().getResourceAsStream("/util/VCR_OSD_MONO_1.001.ttf"), 18);

    // Carrega a imagem do icone da janela
		Image icone = new Image(getClass().getResource("/img/Icone.png").toExternalForm());

		// Configurando a tela inicial (titulo, redimensionamento, icone etc)
		stage.setTitle("Vetor A Distancia");
		stage.setScene(scene);
		stage.getIcons().add(icone);
		stage.setResizable(false);
		stage.show();
	}

  /*
   * ***************************************************************
   * Metodo: main
   * Funcao: executa a aplicacao
   * Parametros: String[] args - vetor contendo argumentos necessarios para a inicializacao do programa
   * Retorno: void
   ****************************************************************/

	public static void main(String[] args) {
		launch(args);
	}
}
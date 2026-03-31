/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 29/03/2026
* Ultima alteracao.: 30/03/2026
* Nome.............: Caminho Mais Curto (Principal)
* Funcao...........: Este trabalho tem como objetivo simular o roteamento de pacotes dentro da camada de rede 
                     atraves do algoritmo do caminho mais curto.
                     
*************************************************************** */

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.application.Application;
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
			// Carrega os controllers de cada tela para serem compilados simultaneamente
			TelaMenuController TelaMenuController = new TelaMenuController();
			TelaPrincipalController TelaPrincipalController = new TelaPrincipalController();

			// Carrega o arquivo FXML para gerar uma nova cena (tela)
			Parent root = FXMLLoader.load(getClass().getResource("/view/TelaMenu.fxml"));
			Scene scene = new Scene(root);

			// Carregando a fonte
			Font.loadFont(getClass().getResourceAsStream("/util/VCR_OSD_MONO_1.001.ttf"), 18);

      // Carrega a imagem do icone da janela
			Image icone = new Image(getClass().getResource("/img/Icone.png").toExternalForm());

			// Configurando a tela inicial (titulo, redimensionamento, icone etc)
			stage.setScene(scene);
			stage.setTitle("Caminho Mais Curto");
			stage.setResizable(false);
			stage.getIcons().add(icone);
			stage.show();
		}
		catch (IOException ex) {
			// Interrompe a execucao e exibe um aviso
			// em caso de excecao
			ex.printStackTrace();
		} // Fim do bloco try/catch
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
/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/06/2026
* Ultima alteracao.: 20/06/2026
* Nome.............: DsgChat (Principal/Cliente)
* Funcao...........: Aplicativo de Instant Messaging desenvolvido utilizando protocolos
                     da camada de transporte TCP/UDP (Cliente).
                     
*************************************************************** */

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
	public void start(Stage stage) throws IOException {
		// Inicializa os controllers de cada tela
		TelaMenuController TelaMenuController = new TelaMenuController();
		TelaPrincipalController TelaPrincipalController = new TelaPrincipalController();
		
		// Carrega o arquivo FXML e gera uma nova cena
		Parent root = FXMLLoader.load(getClass().getResource("/view/TelaMenu.fxml"));
		Scene scene = new Scene(root);

    // Configura a janela
		stage.setScene(scene);
		stage.setTitle("DsgChat");
		stage.setResizable(false);
		stage.show();
	}

	/*
   * ***************************************************************
   * Metodo: main
   * Funcao: executa a aplicacao
   * Parametros: String[] args - vetor contendo argumentos necessarios 
                                 para a inicializacao do programa
   * Retorno: void
   ****************************************************************/

	public static void main(String[] args) {
		launch(args);
	}
}
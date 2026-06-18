/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/06/2026
* Ultima alteracao.: 17/06/2026
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
	@Override
	public void start(Stage stage) throws IOException {
		// Inicializa os controllers de cada tela
		TelaMenuController TelaMenuController = new TelaMenuController();
		TelaPrincipalController TelaPrincipalController = new TelaPrincipalController();
		
		Parent root = FXMLLoader.load(getClass().getResource("/view/TelaMenu.fxml"));
		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.setTitle("DsgChat");
		stage.setResizable(false);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
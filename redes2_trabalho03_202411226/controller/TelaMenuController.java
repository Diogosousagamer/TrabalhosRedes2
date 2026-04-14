/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 14/04/2026
* Ultima alteracao.: 14/04/2026
* Nome.............: TelaMenuController
* Funcao...........: Esta classe tem como objetivo gerenciar as operacoes da TelaMenu. 
                     
*************************************************************** */

package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class TelaMenuController {
	// Componentes da interface
	@FXML private Button btnProsseguir;

	/*
   * ***************************************************************
   * Metodo: prosseguir
   * Funcao: inicia a simulacao
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

	@FXML
	private void prosseguir(ActionEvent event) throws IOException {
		// Carrega o arquivo FXML e gera uma nova cena (tela)
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaPrincipal.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);

		// Carrega a cena (tela) dentro da mesma janela
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
	}
}
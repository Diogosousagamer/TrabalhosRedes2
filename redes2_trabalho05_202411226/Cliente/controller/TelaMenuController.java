/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 17/06/2026
* Nome.............: TelaMenuController
* Funcao...........: Classe que controla os eventos da TelaMenu.
                     
*************************************************************** */

package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Usuario;

public class TelaMenuController implements Initializable {
	// Componentes da interface
	@FXML private AnchorPane painelAviso;
	@FXML private Button btnAlterarPerfil;
	@FXML private Button btnEntrar;
	@FXML private Button btnOk;
	@FXML private Circle imgPerfil;
	@FXML private Label lblAviso;
	@FXML private TextField txtNomeUsuario;
	@FXML private TextField txtIpServidor;

  // Variaveis e instancias
	private static final Image semFoto = new Image(TelaMenuController.class.getResource("/img/SemFoto.png").toExternalForm());
	private Image perfil;
	private String caminhoImagem;
	private final String DADOS_VAZIOS = "Preencha todos os dados obrigatorios antes de prosseguir.";
	private boolean usuarioVazio;
	private boolean ipVazio;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		perfil = semFoto;
		imgPerfil.setFill(new ImagePattern(perfil));

		txtNomeUsuario.textProperty().addListener((obs, oldValue, newValue) -> {
			if (usuarioVazio && !newValue.isEmpty()) {
				txtNomeUsuario.setStyle("-fx-background-color: #d9d9d9; -fx-background-radius: 15px; -fx-padding: 8px");
				usuarioVazio = false;
			}
		});
		
		txtIpServidor.textProperty().addListener((obs, oldValue, newValue) -> {
			if (ipVazio && !newValue.isEmpty()) {
				txtIpServidor.setStyle("-fx-background-color: #d9d9d9; -fx-background-radius: 15px; -fx-padding: 8px");
				if (ipVazio) ipVazio = false;
			}
		});
	}

	@FXML
	private void entrar(ActionEvent event) throws IOException {
		if (!verificarDados()) return;

		String nome = txtNomeUsuario.getText().trim();
		String ip = txtIpServidor.getText().trim();

		Usuario usuario = new Usuario(perfil, nome, ip);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaPrincipal.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);

		TelaPrincipalController principal = loader.getController();
		principal.carregarUsuario(usuario);

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
	}

	@FXML
	private void alterarPerfil(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));
		File f = fc.showOpenDialog(new Stage());

		if (f != null) {
			caminhoImagem = f.toURI().toString();
			perfil = new Image(caminhoImagem);
			imgPerfil.setFill(new ImagePattern(perfil));
		}
	}

	@FXML
	private void ocultarAviso(ActionEvent event) {
		painelAviso.setVisible(false);
	}

	private boolean verificarDados() {
		String nome = txtNomeUsuario.getText();
		String ip = txtIpServidor.getText();
		usuarioVazio = nome.isEmpty();
		ipVazio = ip.isEmpty();

		if (usuarioVazio || ipVazio) {
			if (usuarioVazio) txtNomeUsuario.setStyle("-fx-background-color: #d9d9d9; -fx-background-radius: 15px; -fx-padding: 8px; -fx-border-color: #ff0000; -fx-border-radius: 15px; -fx-border-width: 2px");
			if (ipVazio) txtIpServidor.setStyle("-fx-background-color: #d9d9d9; -fx-background-radius: 15px; -fx-padding: 8px; -fx-border-color: #ff0000; -fx-border-radius: 15px; -fx-border-width: 2px");
			carregarAviso(DADOS_VAZIOS);

			return false;
		}

		return true;
	}

	private void carregarAviso(String aviso) {
		lblAviso.setText(aviso);
		painelAviso.setVisible(true);
	}
}
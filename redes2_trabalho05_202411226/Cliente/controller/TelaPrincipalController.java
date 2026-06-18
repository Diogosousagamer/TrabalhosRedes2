/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 17/06/2026
* Nome.............: TelaPrincipalController
* Funcao...........: Classe que controla os eventos da TelaPrincipal.
                     
*************************************************************** */

package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
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
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Grupo;
import model.Usuario;

public class TelaPrincipalController implements Initializable {
	// Componentes da interface
	@FXML private AnchorPane painelJuntarGrupo;
	@FXML private Button btnEncerrarSessao;
	@FXML private Button btnEntrarGrupo;
	@FXML private Button btnMudarImagem;
	@FXML private Circle imgGrupo;
	@FXML private Circle imgPerfil;
	@FXML private Label lblIpServidor;
	@FXML private Label lblUsuario;
	@FXML private TextField txtGrupo;
	@FXML private VBox listaGrupos;

	// Variaveis e instancias
	private static final Image semFoto = new Image(TelaMenuController.class.getResource("/img/SemFoto.png").toExternalForm());
	private Usuario usuario;
	public static volatile TelaPrincipalController principal;

	@Override 
	public void initialize(URL url, ResourceBundle rb) {
		principal = this;
	}

	@FXML
	private void juntarGrupo(ActionEvent event) {
		imgGrupo.setFill(new ImagePattern(semFoto));
		txtGrupo.setText("");
		painelJuntarGrupo.setVisible(true);
	}

	@FXML
	private void mudarImagem(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));
		File f = fc.showOpenDialog(new Stage());

		if (f != null) {
			String caminhoImagem = f.toURI().toString();
			Image perfil = new Image(caminhoImagem);
			imgGrupo.setFill(new ImagePattern(perfil));
		}
	}

	@FXML
	private void encerrarSessao(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaMenu.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
	}

	public void carregarUsuario(Usuario u) {
		this.usuario = u;
		carregarInformacoes();
	}

	public void carregarInformacoes() {
		// Carrega as informacoes basicas
		lblUsuario.setText(Usuario.nome);
		lblIpServidor.setText(Usuario.ipServidor);
		imgPerfil.setFill(new ImagePattern(Usuario.perfil));

		// Inicializa o UDP do cliente
		// Usuario.udp.receberMensagem();

		iniciarGrupos();
	}

	public void iniciarGrupos() {
		if (Usuario.grupos.isEmpty()) return;
		if (!listaGrupos.getChildren().isEmpty()) listaGrupos.getChildren().clear();

		for (Grupo g : Usuario.grupos) {

		}
	}
}
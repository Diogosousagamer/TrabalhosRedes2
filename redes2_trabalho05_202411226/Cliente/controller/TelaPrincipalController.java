/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 22/06/2026
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.clienteTCP;
import model.clienteUDP;
import model.APDU;
import model.Grupo;
import model.Mensagem;
import model.Usuario;

public class TelaPrincipalController implements Initializable {
	// Componentes da interface
	@FXML private AnchorPane painelAviso;
	@FXML private AnchorPane painelChat;
	@FXML private AnchorPane painelJuntarGrupo;
	@FXML private Button btnEncerrarSessao;
	@FXML private Button btnEntrarGrupo;
	@FXML private Button btnFecharGrupo;
	@FXML private Button btnMudarImagem;
	@FXML private Button btnOk;
	@FXML private Circle imgGrupo;
	@FXML private Circle imgPerfil;
	@FXML private Label lblAviso;
	@FXML private Label lblIpServidor;
	@FXML private Label lblUsuario;
	@FXML private TextField txtGrupo;
	@FXML private VBox listaGrupos;

	// Variaveis e instancias
	private ArrayList<Grupo> grupos;
	public ArrayList<Node> componentesAntigos = new ArrayList<>(); 
	private static final String GRUPO_VAZIO = "Insira o nome do grupo.";
	private static final String GRUPO_EXISTENTE = "Voce ja entrou neste grupo.";
	private static final Image semFoto = new Image(TelaMenuController.class.getResource("/img/SemFoto.png").toExternalForm());
	private Image perfilGrupo;
	public static volatile TelaPrincipalController principal;

	@Override 
	public void initialize(URL url, ResourceBundle rb) {
		perfilGrupo = semFoto;
		grupos = Usuario.getUsuario().getGrupos();
		componentesAntigos.addAll(painelChat.getChildren());

		txtGrupo.textProperty().addListener((obs, oldValue, newValue) -> {
			if (txtGrupo.getText().isEmpty() && !newValue.isEmpty()) {
				txtGrupo.setStyle("-fx-background-color: #d9d9d9; -fx-background-radius: 15px; -fx-padding: 8px; -fx-prompt-text-fill: #3a3d3a");
			}
		});

		principal = this;
		carregarInformacoes();
	}

	@FXML
	private void abrirChat(HBox caixa, Grupo g, MouseEvent event) {
		if (g.isSelected()) return;

		g.setSelected(true);
		caixa.setStyle("-fx-background-color: #969696");

		for (Grupo grupo : grupos) {
			if (!grupo.getNome().equals(g.getNome())) {
				grupo.setSelected(false);
			}
		}

		for (Node filho : listaGrupos.getChildren()) {
			if (filho instanceof HBox && !filho.equals(caixa)) {
				HBox caixaAtual = (HBox) filho;
				caixaAtual.setStyle("-fx-background-color: transparent");
			}
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaGrupo.fxml"));
			Parent root = loader.load();

			TelaGrupoController chat = loader.getController();
			chat.carregarGrupo(g);

			painelChat.getChildren().clear();
			painelChat.getChildren().add(root);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void juntarGrupo(ActionEvent event) {
		imgGrupo.setFill(new ImagePattern(semFoto));
		txtGrupo.setText("");
		painelJuntarGrupo.setVisible(true);
	}

	@FXML
	private void entrarGrupo(ActionEvent event) {
		String nomeGrupo = txtGrupo.getText();

		if (nomeGrupo.isEmpty()) {
			txtGrupo.setStyle("-fx-background-color: #D9D9D9; -fx-padding: 8px; -fx-background-radius: 15px; -fx-prompt-text-fill: #3a3d3a; -fx-border-color: #ff0000; -fx-border-radius: 15px; -fx-border-width: 2px");
			carregarAviso(GRUPO_VAZIO);
			return; 
		}
		else if (novoGrupoExiste(nomeGrupo)) {
			carregarAviso(GRUPO_EXISTENTE);
			return;
		}

		Grupo g = new Grupo(perfilGrupo, nomeGrupo);
		grupos.add(g);
		painelJuntarGrupo.setVisible(false);
		Usuario.getUsuario().getTCP().setAPDU(new APDU("JOIN", Usuario.getUsuario().getNome(), g.getNome()));

		carregarGrupos();
	}

	@FXML
	private void mudarImagem(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));
		File f = fc.showOpenDialog(new Stage());

		if (f != null) {
			String caminhoImagem = f.toURI().toString();
			perfilGrupo = new Image(caminhoImagem);
			imgGrupo.setFill(new ImagePattern(perfilGrupo));
		}
	}

	@FXML
	private void fecharJanelaGrupo(ActionEvent event) {
		painelJuntarGrupo.setVisible(false);
	}

	@FXML
	private void encerrarSessao(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaMenu.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
	}

	@FXML
	private void ocultarAviso(ActionEvent event) {
		painelAviso.setVisible(false);
	}	

	private boolean novoGrupoExiste(String nome) {
		for (Grupo g : grupos) {
			if (g.getNome().equals(nome)) {
				return true;
			}
		}

		return false;
	}

	public void carregarInformacoes() {
		// Carrega as informacoes basicas do usuario (nome, ip do servidor e perfil)
		lblUsuario.setText(Usuario.getUsuario().getNome());
		lblIpServidor.setText("Servidor: " + Usuario.getUsuario().getIpServidor());
		imgPerfil.setFill(new ImagePattern(Usuario.getUsuario().getPerfil()));

		carregarGrupos();
	}

	public void carregarGrupos() {
		if (grupos == null || grupos.isEmpty()) return;
		if (!listaGrupos.getChildren().isEmpty()) listaGrupos.getChildren().clear();

		for (Grupo g : grupos) {
			HBox grupo = new HBox();
			grupo.setAlignment(Pos.CENTER_LEFT);
			grupo.setPrefWidth(237);
			grupo.setPrefHeight(67);
			grupo.setPadding(new Insets(5, 5, 5, 5));
			grupo.setSpacing(10);
			grupo.setCursor(Cursor.HAND);
			grupo.getStyleClass().add("botao-grupo");
			grupo.getStylesheets().add(getClass().getResource("/util/principal.css").toExternalForm());
			if (g.isSelected()) grupo.setStyle("-fx-background-color: #969696");

			Circle imagemGrupo = new Circle();
			imagemGrupo.setRadius(25);
			imagemGrupo.setStrokeWidth(0);
			imagemGrupo.setFill(new ImagePattern(g.getPerfilGrupo()));

			VBox infoGrupo = new VBox();
			infoGrupo.setAlignment(Pos.CENTER_LEFT);
			infoGrupo.setPrefWidth(167);
			infoGrupo.setPrefHeight(67);
			infoGrupo.setSpacing(3);

			Label nomeGrupo = new Label(g.getNome());
			nomeGrupo.setFont(Font.font("Calibri", FontWeight.BOLD, 14));
			nomeGrupo.setTextFill(Color.WHITE);

			Mensagem ultMsg = g.obterUltimaMensagem();
			boolean mensagemUsuario = (ultMsg != null && ultMsg.getAutor().equals(Usuario.getUsuario().getNome()));

			String autor = (mensagemUsuario) ? "Voce: " : ((ultMsg != null) ?  ultMsg.getAutor().getNome() + ": " : "");
			Label ultimaMensagem = new Label((ultMsg != null && !ultMsg.getTexto().isEmpty()) ? autor + ultMsg.getTexto() : "");
			ultimaMensagem.setFont(Font.font("Calibri", 12));
			ultimaMensagem.setTextFill(Color.web("#7d7c7a"));

			infoGrupo.getChildren().addAll(nomeGrupo, ultimaMensagem);

			grupo.getChildren().addAll(imagemGrupo, infoGrupo);

			grupo.setOnMouseClicked(event -> {
				abrirChat(grupo, g, event);
			});

			listaGrupos.getChildren().add(grupo);
		}
	}

	private void carregarAviso(String aviso) {
		lblAviso.setText(aviso);
		painelAviso.setVisible(true);
	}
}
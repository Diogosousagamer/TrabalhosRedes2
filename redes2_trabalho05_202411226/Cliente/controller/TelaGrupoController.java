/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 23/06/2026
* Nome.............: TelaGrupoController
* Funcao...........: Classe que controla os eventos da TelaGrupo.
                     
*************************************************************** */

package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.APDU;
import model.Grupo;
import model.Mensagem;
import model.Usuario;

public class TelaGrupoController implements Initializable {
	// Componentes da interface
	@FXML private Button btnEnviarMensagem;
  @FXML private Button btnSairChat;
  @FXML private Button btnSairGrupo;
  @FXML private Label lblNomeGrupo;
  @FXML private TextField txtMensagem;
  @FXML private VBox listaMensagens;

  // Variaveis e instancias
  private Grupo g;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    txtMensagem.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        enviarMensagem(new ActionEvent());
      }
    }); 
  }

  @FXML
  private void sairChat(ActionEvent event) {
    g.setSelected(false);
    TelaPrincipalController.principal.recarregarComponentes();
  } 

  @FXML
  private void sairGrupo(ActionEvent event) {
    Usuario.getUsuario().getGrupos().remove(g);
    Usuario.getUsuario().getTCP().enviarAPDU(new APDU("LEAVE", Usuario.getUsuario().getNome(), g.getNome()));
    sairChat(event);
  }

  @FXML
  private void enviarMensagem(ActionEvent event) {
    if (txtMensagem.getText().isEmpty()) return;
    
    String mensagem = txtMensagem.getText();
    Usuario autor = Usuario.getUsuario();
    Mensagem m = new Mensagem(mensagem, autor);

    txtMensagem.clear();

    g.adicionarMensagem(m);
    carregarMensagens();
  }

  public void carregarGrupo(Grupo g) {
    this.g = g;
    lblNomeGrupo.setText(this.g.getNome());   
    carregarMensagens();
  }  

  private void carregarMensagens() {
    ArrayList<Mensagem> mensagens = g.getMensagens();
    if (mensagens == null) return;
    if (!listaMensagens.getChildren().isEmpty()) listaMensagens.getChildren().clear();

    for (Mensagem m : mensagens) {
      boolean ehUsuario = (m.getAutor() != null && m.getAutor().getNome().equals(Usuario.getUsuario().getNome()));

      HBox mensagem = new HBox();
      mensagem.setAlignment((ehUsuario) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
      mensagem.setPadding(new Insets(10, 10, 10, 10));
      mensagem.setSpacing(10);
      mensagem.setFillHeight(true);

      Circle perfilAutor = new Circle();
      perfilAutor.setRadius(20);
      perfilAutor.setStrokeWidth(0);
      perfilAutor.setFill(new ImagePattern(m.getAutor().getPerfil()));

      VBox conteudoMensagem = new VBox();
      conteudoMensagem.setAlignment((ehUsuario) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
      conteudoMensagem.setPadding(new Insets(5, 5, 5, 5));
      conteudoMensagem.setSpacing(5);
      conteudoMensagem.setMinWidth(VBox.USE_COMPUTED_SIZE);
      conteudoMensagem.setPrefWidth(VBox.USE_COMPUTED_SIZE);
      conteudoMensagem.setMaxWidth(400);

      Label autor = new Label((m.getAutor().getNome() != null && !m.getAutor().getNome().isEmpty()) ? m.getAutor().getNome() : "");
      autor.setFont(Font.font("Calibri", FontWeight.BOLD, 17));
      autor.setTextFill(Color.WHITE); 

      Label horario = new Label(m.formatarTempoEnvio());
      horario.setFont(Font.font("Calibri", 14));
      horario.setTextFill(Color.WHITE);

      StackPane painelMensagem = new StackPane();
      String estiloCss = (ehUsuario) ? "-fx-background-color: #088924; -fx-background-radius: 15px; -fx-padding: 8px" :
                                       "-fx-background-color: #2c332e; -fx-background-radius: 15px; -fx-padding: 8px";
      painelMensagem.setStyle(estiloCss);
      painelMensagem.setMinHeight(StackPane.USE_COMPUTED_SIZE);
      painelMensagem.setPrefHeight(StackPane.USE_COMPUTED_SIZE);

      Label texto = new Label(m.getTexto());
      texto.setFont(Font.font("Calibri", 15));
      texto.setMaxWidth(380);
      texto.setTextFill(Color.WHITE);
      texto.setWrapText(true);
      texto.setMinHeight(Label.USE_PREF_SIZE);

      painelMensagem.getChildren().add(texto);
      StackPane.setAlignment(texto, Pos.CENTER_LEFT);

      conteudoMensagem.getChildren().addAll(autor, painelMensagem, horario);
      
      mensagem.getChildren().addAll(perfilAutor, conteudoMensagem);
      listaMensagens.getChildren().add(mensagem);
    }

    TelaPrincipalController.principal.carregarGrupos();
  }
}
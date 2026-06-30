/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 29/06/2026
* Nome.............: TelaGrupoController
* Funcao...........: Classe que controla os eventos da TelaGrupo.
                     
*************************************************************** */

package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
  @FXML private ScrollPane barraRolagem;
  @FXML private TextField txtMensagem;
  @FXML private VBox listaMensagens;

  // Variaveis e instancias
  private static final Image SENT = new Image(TelaGrupoController.class.getResource("/img/Sent.png").toExternalForm());
  public static volatile TelaGrupoController grupos;
  private Grupo g;

  /*
   * ***************************************************************
   * Metodo: initialize
   * Funcao: executa um conjunto de instrucoes durante a inicializacao da aplicacao
   * Parametros: URL url: endereco do programa
                 ResourceBundle rb: recursos para inicializacao
   * Retorno: void
   ****************************************************************/
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // Inicializa uma instancia volatil do controller
    grupos = this;
    
    // Ao apertar ENTER, o usuario envia uma mensagem
    txtMensagem.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        try {
          enviarMensagem(new ActionEvent());
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }); 
  }

  /*
   * ***************************************************************
   * Metodo: sairChat
   * Funcao: fecha o chat do grupo
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/
  
  @FXML
  private void sairChat(ActionEvent event) {
    // Desmarca o grupo como selecionado
    g.setSelected(false);

    // Recarrega o chat vazio
    TelaPrincipalController.principal.recarregarComponentes();
  } 

  /*
   * ***************************************************************
   * Metodo: sairGrupo
   * Funcao: remove o usuario do grupo
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void sairGrupo(ActionEvent event) throws IOException {
    // Remove o grupo da lista de grupos do usuario
    Usuario.getUsuario().getGrupos().remove(g);

    // Envia uma APDU LEAVE para o servidor solicitando a remocao do usuario do grupo
    Usuario.getUsuario().getTCP().enviarAPDU(new APDU("LEAVE", Usuario.getUsuario().getNome(), g.getNome()));

    // Fecha o chat
    sairChat(event);
  }

  /*
   * ***************************************************************
   * Metodo: enviarMensagem
   * Funcao: envia uma mensagem no chat
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void enviarMensagem(ActionEvent event) throws IOException {
    // Interrompe o metodo se a mensagem estiver vazia
    if (txtMensagem.getText().isEmpty()) return;
    
    // Cria uma nova mensagem a partir do conteudo no txtMensagem e o nome do usuario
    String mensagem = txtMensagem.getText();
    String autor = Usuario.getUsuario().getNome();
    Mensagem m = new Mensagem(mensagem, autor, SENT);

    // Limpa a caixa de texto
    txtMensagem.clear();

    // Adiciona a mensagem na lista de mensagens do grupo
    g.adicionarMensagem(m);

    // Envia uma APDU SEND para que a mensagem seja comunicada para todos os outros usuarios do grupo
    // pelo servidor UDP
    Usuario.getUsuario().getUDP().enviarAPDU(new APDU("SEND", m.getAutor(), g.getNome(), m.getTexto(), m.getTempoEnvio()));

    // Recarrega as mensagens
    carregarMensagens();
  }

  /*
   * ***************************************************************
   * Metodo: carregarGrupo
   * Funcao: carrega as informacoes do grupo no chat
   * Parametros: Grupo g - grupo a ser carregado
   * Retorno: void
   ****************************************************************/

  public void carregarGrupo(Grupo g) {
    // Inicializa a instancia do grupo
    this.g = g;

    // Carrega o nome do grupo na interface
    lblNomeGrupo.setText(this.g.getNome());

    // Carrega as mensagens   
    carregarMensagens();
  }  

  /*
   * ***************************************************************
   * Metodo: carregarMensagens
   * Funcao: carrega as mensagens enviadas no grupo dentro do chat
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  public synchronized void carregarMensagens() {
    // Obtem a lista de mensagens do grupo atual
    ArrayList<Mensagem> mensagens = g.getMensagens();

    // Interrompe o metodo se a lista de mensagens for nula
    if (mensagens == null) return;

    // Esvazia a VBox para reorganizar o chat
    if (!listaMensagens.getChildren().isEmpty()) listaMensagens.getChildren().clear();

    // Ordena a lista de mensagens pelo tempo de envio (em ordem crescente)
    if (!mensagens.isEmpty()) mensagens.sort(Comparator.comparing(Mensagem::getTempoEnvio));

    // Confirma a leitura das mensagens que nao foram vistas
    confirmarLeitura();

    // Inicio do bloco for
    for (Mensagem m : mensagens) {
      // Verifica se a mensagem atual eh do usuario
      boolean ehUsuario = (m.getAutor() != null && m.getAutor().equals(Usuario.getUsuario().getNome()));

      // Configura a HBox da mensagem
      HBox mensagem = new HBox();
      mensagem.setAlignment((ehUsuario) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
      mensagem.setPadding(new Insets(10, 10, 10, 10));
      mensagem.setSpacing(10);
      mensagem.setFillHeight(true);

      // Configura a VBox com o conteudo da mensagem
      VBox conteudoMensagem = new VBox();
      conteudoMensagem.setAlignment((ehUsuario) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
      conteudoMensagem.setPadding(new Insets(5, 5, 5, 5));
      conteudoMensagem.setSpacing(5);
      conteudoMensagem.setMinWidth(VBox.USE_COMPUTED_SIZE);
      conteudoMensagem.setPrefWidth(VBox.USE_COMPUTED_SIZE);
      conteudoMensagem.setMaxWidth(400);

      // Label que informa o autor da mensagem
      Label autor = new Label((m.getAutor() != null && !m.getAutor().isEmpty()) ? m.getAutor() : "");
      autor.setFont(Font.font("Calibri", FontWeight.BOLD, 17));
      autor.setTextFill(Color.WHITE); 

      // Label que informa o horario que a mensagem foi enviada
      Label horario = new Label(m.formatarTempoEnvio());
      horario.setFont(Font.font("Calibri", 14));
      horario.setTextFill(Color.WHITE);

      // Painel que guarda o texto da mensagem
      StackPane painelMensagem = new StackPane();
      String estiloCss = (ehUsuario) ? "-fx-background-color: #088924; -fx-background-radius: 15px; -fx-padding: 8px" :
                                       "-fx-background-color: #2c332e; -fx-background-radius: 15px; -fx-padding: 8px";
      painelMensagem.setStyle(estiloCss);
      painelMensagem.setMinHeight(StackPane.USE_COMPUTED_SIZE);
      painelMensagem.setPrefHeight(StackPane.USE_COMPUTED_SIZE);

      // Label contendo o texto da mensagem
      Label texto = new Label(m.getTexto());
      texto.setFont(Font.font("Calibri", 15));
      texto.setMaxWidth(380);
      texto.setTextFill(Color.WHITE);
      texto.setWrapText(true);
      texto.setMinHeight(Label.USE_PREF_SIZE);

      // Adiciona o texto dentro do painel e o alinha na esquerda ao centro
      painelMensagem.getChildren().add(texto);
      StackPane.setAlignment(texto, Pos.CENTER_LEFT);

      HBox caixaStatus = new HBox();
      caixaStatus.setPadding(new Insets(3, 3, 3, 3));
      caixaStatus.setSpacing(5);
      caixaStatus.getChildren().add(horario);
      caixaStatus.setAlignment(ehUsuario ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

      // Imagem que representa o status da mensagem (via APDU CONFIRM)
      ImageView imgStatus = new ImageView();
      imgStatus.setFitWidth(17);
      imgStatus.setFitHeight(17);
      imgStatus.setPreserveRatio(true);
      imgStatus.setSmooth(true);

      // Obtem o status da mensagem
      Image status = m.getStatus();

      // Inicio do bloco if
      // Se a mensagem for do usuario e o status nao for nulo
      if (ehUsuario && status != null) {
        // Carrega a imagem do status
        imgStatus.setImage(status);

        // Adiciona a imagem dentro da caixa de status
        caixaStatus.getChildren().add(imgStatus);
      } // Fim do bloco if

      // Adiciona os itens dentro da VBox com o conteudo da mensagem
      conteudoMensagem.getChildren().addAll(autor, painelMensagem, caixaStatus);
      
      // Adiciona o conteudo da mensagem dentro da HBox 
      mensagem.getChildren().add(conteudoMensagem);

      // Adiciona a mensagem dentro da lista de mensagens
      listaMensagens.getChildren().add(mensagem);
    } // Fim do bloco for

    // Obtem a ultima mensagem
    Mensagem ultimaMensagem = g.obterUltimaMensagem();

    // Inicio do bloco if
    // Se a ultima mensagem nao for nula
    if (ultimaMensagem != null) {
      // Verifica se a ultima mensagem foi do usuario
      boolean ehUsuario = ultimaMensagem.getAutor().equals(Usuario.getUsuario().getNome());

      // Inicio do bloco if
      if (ehUsuario) {
        // Joga o chat pra baixo se a ultima mensagem foi enviada pelo usuario
        Platform.runLater(() -> barraRolagem.setVvalue(barraRolagem.getVmax()));
      } // Fim do bloco if
    } // Fim do bloco if

    // Recarrega a lista de grupos na TelaPrincipal
    TelaPrincipalController.principal.carregarGrupos();
  }

  /*
   * ***************************************************************
   * Metodo: confirmarLeitura
   * Funcao: confirma a leitura de mensagens nao vistas
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void confirmarLeitura() {
    // Interrompe o metodo se o grupo ou a lista de mensagens forem nulos
    if (g == null || g.getMensagens() == null) return;

    // Obtem a lista de mensagens do grupo
    ArrayList<Mensagem> mensagens = new ArrayList<>(g.getMensagens());

    // Inicio do bloco for
    for (Mensagem m : mensagens) {
      // Verifica se a mensagem atual eh do usuario
      boolean ehUsuario = (m.getAutor() != null && m.getAutor().equals(Usuario.getUsuario().getNome()));

      // Verifica se a mensagem ja foi lida
      boolean read = m.isRead();

      // Inicio do bloco if
      // Se a mensagem nao for do usuario e nao tiver sido lida anteriormente
      if (!ehUsuario && !read) {
        // Marca a mensagem como lida
        m.setRead(true);

        // Envia uma APDU CONFIRM notificando que a mensagem foi lida
        Usuario.getUsuario().getUDP().enviarAPDU(new APDU("CONFIRM", m.getAutor(), g.getNome(), m.getTexto(), m.getTempoEnvio(), "READ"));
      } // Fim do bloco if
    } // Fim do bloco for
  }
}
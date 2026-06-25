/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 25/06/2026
* Nome.............: TelaPrincipalController
* Funcao...........: Classe que controla os eventos da TelaPrincipal.
                     
*************************************************************** */

package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	private ArrayList<Node> componentesAntigos = new ArrayList<>(); 
	private static final String GRUPO_VAZIO = "Insira o nome do grupo.";
	private static final String GRUPO_EXISTENTE = "Voce ja entrou neste grupo.";
	private static final Image semFoto = new Image(TelaMenuController.class.getResource("/img/SemFoto.png").toExternalForm());
	private Image perfilGrupo;
	public static volatile TelaPrincipalController principal;

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
		// Salva os componentes do painel de chat vazio (caso nenhum grupo tiver sido aberto)
		componentesAntigos.addAll(painelChat.getChildren());

    // Adiciona um listener a caixa de texto do nome do grupo que age quando o usuario
    // insere um novo texto
		txtGrupo.textProperty().addListener((obs, oldValue, newValue) -> {
			if (txtGrupo.getText().isEmpty() && !newValue.isEmpty()) {
				txtGrupo.setStyle("-fx-background-color: #d9d9d9; -fx-background-radius: 15px; -fx-padding: 8px; -fx-prompt-text-fill: #3a3d3a");
			}
		});

    // Carrega a instancia volatil do controller
		principal = this;

		// Carrega as informacoes do usuario
		carregarInformacoes();

    // Coloca o TCP para escutar solicitacoes de JOIN e LEAVE
		Usuario.getUsuario().getTCP().receber();
	}

  /*
   * ***************************************************************
   * Metodo: abrirChat
   * Funcao: abre a tela de chat ao selecionar um grupo
   * Parametros: HBox caixa - caixa clicada
                 Grupo g - grupo selecionado
                 MouseEvent event - evento gerado ao clicar na caixa
   * Retorno: void
   ****************************************************************/

	@FXML
	private void abrirChat(HBox caixa, Grupo g, MouseEvent event) {
		// Interrompe o metodo se o grupo ja estiver aberto		
		if (g.isSelected()) return;

    // Marca o grupo como selecionado e colore a caixa
		g.setSelected(true);
		caixa.setStyle("-fx-background-color: #969696");

    // Desmarca todos os grupos que nao estiverem abertos
		for (Grupo grupo : grupos) {
			if (!grupo.getNome().equals(g.getNome())) {
				grupo.setSelected(false);
			}
		}

    // Descolore os botoes dos grupos para fins de demarcacao
		for (Node filho : listaGrupos.getChildren()) {
			if (filho instanceof HBox && !filho.equals(caixa)) {
				HBox caixaAtual = (HBox) filho;
				caixaAtual.setStyle("-fx-background-color: transparent");
			}
		}

    // Inicio do bloco try/catch
		try {
			// Carrega a tela de chat
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaGrupo.fxml"));
			Parent root = loader.load();

      // Carrega o grupo na tela de chat
			TelaGrupoController chat = loader.getController();
			chat.carregarGrupo(g);

      // Esvazia o painel e carrega os elementos da tela de chat
			painelChat.getChildren().clear();
			painelChat.getChildren().add(root);
		}
		catch (IOException e) {
			// Em caso de excecao, sua origem eh rastreada no console
			e.printStackTrace();
		} // Fim do bloco try/catch
	}

  
  /*
   * ***************************************************************
   * Metodo: juntarGrupo
   * Funcao: exibe a janela de criacao de grupos
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

	@FXML
	private void juntarGrupo(ActionEvent event) {
		// Carrega o perfil e a caixa de texto contendo o nome do grupo
		perfilGrupo = semFoto;
		imgGrupo.setFill(new ImagePattern(semFoto));
		txtGrupo.clear();

		// Exibe a janela de adicionar grupos
		painelJuntarGrupo.setVisible(true);
	}

  /*
   * ***************************************************************
   * Metodo: entrarGrupo
   * Funcao: entra em um novo grupo
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

	@FXML
	private void entrarGrupo(ActionEvent event) throws IOException {
		// Obtem o nome do grupo a ser criado
		String nomeGrupo = txtGrupo.getText();

    // Inicio do bloco if/else if
    // Se o usuario nao tiver inserido nenhum nome para o grupo
		if (nomeGrupo.isEmpty()) {
			// Exibe um aviso ao usuario e interrompe o metodo
			txtGrupo.setStyle("-fx-background-color: #D9D9D9; -fx-padding: 8px; -fx-background-radius: 15px; -fx-prompt-text-fill: #3a3d3a; -fx-border-color: #ff0000; -fx-border-radius: 15px; -fx-border-width: 2px");
			carregarAviso(GRUPO_VAZIO);
			return; 
		}
		else if (novoGrupoExiste(nomeGrupo)) {
			// Porem se o usuario ja tiver entrado no grupo, ele tambem sera notificado disso
			carregarAviso(GRUPO_EXISTENTE);
			return;
		} // Fim do bloco if/else if

    // Cria um novo grupo, adiciona a lista de grupos e oculta o painel
		Grupo g = new Grupo(perfilGrupo, nomeGrupo);
		grupos.add(g);
		painelJuntarGrupo.setVisible(false);

		byte[] perfil = Files.readAllBytes(Paths.get(Usuario.getUsuario().getCaminhoImagem()));

		// Envia uma nova APDU JOIN para o servidor TCP
		Usuario.getUsuario().getTCP().enviarAPDU(new APDU("JOIN", Usuario.getUsuario().getNome(), g.getNome(), perfil, Usuario.getUsuario().getIpServidor()));

    // Recarrega os grupos
		carregarGrupos();
	}
 
  /*
   * ***************************************************************
   * Metodo: mudarImagem
   * Funcao: altera a imagem do perfil do grupo
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

	@FXML
	private void mudarImagem(ActionEvent event) {
		// Abre uma janela para que o usuario possa escolher uma imagem
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));
		File f = fc.showOpenDialog(new Stage());

    // Inicio do bloco if
		if (f != null) {
			// Importa a imagem para o perfil do grupo caso ela estiver
			// sido escolhida pelo usuario
			String caminhoImagem = f.toURI().toString();
			perfilGrupo = new Image(caminhoImagem);
			imgGrupo.setFill(new ImagePattern(perfilGrupo));
		} // Fim do bloco if
	}

  /*
   * ***************************************************************
   * Metodo: fecharJanelaGrupo
   * Funcao: oculta o painel de criacao de grupos
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

	@FXML
	private void fecharJanelaGrupo(ActionEvent event) {
		painelJuntarGrupo.setVisible(false);
	}

  /*
   * ***************************************************************
   * Metodo: encerrarSessao
   * Funcao: encerra a sessao do usuario e retorna a tela de login
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

	@FXML
	private void encerrarSessao(ActionEvent event) throws IOException {
		// Carrega o menu principal
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaMenu.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);

    // Carrega a nova cena na janela atual
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
	}

  /*
   * ***************************************************************
   * Metodo: ocultarAviso
   * Funcao: oculta o painel de aviso
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

	@FXML
	private void ocultarAviso(ActionEvent event) {
		painelAviso.setVisible(false);
	}	

  /*
   * ***************************************************************
   * Metodo: novoGrupoExiste
   * Funcao: verifica se ja existe um grupo com o nome especificado
   * Parametros: String nome - nome do grupo
   * Retorno: boolean
   ****************************************************************/

	private boolean novoGrupoExiste(String nome) {
		// Inicio do bloco for
		for (Grupo g : grupos) {
			// Inicio do bloco if
			if (g.getNome().equals(nome)) {
				// Retorna verdadeiro caso haver algum grupo
				// com o mesmo nome
				return true;
			} // Fim do bloco if
		} // Fim do bloco for

    // Retorna falso caso nao encontrar nenhuma correspondencia
		return false;
	}

  /*
   * ***************************************************************
   * Metodo: carregarInformacoes
   * Funcao: carrega as informacoes do usuario na interface
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	private void carregarInformacoes() {
		// Carrega as informacoes basicas do usuario (nome, ip do servidor e perfil)
		lblUsuario.setText(Usuario.getUsuario().getNome());
		lblIpServidor.setText("Servidor: " + Usuario.getUsuario().getIpServidor());
		imgPerfil.setFill(new ImagePattern(Usuario.getUsuario().getPerfil()));

    // Carrega os grupos
		carregarGrupos();
	}

  /*
   * ***************************************************************
   * Metodo: carregarGrupos
   * Funcao: carrega os grupos existentes do usuario
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public synchronized void carregarGrupos() {
		// Obtem os grupos do usuario		
		grupos = Usuario.getUsuario().getGrupos();

		// Interompe o metodo caso o usuario nao for parte de nenhum grupo
		if (grupos == null) return;

		// Limpa a lista de grupos caso o usuario ja estiver em algum grupo para evitar desorganizacao
		if (!listaGrupos.getChildren().isEmpty()) listaGrupos.getChildren().clear();

    // Inicio do bloco for
		for (Grupo g : grupos) {
			// Configura o botao do grupo correspondente 
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

      // Cria a imagem do perfil do grupo
			Circle imagemGrupo = new Circle();
			imagemGrupo.setRadius(25);
			imagemGrupo.setStrokeWidth(0);
			imagemGrupo.setFill(new ImagePattern(g.getPerfilGrupo()));

      // Cria a caixa contendo as informacoes do grupo (nome e mensagem mais recente)
			VBox infoGrupo = new VBox();
			infoGrupo.setAlignment(Pos.CENTER_LEFT);
			infoGrupo.setPrefWidth(167);
			infoGrupo.setPrefHeight(67);
			infoGrupo.setSpacing(3);

      // Insere o nome do grupo
			Label nomeGrupo = new Label(g.getNome());
			nomeGrupo.setFont(Font.font("Calibri", FontWeight.BOLD, 14));
			nomeGrupo.setTextFill(Color.WHITE);

      // Obtem a mensagem mais recente (se houver) e verifica se ela ja foi definida e ela corresponde a uma
      // mensagem enviada pelo usuario
			Mensagem ultMsg = g.obterUltimaMensagem();
			boolean mensagemUsuario = (ultMsg != null && ultMsg.getAutor().getNome().equals(Usuario.getUsuario().getNome()));

      // Obtem o autor da mensagem e carrega o texto contendo a mensagem mais recente
			String autor = (mensagemUsuario) ? "Voce: " : ((ultMsg != null) ?  ultMsg.getAutor().getNome() + ": " : "");
			Label ultimaMensagem = new Label((ultMsg != null && !ultMsg.getTexto().isEmpty()) ? autor + ultMsg.getTexto() : "");
			ultimaMensagem.setFont(Font.font("Calibri", 12));
			ultimaMensagem.setTextFill(Color.web("#dfe1e6"));

      // Adiciona todas as Labels na caixa de informacoes
			infoGrupo.getChildren().addAll(nomeGrupo, ultimaMensagem);

      // Adiciona a imagem e as informacoes dentro do botao do grupo
			grupo.getChildren().addAll(imagemGrupo, infoGrupo);

      // Importa o evento que abre o chat do grupo 
      // caso o usuario clicar no botao
			grupo.setOnMouseClicked(event -> {
				abrirChat(grupo, g, event);
			});

      // Adiciona o grupo na lista de grupos
			listaGrupos.getChildren().add(grupo);
		} // Fim do bloco for
	}

  /*
   * ***************************************************************
   * Metodo: recarregarComponentes
   * Funcao: recarrega o painel de chat vazio
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void recarregarComponentes() {
		// Limpa a janela e recarrega os componentes do chat vazio
		painelChat.getChildren().clear();
		painelChat.getChildren().addAll(componentesAntigos);

    // Recarrega os grupos
		carregarGrupos();
	}

  /*
   * ***************************************************************
   * Metodo: carregarAviso
   * Funcao: exibe o painel de aviso na tela do usuario
   * Parametros: String aviso - aviso a ser exibido
   * Retorno: void
   ****************************************************************/

	private void carregarAviso(String aviso) {
		lblAviso.setText(aviso);
		painelAviso.setVisible(true);
	}
}
/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 02/07/2026
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
	@FXML private Button btnEntrarGrupo;
	@FXML private Button btnFecharGrupo;
	@FXML private Button btnOk;
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

    // Caso o usuario apertar ENTER enquanto estiver interagindo com o txtGrupo,
    // o evento de entrar grupo eh executado
		txtGrupo.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				try {
					entrarGrupo(new ActionEvent());
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		});	

    // Carrega a instancia volatil do controller
		principal = this;

		// Carrega as informacoes do usuario
		carregarInformacoes();
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
		// Esvazia qualquer texto escrito no nome do grupo
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
		Grupo g = new Grupo(nomeGrupo);
		grupos.add(g);
		painelJuntarGrupo.setVisible(false);

		// Envia uma nova APDU JOIN para o servidor TCP
		Usuario.getUsuario().getTCP().enviarAPDU(new APDU("JOIN", Usuario.getUsuario().getNome(), g.getNome()));

    // Recarrega os grupos
		carregarGrupos();
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
			boolean mensagemUsuario = (ultMsg != null && ultMsg.getAutor().equals(Usuario.getUsuario().getNome()));

      // Obtem o autor da mensagem e carrega o texto contendo a mensagem mais recente
			String autor = (mensagemUsuario) ? "Voce: " : ((ultMsg != null) ?  ultMsg.getAutor() + ": " : "");
			Label ultimaMensagem = new Label((ultMsg != null && !ultMsg.getTexto().isEmpty()) ? autor + ultMsg.getTexto() : "");
			ultimaMensagem.setFont(Font.font("Calibri", 12));
			ultimaMensagem.setTextFill(Color.web("#dfe1e6"));

      // Adiciona todas as Labels na caixa de informacoes
			infoGrupo.getChildren().addAll(nomeGrupo, ultimaMensagem);

      // Gera a Label informando a hora em que foi enviada a ultima mensagem
			Label envioUltimaMensagem = new Label((ultMsg != null) ? ultMsg.formatarHora() : "");
			envioUltimaMensagem.setFont(Font.font("Calibri", 14));
			envioUltimaMensagem.setTextFill(Color.WHITE);

      // Adiciona a imagem e as informacoes dentro do botao do grupo
			grupo.getChildren().addAll(infoGrupo, envioUltimaMensagem);

			// Inicio do bloco if
			// Se o grupo possuir mensagens que nao foram lidas
			if (verificarMensagensNaoLidas(g)) {
				// Cria um circulo para sinalizar o recebimento de mensagens
				// nao lidas
				Circle sinal = new Circle();
				sinal.setRadius(10);
				sinal.setStrokeWidth(0);
				sinal.setFill(Color.web("#02c72d"));

				// Calcula a quantidade de mensagens nao lidas e a converte em texto
				int quantidade = contarMensagensNaoLidas(g);
				String textoQuantidade = (quantidade > 99) ? "99+" : Integer.toString(quantidade);

        // Cria uma Label armazenando a quantidade de mensagens nao lidas
				Label lblNumero = new Label(textoQuantidade);
				lblNumero.setFont(Font.font("Calibri", FontWeight.BOLD, 10));
				lblNumero.setTextFill(Color.WHITE);

				// Cria uma StackPane para representar a "badge" de mensagens nao lidas
				StackPane badge = new StackPane();
				badge.setAlignment(Pos.CENTER);
				badge.getChildren().addAll(sinal, lblNumero);

				// Adiciona a badge dentro do grupo
				grupo.getChildren().add(badge);
			} // Fim do bloco if

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

	/*
   * ***************************************************************
   * Metodo: verificarMensagensNaoLidas
   * Funcao: verifica se um grupo possui mensagens que nao foram lidas
             pelo usuario
   * Parametros: Grupo g - grupo a ser verificado
   * Retorno: boolean
   ****************************************************************/

	private boolean verificarMensagensNaoLidas(Grupo g) {
		// Obtem a lista de mensagens do grupo
		ArrayList<Mensagem> mensagens = g.getMensagens();

		// Retorna falso se a lista de mensagens nao tiver sido inicializada
		if (mensagens == null) return false;

		// Inicio do bloco for
		for (Mensagem m : mensagens) {
			// Verifica se a autoria da mensagem atual eh de outro usuario
			boolean ehUsuario = (m.getAutor() != null && m.getAutor().equals(Usuario.getUsuario().getNome()));

			// Retorna verdadeiro se encontrar uma mensagem que nao foi escrita e lida pelo usuario
			if (!ehUsuario && !m.isRead()) return true;
		} // Fim do bloco for

		// Retorna falso caso nenhuma mensagem nao lida tiver sido descoberta
		return false;
	}

	/*
   * ***************************************************************
   * Metodo: contarMensagensNaoLidas
   * Funcao: calcula a quantidade de mensagens nao lidas pelo usuario
             em um determinado grupo
   * Parametros: Grupo g - grupo a ser verificado
   * Retorno: int
   ****************************************************************/

	private int contarMensagensNaoLidas(Grupo g) {
		// Obtem a lista de mensagens do grupo
		ArrayList<Mensagem> mensagens = g.getMensagens();

		// Retorna zero se a lista de mensagens nao tiver sido inicializada
		if (mensagens == null) return 0;

		// Inicializa o contador de mensagens nao lidas
		int contador = 0;

		// Inicio do bloco for
		for (Mensagem m : mensagens) {
			// Verifica se a autoria da mensagem atual eh de outro usuario
			boolean ehUsuario = (m.getAutor() != null && m.getAutor().equals(Usuario.getUsuario().getNome()));

			// Incrementa o contador sempre que encontrar uma mensagem que nao foi escrita e lida pelo usuario
			if (!ehUsuario && !m.isRead()) contador++;
		} // Fim do bloco for

		// Retorna a quantidade calculada
		return contador;
	}
}
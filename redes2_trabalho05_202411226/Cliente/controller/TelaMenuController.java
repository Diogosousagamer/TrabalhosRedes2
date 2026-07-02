/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 02/07/2026
* Nome.............: TelaMenuController
* Funcao...........: Classe que controla os eventos da TelaMenu.
                     
*************************************************************** */

package controller;

import java.io.File;
import java.io.IOException;
import java.lang.Thread;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.APDU;
import model.Usuario;

public class TelaMenuController implements Initializable {
	// Componentes da interface
	@FXML private AnchorPane painelAviso;
	@FXML private Button btnEntrar;
	@FXML private Button btnOk;
	@FXML private Label lblAviso;
	@FXML private TextField txtNomeUsuario;
	@FXML private TextField txtIpServidor;

  // Variaveis e instancias
	private final String DADOS_VAZIOS = "Preencha todos os dados obrigatorios antes de prosseguir.";
	private final String NAO_CONECTADO = "Nao conectado. Tente novamente mais tarde.";
	private boolean usuarioVazio;
	private boolean ipVazio;

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
		// Listener no txtNomeUsuario que reage ao texto digitado
		txtNomeUsuario.textProperty().addListener((obs, oldValue, newValue) -> {
			if (usuarioVazio && !newValue.isEmpty()) {
				txtNomeUsuario.setStyle("-fx-background-color: #d9d9d9; -fx-background-radius: 15px; -fx-padding: 8px");
				usuarioVazio = false;
			}
		});
		
		// Listener no txtIpServidor que reage ao texto digitado
		txtIpServidor.textProperty().addListener((obs, oldValue, newValue) -> {
			if (ipVazio && !newValue.isEmpty()) {
				txtIpServidor.setStyle("-fx-background-color: #d9d9d9; -fx-background-radius: 15px; -fx-padding: 8px");
				if (ipVazio) ipVazio = false;
			}
		});

    // Caso o usuario apertar ENTER enquanto estiver interagindo com o txtNomeUsuario,
    // o evento de entrar no sistema eh executado
		txtNomeUsuario.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				entrar(new ActionEvent(txtNomeUsuario, null));
			}
		});	

    // Caso o usuario apertar ENTER enquanto estiver interagindo com o txtIpServidor,
    // o evento de entrar no sistema eh executado
		txtIpServidor.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				entrar(new ActionEvent(txtIpServidor, null));
			}
		});	
	}

  /*
   * ***************************************************************
   * Metodo: entrar
   * Funcao: loga o usuario na aplicacao
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

	@FXML
	private void entrar(ActionEvent event) {
		// Interrompe o processo se algum dado estiver faltando
		if (!verificarDados()) return;

    // Obtem o nome e o ip inseridos
		String nome = txtNomeUsuario.getText().trim();
		String ip = txtIpServidor.getText().trim();

		// Abre uma Thread para executar a conexao com o usuario
		new Thread(() -> {
	    // Tenta conectar o usuario enviando uma APDU solicitando resposta do servidor
			boolean conectado = Usuario.conectarUsuario(nome, ip);

			// Inicio do bloco Platform.runLater
			Platform.runLater(() -> {
		    // Inicio do bloco if/else
		    // Se o usuario tiver sido conectado
				if (conectado) {
					// Inicio do bloco try/catch
					try {
						// Carrega a tela principal
						FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaPrincipal.fxml"));
						Parent root = loader.load();
						Scene scene = new Scene(root);

			      // Importa ela para dentro da janela
						Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
						stage.setScene(scene);
					}
					catch (IOException e) {
						// Em caso de excecao, emite a pilha de execucao
						// para rastrear a sua origem
						e.printStackTrace();
					} // Fim do bloco try/catch
				}
				else {
					// Senao, eh exibida uma mensagem de erro
					carregarAviso("Nao conectado. Tente novamente mais tarde.");
				} // Fim do bloco if/else
			}); // Fim do bloco Platform.runLater
		}).start();
	}

  /*
   * ***************************************************************
   * Metodo: ocultarAviso
   * Funcao: oculta o painel de aviso na tela
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

	@FXML
	private void ocultarAviso(ActionEvent event) {
		painelAviso.setVisible(false);
	}

  /*
   * ***************************************************************
   * Metodo: verificarDados
   * Funcao: verifica se todos os dados necessarios para conexao (nome de usuario
             e ip do servidor) foram preenchidos
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

	private boolean verificarDados() {
		// Obtem os dados e verifica se um deles esta vazio
		String nome = txtNomeUsuario.getText();
		String ip = txtIpServidor.getText();
		usuarioVazio = nome.isEmpty();
		ipVazio = ip.isEmpty();

    // Inicio do bloco if
    // Se um dos dados (ou ambos) estiver vazio
		if (usuarioVazio || ipVazio) {
			// Marca os componentes que estao vazios
			if (usuarioVazio) txtNomeUsuario.setStyle("-fx-background-color: #d9d9d9; -fx-background-radius: 15px; -fx-padding: 8px; -fx-border-color: #ff0000; -fx-border-radius: 15px; -fx-border-width: 2px");
			if (ipVazio) txtIpServidor.setStyle("-fx-background-color: #d9d9d9; -fx-background-radius: 15px; -fx-padding: 8px; -fx-border-color: #ff0000; -fx-border-radius: 15px; -fx-border-width: 2px");

			// Exibe um aviso
			carregarAviso(DADOS_VAZIOS);

      // Retorna falso
			return false;
		} // Fim do bloco if

    // Retorna verdadeiro caso nenhum dado estiver vazio
		return true;
	}

  /*
   * ***************************************************************
   * Metodo: carregarAviso
   * Funcao: exibe um aviso dentro do painel de aviso
   * Parametros: String aviso - aviso a ser exibido
   * Retorno: boolean
   ****************************************************************/

	private void carregarAviso(String aviso) {
		// Carrega o aviso na Label
		lblAviso.setText(aviso);

		// Exibe o painel de aviso
		painelAviso.setVisible(true);
	}
}
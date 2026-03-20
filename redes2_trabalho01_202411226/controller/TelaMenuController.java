/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 14/03/2026
* Ultima alteracao.: 20/03/2026
* Nome.............: TelaMenuController
* Funcao...........: Classe que controla os eventos da TelaMenu.
                     
*************************************************************** */

package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TelaMenuController implements Initializable {
	// Componentes da interface
	@FXML private ComboBox<String> cbVersao;
	@FXML private Label lblNota;
	@FXML private Button btnProsseguir;

  // Notas explicando as funcionalidades de cada versao do algoritmo
	private String[] notas = {
		"Nota: Cada pacote que chega em um roteador eh enviado para TODAS as interfaces de rede deste roteador.",
		"Nota: Cada pacote que chega em um roteador eh enviado para todas as interfaces de rede deste roteador, EXCETO por aquela pela qual ele chegou.",
		"Nota: Cada pacote que chega em um roteador eh enviado para todas as interfaces de rede deste roteador, exceto por aquela pela qual ele chegou. Cada roteador verifica a informacao de TTL para decidir se o pacote continua a circular na rede.",
		"Nota: Cada pacote que chega em um roteador eh enviado para todas as interfaces de rede deste roteador, exceto por aquela pela qual ele chegou. Cada roteador verifica a informacao de TTL para decidir se o pacote continua a circular na rede."
	};

  /*
   * ***************************************************************
   * Metodo: initialize
   * Funcao: executa um conjunto de instrucoes durante a inicializacao da aplicacao
   * Parametros: URL location: endereco do programa
   * ResourceBundle resources: recursos para inicializacao
   * Retorno: void
   ****************************************************************/

	@Override 
	public void initialize(URL url, ResourceBundle rb) {
		// Carrega as versoes do algoritmo existentes
		ObservableList<String> versoes = FXCollections.observableArrayList("1.0", "2.0", "3.0", "4.0");
		cbVersao.setItems(versoes);
		cbVersao.getSelectionModel().selectFirst();
		selecionarVersao(new ActionEvent());

		// Metodo que altera a cor do texto da comboBox
    cbVersao.setButtonCell(new ListCell<String>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        // O item e atualizado ao ser selecionado na comboBox
        super.updateItem(item, empty);

        // Inicio do bloco if/else
        if (empty || item == null) {
          // Define o texto como nulo
          // caso o item estiver vazio
          setText(null);
        } 
        else {
          // Caso contrario, o item e selecionado
          setText(item);

          // E a cor do texto e trocada para branco
          setTextFill(Color.web("#1b42b5"));
        } // Fim do bloco if/else
      }
    });
	}

	/*
   * ***************************************************************
   * Metodo: selecionarVersao
   * Funcao: seleciona a versao de um certo algoritmo
   * Parametros: ActionEvent event - evento gerado ao selecionar
                 alguma opcao da ComboBox
   * Retorno: void
   ****************************************************************/

	@FXML
	private void selecionarVersao(ActionEvent event) {
		// Obtem-se o indice da opcao
		int opcao = cbVersao.getSelectionModel().getSelectedIndex();
		lblNota.setText(notas[opcao]);
	}

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

    // Carrega o controller da TelaPrincipal para importar a versao selecionada
		TelaPrincipalController p = loader.getController();
		int versao = cbVersao.getSelectionModel().getSelectedIndex();
		p.configurar(versao);
		if (versao > 1) p.definirTempoDeVida(0);

    // Carrega a cena (tela) dentro da mesma janela
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
	}

  /*
   * ***************************************************************
   * Metodo: definirVersao
   * Funcao: define a versao selecionada anteriormente
   * Parametros: int versao - indice da versao selecionada anteriormente
   * Retorno: void
   ****************************************************************/

	public void definirVersao(int versao) {
		// A versao fica marcada dentro da ComboBox
		cbVersao.getSelectionModel().select(versao);
	}
}
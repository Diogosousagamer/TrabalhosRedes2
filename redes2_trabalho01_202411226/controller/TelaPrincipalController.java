/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 15/03/2026
* Ultima alteracao.: 30/08/2026
* Nome.............: TelaPrincipalController
* Funcao...........: Classe que controla os eventos da TelaPrincipal.
                     
*************************************************************** */


package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Pacote;
import model.Roteador;
import model.Grafo;

public class TelaPrincipalController implements Initializable {
	// Componentes da interface
  @FXML private AnchorPane painelReinicio;
  @FXML private AnchorPane subrede;
  @FXML private Button btnContinuar;
	@FXML private Button btnVoltar;
  @FXML private Label lblOrigem;
  @FXML private Label lblDestino;
	@FXML private Label lblPacotes;
  @FXML private Label lblResultados;
  @FXML private Label lblSelecao;

	// Variaveis e instancias
  private Grafo grafo;
  private volatile boolean simulacaoAtiva;
  public static volatile TelaPrincipalController controller;
	private Roteador origem;
  private Roteador destino;
	private int versao;
  private int tempoDeVida;

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
    // Carrega a instancia volatil do controller
    controller = this;

    // Carrega o grafo
    grafo = new Grafo(subrede, lblPacotes, this);
	}

  /*
   * ***************************************************************
   * Metodo: voltar
   * Funcao: volta para a tela de inicio
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

	@FXML
	private void voltar(ActionEvent event) throws IOException {
		// Carrega o arquivo FXML e gera uma nova cena
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TelaMenu.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);

    // Deixa a versao executada marcada no menu
		TelaMenuController m = loader.getController();
		m.definirVersao(this.versao);
    if (this.versao > 1) m.definirTTL(this.tempoDeVida);

    // Carrega a cena (tela) dentro da mesma janela
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
	}

  /*
   * ***************************************************************
   * Metodo: definirOrigemDestino
   * Funcao: define os roteadores de origem e destino ao clicar em dois nos
             presentes na subrede gerada via backbone
   * Parametros: MouseEvent event - evento gerado ao clicar no circulo
                 Circle c - circulo no qual o usuario clicou
   * Retorno: void
   ****************************************************************/

  @FXML
  public void definirOrigemDestino(MouseEvent event, Roteador r) {
    if (r == null) return;
    String nome = r.getNome();

    // Inicio do bloco if/else if/else if
    // Se um roteador ainda nao tiver sido definido como origem
    if (!grafo.existeOrigem()) {
      r.setOrigem(true);
      r.bloquearRoteador(true);
      r.alterarCorRoteador("#1fdb18");
      origem = r;

      // Exibe o rotulo do roteador na label
      lblOrigem.setText(origem.getNome());
    }
    else if (!grafo.existeDestino()) { // Porem se um destino nao tiver sido definido
                                                         // e a origem da rota tiver sido definida
      r.setDestino(true);
      r.alterarCorRoteador("#d60b18");
      destino = r;
      grafo.bloquearRoteadores(true);

      // Exibe o rotulo do roteador de destino na label
      lblDestino.setText(destino.getNome());

      // Oculta a label de selecao
      lblSelecao.setVisible(false);
 
      // Inicio do bloco if
      // Se o roteador de origem nao for nulo
      if (origem != null) {
        // Inicia a simulacao e gera o primeiro pacote
        simulacaoAtiva = true;
        gerarPacoteInicial(origem);
      } // Fim do bloco if
    }
  } 

  /*
   * ***************************************************************
   * Metodo: gerarPacoteInicial
   * Funcao: gera um pacote para iniciar a simulacao
   * Parametros: Roteador r - roteador onde o pacote iniciara
   * Retorno: void
   ****************************************************************/

  private void gerarPacoteInicial(Roteador r) {
    // Interompe o metodo se a simulacao nao estiver ativa
    if (!simulacaoAtiva) return;

    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Carrega uma instancia da imagem do envelope
      Image mail = new Image(getClass().getResource("/img/Envelope.png").toExternalForm());

      // Cria uma nova imagem para o pacote e a adiciona na sub rede e na lista de imagens criadas 
      ImageView envelope = new ImageView(mail);
      envelope.setFitWidth(41);
      envelope.setFitHeight(98);
      envelope.setLayoutX(r.getPosX());
      envelope.setLayoutY(r.getPosY());
      envelope.setVisible(true);
      envelope.setPreserveRatio(true);

      // Gera um novo pacote, define a sua posicao, o adiciona na lista de pacotes e incrementa a quantidade
      // de pacotes existentes
      Pacote p = criarPacote(envelope, r, r, null);
      p.definirPosicao();
      grafo.adicionarPacote(p);
    }); // Fim do bloco Platform.runLater
  }

  /*
   * ***************************************************************
   * Metodo: gerarMaisPacotes
   * Funcao: gera mais pacotes para dar continuidade a simulacao
   * Parametros: Roteador origem - roteador do qual o pacote se originou
                 Roteador destino - roteador para o qual o pacote sera encaminhado
                 Roteador vindoDe - roteador do qual o pacote veio (parametro usado para impedir que ele seja encaminhado
                 novamente para o roteador do qual veio na versao 2 do algoritmo de inundacao)
   * Retorno: void
   ****************************************************************/

  public void gerarMaisPacotes(Roteador origem, Roteador destino, Roteador vindoDe) {
    // Interompe o metodo se a simulacao nao estiver ativa
    if (!simulacaoAtiva) return;

    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Carrega uma instancia da imagem do envelope
      Image mail = new Image(getClass().getResource("/img/Envelope.png").toExternalForm());

      // Cria uma nova imagem para o pacote e a adiciona na sub rede e na lista de imagens criadas 
      ImageView envelope = new ImageView(mail);
      envelope.setFitWidth(41);
      envelope.setFitHeight(98);
      envelope.setLayoutX(origem.getPosX());
      envelope.setLayoutY(origem.getPosY());
      envelope.setVisible(true);
      envelope.setPreserveRatio(true);

      // Gera um novo pacote, define a sua posicao, o adiciona na lista de pacotes e incrementa a quantidade
      // de pacotes existentes
      Pacote p = criarPacote(envelope, origem, destino, vindoDe);
      p.definirPosicao();
      grafo.adicionarPacote(p);
    }); // Fim do bloco Platform.runLater
  }

  /*
   * ***************************************************************
   * Metodo: criarPacote
   * Funcao: cria uma nova instancia da classe Pacote
   * Parametros: ImageView envelope - imagem do pacote
                 Roteador origem - roteador do qual o pacote se originou
                 Roteador destino - roteador para o qual o pacote sera encaminhado
                 Roteador vindoDe - roteador do qual o pacote veio (parametro usado para impedir que ele seja encaminhado
                 novamente para o roteador do qual veio na versao 2 do algoritmo de inundacao)
   * Retorno: void
   ****************************************************************/

  private Pacote criarPacote(ImageView envelope, Roteador origem, Roteador destino, Roteador vindoDe) {
    // Inicio do bloco switch/case
    // O pacote sera gerado conforme a versao do algoritmo de inundacao selecionada pelo usuario
    switch (versao) {
      case 0: // Retona um pacote para a versao 1.0
        return new Pacote(envelope, this.versao, origem, destino);
      case 1: // Retorna um pacote para a versao 2.0 (inclui a linha de saida pela qual ele chegou)
        return new Pacote(envelope, this.versao, origem, destino, vindoDe);
      case 2: 
      case 3: // Retorna um pacote para a versao 3.0/4.0 (inclui a linha de saida pela qual ele chegou e o seu tempo de vida na rede)
        return new Pacote(envelope, this.versao, origem, destino, vindoDe, this.tempoDeVida);
    } // Fim do bloco switch/case

    // Retorna nulo caso nenhuma das opcoes for atendida
    return null;
  }

  /*
   * ***************************************************************
   * Metodo: interromper
   * Funcao: interrompe a simulacao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  public void interromper() {
    // Obtem o valor final da versao
    int vFinal = versao + 1;

    // Obtem a quantidade final de pacotes presentes na rede ate o encerramento da simulacao
    int nFinalPacotes = Integer.parseInt(lblPacotes.getText());

    // Modelo da mensagem exibida para o usuario
    String modelo = "Voce precisou de X pacotes para caminhar do roteador Y para o roteador Z com a versao W do algoritmo de inundacao.";

    // Define os resultados da simulacao substituindo os valores
    String resultados = modelo.replace("X", Integer.toString(nFinalPacotes))
                                                 .replace("Y", origem.getNome())
                                                 .replace("Z", destino.getNome())
                                                 .replace("W", Integer.toString(vFinal) + ".0");

    simulacaoAtiva = false;
    grafo.removerPacotes();

    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Exibe o painel contendo os resultados e o joga na camada de baixo
      // da AnchorPane para que ele nao seja sobreposto pelo grafo da rede
      painelReinicio.setVisible(true);
      painelReinicio.toFront();

      // Exibe os resultados da simulacao na label
      lblResultados.setText(resultados);
    }); // Fim do bloco Platform.runLater
  }

  /*
   * ***************************************************************
   * Metodo: continuar
   * Funcao: permite que o usuario prossiga com a simulacao
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void continuar(ActionEvent event) {
    // Oculta o painel exibido durante a interrupcao da simulacao
    painelReinicio.setVisible(false);

    // Esvazia a label de origem e de destino   
    lblOrigem.setText("");
    lblDestino.setText("");

    // Exibe a label de selecao
    lblSelecao.setVisible(true);

    origem = null;
    destino = null;

    grafo.reiniciarGrafo();
  }

  /*
   * ***************************************************************
   * Metodo: removerPacote
   * Funcao: remove um pacote da rede
   * Parametros: Pacote p - pacote a ser removido
   * Retorno: void
   ****************************************************************/

  public void removerPacote(Pacote p) {
    grafo.removerPacote(p);
  }

  /*
   * ***************************************************************
   * Metodo: configurar
   * Funcao: configura a simulacao
   * Parametros: int versao - indice da versao selecionada
   * Retorno: void
   ****************************************************************/

	public void configurar(int versao) {
		// Define a versao do algoritmo
		this.versao = versao;
	}

  /*
   * ***************************************************************
   * Metodo: definirTempoDeVida
   * Funcao: define o tempo de vida (TTL) de cada pacote dentro da rede
   * Parametros: int tempoDeVida - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void definirTempoDeVida(int tempoDeVida) {
    this.tempoDeVida = tempoDeVida;
  }
}
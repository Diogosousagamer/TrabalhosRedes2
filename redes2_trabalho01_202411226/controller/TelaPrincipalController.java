/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 15/03/2026
* Ultima alteracao.: 20/03/2026
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Host;
import model.Pacote;
import model.Roteador;

public class TelaPrincipalController implements Initializable {
	// Componentes da interface
  @FXML private AnchorPane subrede;
	@FXML private Button btnVoltar;
	@FXML private ComboBox<String> cbTransmissor;
	@FXML private Label lblPacotes;
  @FXML private AnchorPane painelReinicio;

	// Variaveis e instancias
  public static volatile TelaPrincipalController controller;
	private Host diogo;
	private Host gustavo;
  private Host hostTransmissor;
  private Host hostReceptor;
  private int quantidadeNos;
	private int versao;
	private int numPacotes;
	private int transmissor;
  private int tempoDeVida;
  private ArrayList<Pacote> pacotes;
  private ArrayList<ImageView> imagens;
  private ArrayList<Roteador> roteadores;
  private HashMap<String, Circle> nosCriados = new HashMap<>();
  private Set<String> arestasExistentes = new HashSet<>();
  private final Map<String, double[]> posicaoCirculos = new HashMap<>();
  private final Map<String, double[]> posicaoLabels = new HashMap<>();
  private final Map<String, double[]> posicaoRoteadores = new HashMap<>();

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
    // Preenchendo PosicaoCirculos
    posicaoCirculos.put("A", new double[]{60.0, 126.0});
    posicaoCirculos.put("B", new double[]{133.0, 52.0});
    posicaoCirculos.put("C", new double[]{133.0, 189.0});
    posicaoCirculos.put("D", new double[]{237.0, 126.0});
    posicaoCirculos.put("E", new double[]{330.0, 52.0});
    posicaoCirculos.put("F", new double[]{330.0, 189.0});
    posicaoCirculos.put("G", new double[]{400.0, 126.0});

    // Preenchendo PosicaoLabels
    posicaoLabels.put("A", new double[]{54.0, 142.0});
    posicaoLabels.put("B", new double[]{127.0, 20.0});
    posicaoLabels.put("C", new double[]{127.0, 206.0});
    posicaoLabels.put("D", new double[]{233.0, 96.0});
    posicaoLabels.put("E", new double[]{324.0, 20.0});
    posicaoLabels.put("F", new double[]{326.0, 206.0});
    posicaoLabels.put("G", new double[]{395.0, 145.0});

    // Preenchendo PosicaoRoteadores
    posicaoRoteadores.put("A", new double[]{40.0, 105.0});
    posicaoRoteadores.put("B", new double[]{113.0, 32.0});
    posicaoRoteadores.put("C", new double[]{114.0, 168.0});
    posicaoRoteadores.put("D", new double[]{217.0, 105.0});
    posicaoRoteadores.put("E", new double[]{310.0, 32.0});
    posicaoRoteadores.put("F", new double[]{310.0, 168.0});
    posicaoRoteadores.put("G", new double[]{380.0, 105.0});

		// Metodo que altera a cor do texto da comboBox
    cbTransmissor.setButtonCell(new ListCell<String>() {
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

    // Carrega os hosts e suas respetivas posicoes
    diogo = new Host(-10, 105);
    gustavo = new Host(430, 105);

    roteadores = new ArrayList<>();

    // Carrega as ArrayLists que armazenarao os pacotes e suas respectivas imagens
    pacotes = new ArrayList<>();
    imagens = new ArrayList<>();

    // Carrega a instancia volatil do controller
    controller = this;

    // Configura a subrede
    configurarSubrede();

    // Carrega a combo box contendo as opcoes para o host transmissor
    ObservableList<String> hosts = FXCollections.observableArrayList("Diogo", "Gustavo");
    cbTransmissor.setItems(hosts);
    cbTransmissor.getSelectionModel().selectFirst();
    definirTransmissor(new ActionEvent());
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

    // Carrega a cena (tela) dentro da mesma janela
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
	}


	/*
   * ***************************************************************
   * Metodo: definirTransmissor
   * Funcao: define o host transmissor
   * Parametros: ActionEvent event - evento gerado ao selecionar uma opcao na ComboBox
   * Retorno: void
   ****************************************************************/

	@FXML
	private void definirTransmissor(ActionEvent event) {
		transmissor = cbTransmissor.getSelectionModel().getSelectedIndex();
		diogo.setTransmissor((transmissor == 0) ? true : false);
		gustavo.setTransmissor((transmissor == 1) ? true : false);

    roteadores.get(0).setHostProximo(diogo);
    roteadores.get(roteadores.size() - 1).setHostProximo(gustavo);
    Roteador rotDiogo = roteadores.get(0);
    Roteador rotGustavo = roteadores.get(roteadores.size() - 1);

    for (int i = 1; i < roteadores.size() - 1; i++) {
      roteadores.get(i).alterarVizinho(rotDiogo);
      roteadores.get(i).alterarVizinho(rotGustavo);
    }

    // Se haver algum pacote na rede, reinicia a simulacao
    if (pacotes.size() != 0) reiniciar();

    // Inicia a geracao de pacotes
    gerarPacoteInicial((transmissor == 0) ? rotDiogo : rotGustavo);
	} 

  private void gerarPacoteInicial(Roteador r) {
    Platform.runLater(() -> {
      Image mail = new Image(getClass().getResource("/img/Envelope.png").toExternalForm());

      ImageView envelope = new ImageView(mail);
      envelope.setFitWidth(41);
      envelope.setFitHeight(98);
      envelope.setLayoutX((transmissor == 0) ? diogo.getPosX() : gustavo.getPosY());
      envelope.setLayoutY((transmissor == 0) ? diogo.getPosY() : gustavo.getPosY());
      envelope.setVisible(true);
      envelope.setPreserveRatio(true);
      subrede.getChildren().add(envelope);
      imagens.add(envelope);

      Pacote p = (tempoDeVida == 0) ? new Pacote(envelope, versao, r) : new Pacote(envelope, versao, r, tempoDeVida);
      p.definirPosicao();
      pacotes.add(p);
      incrementarPacotes();

      p.setDaemon(true);
      p.start();
    });
  }

  public void gerarMaisPacotes(Roteador origem, Roteador destino) {
    Platform.runLater(() -> {
      Image mail = new Image(getClass().getResource("/img/Envelope.png").toExternalForm());

      ImageView envelope = new ImageView(mail);
      envelope.setFitWidth(41);
      envelope.setFitHeight(98);
      envelope.setLayoutX(origem.getPosX());
      envelope.setLayoutY(origem.getPosY());
      envelope.setVisible(true);
      envelope.setPreserveRatio(true);
      subrede.getChildren().add(envelope);
      imagens.add(envelope);

      Pacote p = (tempoDeVida == 0) ? new Pacote(envelope, versao, destino) : new Pacote(envelope, versao, destino, tempoDeVida);
      p.definirPosicao();
      pacotes.add(p);
      incrementarPacotes();

      p.setDaemon(true);
      p.start();
    });
  }

	private void reiniciar() {
    for (int i = 0; i < pacotes.size(); i++) {
      Pacote p = pacotes.get(i);
      p.interrupt();

      ImageView img = imagens.get(i);
      subrede.getChildren().remove(img);
      imagens.remove(img);
      pacotes.remove(p);

      try {
        Thread.sleep(200);
      }
      catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
	}

  public void interromper() {
    reiniciar();
    int quantidadePacotes = Integer.parseInt(lblPacotes.getText());
    painelReinicio.setVisible(true);
  }

  /*
   * ***************************************************************
   * Metodo: incrementarPacotes
   * Funcao: incrementa a quantidade de pacotes presentes na rede
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void incrementarPacotes() {
    // Incrementa o numero de pacotes e exibe na Label
    numPacotes++;
    lblPacotes.setText(Integer.toString(numPacotes));
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

  private void configurarSubrede() {
    try (BufferedReader br = new BufferedReader(new FileReader("backbone.txt"))) {
      quantidadeNos = Integer.parseInt(br.readLine());
      String linha;

      while ((linha = br.readLine()) != null) {
        String[] partes = linha.split(",");

        String no1 = partes[0];
        String no2 = partes[1];
        String peso = partes[2];

        Circle c1 = criarNo(no1);
        Circle c2 = criarNo(no2);

        Roteador r1 = (!verificarRoteador(no1)) ? criarRoteador(c1, no1) : obterRoteador(no1);
        Roteador r2 = (!verificarRoteador(no2)) ? criarRoteador(c2, no2) : obterRoteador(no2);
        gerarAresta(r1, r2, peso);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Circle criarNo(String nome) {
    if (nosCriados.containsKey(nome)) return nosCriados.get(nome);

    double xCirculo, yCirculo, xLabel, yLabel;

    if (posicaoCirculos.containsKey(nome) && posicaoLabels.containsKey(nome)) {
      xCirculo = posicaoCirculos.get(nome)[0];
      yCirculo = posicaoCirculos.get(nome)[1];
      xLabel = posicaoLabels.get(nome)[0];
      yLabel = posicaoLabels.get(nome)[1];
    }
    else {
      xCirculo = Math.random() * (subrede.getPrefWidth() - 40) + 20;
      yCirculo = Math.random() * (subrede.getPrefHeight() - 40) + 20;
      xLabel = 0;
      yLabel = 0;
    }

    Circle circulo = new Circle(xCirculo, yCirculo, 15, Color.WHITE); 
    circulo.setStroke(Color.BLACK);
    circulo.setStrokeWidth(1);
    circulo.setStrokeType(StrokeType.OUTSIDE);

    Label label = new Label(nome);
    label.setTextFill(Color.WHITE);
    label.setFont(Font.font("VCR OSD Mono", 15));
    label.setLayoutX(xLabel);
    label.setLayoutY(yLabel);

    nosCriados.put(nome, circulo);
    subrede.getChildren().addAll(circulo, label);

    return circulo;
  }

  private void gerarAresta(Roteador r1, Roteador r2, String peso) {
    String idConexao = (r1.getNome().compareTo(r2.getNome()) < 0) ? r1.getNome() + r2.getNome() : r2.getNome() + r1.getNome();

    if (!arestasExistentes.contains(idConexao)) {
      Line linha = new Line(r1.getNo().getCenterX(), r1.getNo().getCenterY(), r2.getNo().getCenterX(), r2.getNo().getCenterY());
      linha.setStroke(Color.WHITE);
      linha.setStrokeWidth(1.0);

      double meioX = (r1.getNo().getCenterX() + r2.getNo().getCenterX()) / 2;
      double meioY = (r1.getNo().getCenterY() + r2.getNo().getCenterY()) / 2;
        
      Label labelPeso = new Label(peso);
      labelPeso.setLayoutX(meioX + 5);
      labelPeso.setLayoutY(meioY - 10);
      labelPeso.setFont(Font.font("VCR OSD Mono", 15));
      labelPeso.setTextFill(Color.WHITE);

      arestasExistentes.add(idConexao);
      subrede.getChildren().addAll(linha, labelPeso);
      r1.adicionarVizinho(r2);
      r2.adicionarVizinho(r1);
    } 
  }

  private boolean verificarRoteador(String nome) {
    for (Roteador r : roteadores) {
      if (r.getNome().equals(nome)) {
        return true;
      }
    }

    return false;
  }

  private Roteador criarRoteador(Circle no, String nome) {
    Roteador r = new Roteador(no, nome);
    double[] posicoes = posicaoRoteadores.get(nome);
    r.definirPosicao(posicoes[0], posicoes[1]);
    roteadores.add(r);

    return r;
  }

  private Roteador obterRoteador(String nome) {
    for (Roteador r : roteadores) {
      if (r.getNome().equals(nome)) {
        return r;
      }
    }

    return null;
  }

  public void definirTempoDeVida(int tempoDeVida) {
    this.tempoDeVida = tempoDeVida;
  }
}
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
  private Map<String, double[]> posicaoCirculos = new HashMap<>();
  private HashMap<String, Label> labels = new HashMap<>();

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

    // Carrega as ArrayLists que armazenarao os roteadores, bem como os pacotes e suas respectivas imagens
    roteadores = new ArrayList<>();
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
    
    // Define quem sera o transmissor
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
    // if (quantidadeNos != 0) gerarPacoteInicial((transmissor == 0) ? rotDiogo : rotGustavo);
	} 

  /*
   * ***************************************************************
   * Metodo: gerarPacoteInicial
   * Funcao: gera um pacote para iniciar a simulacao
   * Parametros: Roteador r - roteador onde o pacote iniciara
   * Retorno: void
   ****************************************************************/

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

  /*
   * ***************************************************************
   * Metodo: gerarMaisPacotes
   * Funcao: gera mais pacotes para dar continuidade a simulacao
   * Parametros: Roteador origem - roteador do qual o pacote se originou
                 Roteador destino - roteador para o qual o pacote sera encaminhado
   * Retorno: void
   ****************************************************************/

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

  /*
   * ***************************************************************
   * Metodo: reiniciar
   * Funcao: reinicia a simulacao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

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

  /*
   * ***************************************************************
   * Metodo: interromper
   * Funcao: interrompe a simulacao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

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

  /*
   * ***************************************************************
   * Metodo: configurarSubrede
   * Funcao: gera o grafo correspondente a topologia da sub rede 
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void configurarSubrede() {
    try (BufferedReader br = new BufferedReader(new FileReader("backbone.txt"))) {
      String linha = br.readLine();
      if (linha == null) return;

      quantidadeNos = Integer.parseInt(linha.trim());
      calcularPosicaoNos(quantidadeNos);
      gerarLabels(quantidadeNos);

      for (int i = 0; i < quantidadeNos; i++) {
        String nome = gerarNome(i);
        Circle c = criarNo(nome);
        Roteador r = criarRoteador(c, nome);
      }

      calcularPosicaoLabels(quantidadeNos);
      calcularPosicaoRoteadores(quantidadeNos);

      while ((linha = br.readLine()) != null) {
        String[] partes = linha.split(",");
        if (partes.length < 3) continue;

        String nome1 = partes[0];
        String nome2 = partes[1];
        String peso = partes[2];

        Roteador r1 = obterRoteador(nome1);
        Roteador r2 = obterRoteador(nome2);
        if (r1 != null && r2 != null) gerarAresta(r1, r2, peso);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*
   * ***************************************************************
   * Metodo: calcularPosicaoNos
   * Funcao: calcula a posicao para cada no que sera criado dentro do grafo
   * Parametros: int totalNos - total de nos existentes na sub rede
   * Retorno: void
   ****************************************************************/

  private void calcularPosicaoNos(int totalNos) {
    double centroX = subrede.getPrefWidth() / 2;
    double centroY = subrede.getPrefHeight() / 2;
    double raio = Math.min(centroX, centroY) - 60;

    for (int i = 0; i < totalNos; i++) {
      String nome = gerarNome(i);

      double angulo = (2 * Math.PI * i) / totalNos;
      double x = centroX + raio * Math.cos(angulo);
      double y = centroY + raio * Math.sin(angulo);

      posicaoCirculos.put(nome, new double[]{x, y});
    }
  }

  /*
   * ***************************************************************
   * Metodo: gerarNome
   * Funcao: gera um rotulo para cada no (roteador)
   * Parametros: int i - indice do no (roteador)
   * Retorno: String
   ****************************************************************/

  private String gerarNome(int i) {
    return String.valueOf((char) ('A' + i));
  }

  /*
   * ***************************************************************
   * Metodo: calcularPosicaoLabels
   * Funcao: calcula as posicoes das labels correspondentes a cada roteador
   * Parametros: int totalNos - total de nos existentes na sub rede
   * Retorno: void
   ****************************************************************/

  private void calcularPosicaoLabels(int totalNos) {
    for (int i = 0; i < totalNos; i++) {
      String nome = gerarNome(i);
      Roteador r = obterRoteador(nome);
      Circle c = r.getNo();
      Label l = labels.get(nome);
      if (c == null || l == null) continue;

      l.applyCss();
      l.layout();

      double largura = l.getLayoutBounds().getWidth();
      double altura = l.getLayoutBounds().getHeight();

      double x = c.getCenterX() - (largura / 2);
      double y = c.getCenterY() - (altura / 2);

      l.setLayoutX(x);
      l.setLayoutY(y);

      subrede.getChildren().add(l);
    }
  }

  /*
   * ***************************************************************
   * Metodo: calcularPosicaoRoteadoress
   * Funcao: calcula a posicao de cada roteador gerado na sub rede
   * Parametros: int totalNos - total de nos existentes na sub rede
   * Retorno: void
   ****************************************************************/

  private void calcularPosicaoRoteadores(int totalNos) {
    double larguraPacote = 41.0;
    double alturaPacote = 98.0;

    for (int i = 0; i < totalNos; i++) {
      String nome = gerarNome(i);
      Roteador r = obterRoteador(nome);
      Circle c = r.getNo();
      if (c == null) continue;

      double x = c.getCenterX() - (larguraPacote / 2);
      double y = c.getCenterY() - (alturaPacote / 2);
      r.definirPosicao(x, y);
      atualizarRoteador(r);
    }
  }

  /*
   * ***************************************************************
   * Metodo: criarNo
   * Funcao: cria um no correspondente a cada roteador
   * Parametros: String nome - rotulo do roteador
   * Retorno: Circle
   ****************************************************************/

  private Circle criarNo(String nome) {
    if (nosCriados.containsKey(nome)) return nosCriados.get(nome);

    double xCirculo = posicaoCirculos.get(nome)[0];
    double yCirculo = posicaoCirculos.get(nome)[1];

    Circle circulo = new Circle(xCirculo, yCirculo, 15, Color.WHITE); 
    circulo.setStroke(Color.BLACK);
    circulo.setStrokeWidth(1);
    circulo.setStrokeType(StrokeType.OUTSIDE);

    nosCriados.put(nome, circulo);
    subrede.getChildren().addAll(circulo);

    return circulo;
  }

  /*
   * ***************************************************************
   * Metodo: gerarLabels
   * Funcao: gera as Labels correspondentes a cada no
   * Parametros: int totalNos - total de nos existentes na sub rede
   * Retorno: void
   ****************************************************************/

  private void gerarLabels(int totalNos) {
    for (int i = 0; i < totalNos; i++) {
      String nome = gerarNome(i);
      Label label = new Label(nome);
      label.setTextFill(Color.web("#2d4180"));
      label.setFont(Font.font("VCR OSD Mono", 15));
      labels.put(nome, label);
    }
  }

  /*
   * ***************************************************************
   * Metodo: gerarAresta
   * Funcao: desenha a aresta existente entre dois roteadores
   * Parametros: Roteador r1 - roteador de origem
                 Roteador r2 - roteador de destino
                 String peso - peso da aresta
   * Retorno: void
   ****************************************************************/

  private void gerarAresta(Roteador r1, Roteador r2, String peso) {
    String idConexao = (r1.getNome().compareTo(r2.getNome()) < 0) ? r1.getNome() + r2.getNome() : r2.getNome() + r1.getNome();

    if (!arestasExistentes.contains(idConexao)) {
      Line linha = new Line(r1.getNo().getCenterX(), r1.getNo().getCenterY(), r2.getNo().getCenterX(), r2.getNo().getCenterY());
      linha.setStroke(Color.WHITE);
      linha.setStrokeWidth(1.0);

      double meioX = (r1.getNo().getCenterX() + r2.getNo().getCenterX()) / 2;
      double meioY = (r1.getNo().getCenterY() + r2.getNo().getCenterY()) / 2;

      arestasExistentes.add(idConexao);
      subrede.getChildren().add(linha);
      r1.adicionarVizinho(r2);
      r2.adicionarVizinho(r1);
    } 
  }

  /*
   * ***************************************************************
   * Metodo: criarRoteador
   * Funcao: cria uma nova instancia da classe Roteador
   * Parametros: Circle no - no do roteador
                 String nome - rotulo do roteador
   * Retorno: Roteador
   ****************************************************************/

  private Roteador criarRoteador(Circle no, String nome) {
    Roteador r = new Roteador(no, nome);
    roteadores.add(r);

    return r;
  }

  /*
   * ***************************************************************
   * Metodo: obterRoteador
   * Funcao: obtem um roteador ja existente na sub rede
   * Parametros: String nome - rotulo do roteador a ser buscado
   * Retorno: Roteador
   ****************************************************************/

  private Roteador obterRoteador(String nome) {
    for (Roteador r : roteadores) {
      if (r.getNome().equals(nome)) {
        return r;
      }
    }

    return null;
  }
 
  /*
   * ***************************************************************
   * Metodo: atualizarRoteador
   * Funcao: atualiza os dados de um roteador ja existente na sub rede
   * Parametros: Roteador r - roteador a ser atualizado
   * Retorno: void
   ****************************************************************/

  private void atualizarRoteador(Roteador r) {
    for (int i = 0; i < roteadores.size(); i++) {
      String nome = roteadores.get(i).getNome();

      if (r.getNome().equals(nome)) {
        roteadores.set(i, r);
        break;
      }
    }
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
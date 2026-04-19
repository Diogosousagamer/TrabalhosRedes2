/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 16/04/2026
* Ultima alteracao.: 19/04/2026
* Nome.............: TelaPrincipalController
* Funcao...........: Classe que controla os eventos da TelaPrincipal.
                     
*************************************************************** */

package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.Thread;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Aresta;
import model.EntradaTabela;
import model.Pacote;
import model.Roteador;
import model.TabelaRoteamento;

public class TelaPrincipalController implements Initializable {
	// Componentes da interface
	@FXML private AnchorPane painelAlterarRede;
	@FXML private AnchorPane subrede;
  @FXML private AnchorPane visaoTabelas;
	@FXML private Button btnAlterarRede;
  @FXML private Button btnAplicar;
  @FXML private Button btnExibirTabelas;
  @FXML private Button btnFecharAlterarRede;
  @FXML private Button btnFecharTabela;
	@FXML private Button btnVoltar;
	@FXML private Label lblCaminho;
	@FXML private Label lblDestino;
	@FXML private Label lblOrigem;
	@FXML private Label lblSelecao;
  @FXML private TabPane painelTabela;
	@FXML private TextArea txtBackbone;

	// Variaveis e instancias
	public static volatile TelaPrincipalController controller;
	private int quantidadeNos;
	private Roteador origem;
  private Roteador destino;
  private String modelo;
  private ArrayList<Label> tempoArestas;
  private ArrayList<Roteador> roteadores;
  private HashMap<String, Circle> nosCriados = new HashMap<>();
  private Map<String, double[]> posicaoCirculos = new HashMap<>();
  private HashMap<String, Aresta> arestasExistentes = new HashMap<>();
  private HashMap<String, Label> labels = new HashMap<>();
  private HashMap<String, Label> distancias = new HashMap<>();

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
    // Carrega as ArrayLists que armazenarao os roteadores e os tempos de ida e volta das arestas, respectivamente
    roteadores = new ArrayList<>();
    tempoArestas = new ArrayList<>();

    // Carrega a instancia volatil do controller
    controller = this;

    // Gera o grafo da subrede via backbone
    configurarSubrede();
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

    // Carrega a cena (tela) dentro da mesma janela
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(scene);
	}

  /*
   * ***************************************************************
   * Metodo: ocultarAresta
   * Funcao: oculta a aresta da sub rede, desabilitando-a quando o usuario
             clicar na linha
   * Parametros: MouseEvent event - evento gerado ao clicar no circulo
                 Aresta a - aresta na qual o usuario clicou
   * Retorno: void
   ****************************************************************/

  @FXML
  private void ocultarAresta(MouseEvent event, Aresta a) {
    Roteador r1 = a.getR1();
    Roteador r2 = a.getR2();

    String nome1 = r1.getNome();
    String nome2 = r2.getNome();
    String ida = Long.toString(a.getIda());
    String volta = Long.toString(a.getVolta());

    String linha = nome1 + "," + nome2 + "," + ida + "," + volta;

    File backbone = new File("backbone.txt");
    ArrayList<String> linhasRestantes = new ArrayList<>();

    try {
      if (backbone.exists()) {
        Files.lines(backbone.toPath()).forEach(l -> {
          if (!l.trim().equals(linha)) {
            linhasRestantes.add(l);
          }
        });
      }

      Files.write(backbone.toPath(), linhasRestantes);
      removerSubrede();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
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
  private void definirOrigemDestino(MouseEvent event, Circle c) {
    // Obtem-se o rotulo do no
    String nome = obterRotuloNo(c);

    // Interrompe o metodo se o circulo nao tiver um rotulo correspondente
    if (nome == null) return;

    // Inicio do bloco if/else if/else if
    // Se um roteador ainda nao tiver sido definido como origem
    if (!existeOrigem()) {
      // O contorno do no do roteador de destino se torna verde
      c.setStroke(Color.web("#1fdb18"));

      // Impede que o circulo possa ser selecionado de novo    
      c.setMouseTransparent(true);

      /* O circulo nao se torna exatamente "selecionavel"; o cursor eh alterado por motivos visuais, 
      para que nao induza erroneamente o usuario a tentar seleciona-lo novamente, pois ele ja foi marcado 
      como origem/destino em outro momento */

      // Configura o roteador de origem e o atualiza na lista de roteadores
      origem = obterRoteador(nome);
      origem.setOrigem(true);
      origem.setNo(c);
      atualizarRoteador(origem);
      alterarRoteadorNosVizinhos(origem);
      lblOrigem.setText(origem.getNome());
    }
    else if (!existeDestino() && origem != null && !nome.equals(origem.getNome())) { // Porem se um destino nao tiver sido definido
                                                                                     // a origem da rota tiver sido definida
                                                                                     // e o no selecionado possuir um rotulo diferente
                                                                                     // do rotulo do no de origem
      // O contorno do no do roteador de destino se torna vermelho
      c.setStroke(Color.web("#d60b18"));

      // Inicio do bloco for
      for (Map.Entry<String, Circle> entrada : nosCriados.entrySet()) {
        // Todos os nos tem seus cursores alterados para que nao sejam mais "selecionaveis"
        Circle circulo = entrada.getValue();
        circulo.setMouseTransparent(true);
      } // Fim do bloco for

      // Configura o roteador de destino e o atualiza na lista de roteadores
      destino = obterRoteador(nome);
      destino.setNo(c);
      destino.setDestino(true);
      atualizarRoteador(destino);
      alterarRoteadorNosVizinhos(destino);
      lblDestino.setText(destino.getNome());

      // Oculta a label de selecao
      lblSelecao.setVisible(false);
      lblCaminho.setVisible(true);

      // Impede que a rede seja alterada durante a simulacao
      btnAlterarRede.setDisable(true);

      for (Aresta a : arestasExistentes.values()) {
        Line l = a.getLinha();
        l.setMouseTransparent(true);
      }

      if (origem != null) iniciarSimulacao();
    }
    else if (existeOrigem() && existeDestino()) {
      // Interrompe o metodo se uma origem e um destino ja tiverem
      // sido definidos
      return;
    } // Fim do bloco if/else if/else if
  }

  private void iniciarSimulacao() {
    Platform.runLater(() -> {
      Image mail = new Image(getClass().getResource("/img/Envelope.png").toExternalForm());

      ImageView envelope = new ImageView(mail);
      envelope.setFitWidth(41);
      envelope.setFitHeight(98);
      envelope.setPreserveRatio(true);
      subrede.getChildren().add(envelope);

      Pacote p = new Pacote(envelope, origem, destino);
      p.setDaemon(true);
      p.start();

      // calcularVetorDistancia(p);
    });
  }

  private void calcularVetorDistancia(Pacote p) {
    Thread vetorDistancia = new Thread(() -> {

    });

    vetorDistancia.setDaemon(true);
    vetorDistancia.start();
  }

  /*
   * ***************************************************************
   * Metodo: exibirTabelas
   * Funcao: exibe o painel das tabelas de roteamento
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void exibirTabelas(ActionEvent event) {
    // Exibe o painel das tabelas de roteamento acima da sub rede (porque a sub rede
    // eh criada via codigo, acima dos demais componentes)
    visaoTabelas.toFront();
    visaoTabelas.setVisible(true);
  }

  /*
   * ***************************************************************
   * Metodo: fecharTabela
   * Funcao: oculta o painel das tabelas de roteamento
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void fecharTabela(ActionEvent event) {
    visaoTabelas.setVisible(false);
  }

	/*
   * ***************************************************************
   * Metodo: alterarRede
   * Funcao: exibe e configura o painel de alteracao da sub rede
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void alterarRede(ActionEvent event) {
    // Inicio do bloco try/catch
    // Faz uma leitura do conteudo presente no arquivo do backbone da rede
    try (BufferedReader br = new BufferedReader(new FileReader("backbone.txt"))) {
      // Variavel responsavel por ler cada linha do arquivo
      String linha = "";

      // Variavel que guardara o texto obtido do arquivo
      String backbone = "";

      // Inicio do bloco while
      // Enquanto ainda houver texto escrito no arquivo "backbone.txt"
      while ((linha = br.readLine()) != null) {
        // Guarda a linha dentro do texto de backbone, dando espaco para a proxima linha
        backbone += linha + "\n";
      } // Fim do bloco while

      // Armazena o backbone em uma constante
      final String backboneFinal = backbone;

      // Inicio do bloco Platform.runLater
      Platform.runLater(() -> {
        // Exibe o painel com o texto do backbone ja escrito
        painelAlterarRede.toFront();
        painelAlterarRede.setVisible(true);
        txtBackbone.setText(backboneFinal);
      }); // Fim do bloco Platform.runLater
    }
    catch (IOException e) {
      // Em caso de excecao, ela eh exibida no terminal
      e.printStackTrace();
    } // Fim do bloco try/catch
  }

  /*
   * ***************************************************************
   * Metodo: fecharAlterarRede
   * Funcao: oculta o painel de alteracao da sub rede
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void fecharAlterarRede(ActionEvent event) {
    painelAlterarRede.setVisible(false);
  }

  /*
   * ***************************************************************
   * Metodo: aplicar
   * Funcao: aplica as alteracoes na subrede
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void aplicar(ActionEvent event) {
    // Inicio do bloco try/catch
    try (PrintWriter out = new PrintWriter(new FileWriter("backbone.txt", false))) {
      // Pega o texto inserido na caixa de texto e sobrescreve o texto
      // anteriormente escrito no arquivo
      out.print(txtBackbone.getText());

      // Oculta o painel de modificacao da rede
      painelAlterarRede.setVisible(false);

      // Remove a sub rede para depois reconfigura-la
      removerSubrede();
    }
    catch (IOException e) {
      // Em caso de excecao, ela eh exibida no terminal
      e.printStackTrace();
    } // Fim do bloco try/catch
  }

  /*
   * ***************************************************************
   * Metodo: configurarSubrede
   * Funcao: gera o grafo correspondente a topologia da sub rede 
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void configurarSubrede() {
    // Inicio do bloco try/catch
    // Tenta-se abrir um novo BufferedReader para ler o backbone e gerar o grafo da subrede
    try (BufferedReader br = new BufferedReader(new FileReader("backbone.txt"))) {
      // Le a primeira linha (quantidade de nos do grafo)
      String linha = br.readLine();

      // Interrompe o metodo se a linha for nula
      if (linha == null) return;

      // Obtem-se a quantidade de nos a partir da primeira linha
      quantidadeNos = Integer.parseInt(linha.trim());

      // Calcula a posicao dos nos
      calcularPosicaoNos(quantidadeNos);

      // Gera as labels de cada no
      gerarLabels(quantidadeNos);

      // Inicio do bloco for
      // O laco continua sendo executado ate que atinja a quantidade de nos
      // presentes no grafo da sub rede
      for (int i = 0; i < quantidadeNos; i++) {
        // Gera o rotulo de acordo com o indice
        String nome = gerarNome(i);

        // Cria o no correspondente ao rotulo gerado
        Circle c = criarNo(nome);

        // Cria uma nova instancia do roteador
        Roteador r = criarRoteador(c, nome);
      } // Fim do bloco for

      // Calcula as posicoes das labels e dos roteadores
      calcularPosicaoLabels(quantidadeNos);
      calcularPosicaoRoteadores(quantidadeNos);

      // Inicio do bloco while
      // Enquanto ainda houver linhas presentes no arquivo
      while ((linha = br.readLine()) != null) {
        // Divide a linha em partes, separadas por virgulas no arquivo
        String[] partes = linha.split(",");

        // Interrompe o instante atual e retoma o laco
        // se a quantidade de partes for menor que 3
        if (partes.length < 4) continue;

        // Obtem-se os rotulos dos nos e o peso da aresta
        String nome1 = partes[0];
        String nome2 = partes[1];
        String ida = partes[2];
        String volta = partes[3];

        // Obtem-se os roteadores com base nos rotulos obtidos
        Roteador r1 = obterRoteador(nome1);
        Roteador r2 = obterRoteador(nome2);

        // Desenha a aresta se nenhum dos roteadores for nulo
        if (r1 != null && r2 != null) gerarAresta(r1, r2, ida, volta);
      } // Fim do bloco while

      criarTabelas();
    }
    catch (IOException e) {
      // Em caso de excecao, ela sera exibida no terminal
      // no instante em que o metodo for interrompido
      e.printStackTrace();
    } // Fim do bloco try/catch
  }

  /*
   * ***************************************************************
   * Metodo: removerSubrede
   * Funcao: remove a sub rede presente para dar lugar a uma nova
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void removerSubrede() {
    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Inicio do bloco for
      for (Map.Entry<String, Circle> entrada : nosCriados.entrySet()) {
        // Remova os nos presentes na topologia da subrede
        subrede.getChildren().remove(entrada.getValue());
      } // Fim do bloco for

      // Inicio do bloco for  
      for (Map.Entry<String, Label> entrada : labels.entrySet()) {
        subrede.getChildren().remove(entrada.getValue());
      } // Fim do bloco for

      // Inicio do bloco for
      for (Aresta a : arestasExistentes.values()) {
        // Remove as arestas presentes na topologia da subrede
        subrede.getChildren().remove(a.getLinha());
      } // Fim do bloco for

      // Inicio do bloco for
      for (Label t : tempoArestas) {
        // Remove os tempos de ida e volta das arestas
        subrede.getChildren().remove(t);
      } // Fim do bloco for

      // Inicio do bloco for
      for (Label d : distancias.values()) {
        // Remove as distancias de cada no
        subrede.getChildren().remove(d);
      } // Fim do bloco for

      // Inicio do bloco for
      for (Roteador r : roteadores) {
        // Esvazia os roteadores
        r = null;
      } // Fim do bloco for

      painelTabela.getTabs().clear();

      // Esvazia as listas e os HashMaps
      roteadores.clear();
      nosCriados.clear();
      posicaoCirculos.clear();
      arestasExistentes.clear();
      labels.clear();
      distancias.clear();
      tempoArestas.clear();

      // Reconfigura a sub rede
      configurarSubrede();
    }); // Fim do bloco Platform.runLater
  }

  /*
   * ***************************************************************
   * Metodo: calcularPosicaoNos
   * Funcao: calcula a posicao para cada no que sera criado dentro do grafo
   * Parametros: int totalNos - total de nos existentes na sub rede
   * Retorno: void
   ****************************************************************/

  private void calcularPosicaoNos(int totalNos) {
    // Obtem-se o centro e o raio do painel da sub rede
    double centroX = subrede.getPrefWidth() / 2;
    double centroY = subrede.getPrefHeight() / 2;
    double raio = Math.min(centroX, centroY) - 60;

    // Inicio do bloco for
    // O laco eh executado ate que se atinja a quantidade de nos
    // existentes na sub rede
    for (int i = 0; i < totalNos; i++) {
      // Gera o rotulo conforme o indice atual
      String nome = gerarNome(i);

      // Calcula o angulo do no
      double angulo = (2 * Math.PI * i) / totalNos;

      // Calcula as posicoes do no via conversao de coordenadas polares
      // para coordenadas cartesianas
      double x = centroX + raio * Math.cos(angulo);
      double y = centroY + raio * Math.sin(angulo);

      // Armazena o rotulo e as posicoes correspondentes no HashMap
      posicaoCirculos.put(nome, new double[]{x, y});
    } // Fim do bloco for
  }

  /*
   * ***************************************************************
   * Metodo: gerarNome
   * Funcao: gera um rotulo para cada no (roteador)
   * Parametros: int i - indice do no (roteador)
   * Retorno: String
   ****************************************************************/

  private String gerarNome(int i) {
    // Retorna uma letra correspondente ao indice do no
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
    // Inicio do bloco for
    // O laco e executado ate atingir o total de nos existentes
    for (int i = 0; i < totalNos; i++) {
      // Obtem-se o roteador a partir do nome gerado
      String nome = gerarNome(i);
      Roteador r = obterRoteador(nome);

      // Obtem-se o no do roteador
      Circle c = r.getNo();

      // Obtem-se a label do rotulo correspondente
      Label l = labels.get(nome);

      // Retoma o laco se o no ou a label nao tiverem sido encontrados
      if (c == null || l == null) continue;

      // Inicio do bloco if
      if (!subrede.getChildren().contains(l)) {
        // Adiciona a label na sub rede caso ela nao tiver sido 
        // adicionada anteriormente
        subrede.getChildren().add(l);
      } // Fim do bloco if

      // Forca o calculo das propriedades visuais da label
      l.applyCss();
      l.layout();

      // Obtem-se a largura e a altura da label
      double largura = l.getBoundsInLocal().getWidth();
      double altura = l.getBoundsInLocal().getHeight();

      // Calcula as posicoes X e Y da label
      double x = c.getCenterX() - (largura / 2.0);
      double y = c.getCenterY() - (altura / 2.0);

      // Posiciona a label
      l.setLayoutX(x);
      l.setLayoutY(y);
    } // Fim do bloco for
  }

  /*
   * ***************************************************************
   * Metodo: calcularPosicaoRoteadores
   * Funcao: calcula a posicao de cada roteador gerado na sub rede
   * Parametros: int totalNos - total de nos existentes na sub rede
   * Retorno: void
   ****************************************************************/

  private void calcularPosicaoRoteadores(int totalNos) {
    // Largura e altura da imagem do pacote
    double larguraPacote = 41.0;
    double alturaPacote = 98.0;

    // Inicio do bloco for
    // O laco e executado ate atingir o total de nos existentes
    for (int i = 0; i < totalNos; i++) {
      // Obtem-se o roteador atraves do nome gerado
      String nome = gerarNome(i);
      Roteador r = obterRoteador(nome);

      // Obtem-se o no do roteador
      Circle c = r.getNo();

      // Pula para outro laco se o circulo for nulo
      if (c == null) continue;

      // Calcula as posicoes X e Y do roteador
      double x = c.getCenterX() - (larguraPacote / 2);
      double y = c.getCenterY() - (alturaPacote / 2);

      // Define a posicao do roteador e o atualiza
      r.definirPosicao(x, y);
      atualizarRoteador(r);
    } // Fim do bloco for
  }

  /*
   * ***************************************************************
   * Metodo: criarNo
   * Funcao: cria um no correspondente a cada roteador
   * Parametros: String nome - rotulo do roteador
   * Retorno: Circle
   ****************************************************************/

  private Circle criarNo(String nome) {
    // Retorna o no correspondente ao nome caso ele ja tiver sido criado
    if (nosCriados.containsKey(nome)) return nosCriados.get(nome);
    
    // Obtem a posicao a ser assumida pelo no
    double xCirculo = posicaoCirculos.get(nome)[0];
    double yCirculo = posicaoCirculos.get(nome)[1];

    // Cria o circulo que representara o no
    Circle circulo = new Circle(xCirculo, yCirculo, 15, Color.WHITE); 
    circulo.setStroke(Color.BLACK);
    circulo.setStrokeWidth(2);
    circulo.setStrokeType(StrokeType.OUTSIDE);
    circulo.setCursor(Cursor.HAND);

    // Adiciona o evento para definir o no (roteador) como origem/destino
    // do percurso ao ser clicado
    circulo.setOnMouseClicked(event -> {
      definirOrigemDestino(event, circulo);
    });

    // Cria a label correspondente a distancia do no, obtida durante
    // o calculo do caminho mais curto
    Label distancia = new Label("(" + nome + ", ?)");
    distancia.setFont(Font.font("VCR OSD Mono", 13));
    distancia.setTextFill(Color.WHITE);

    // Seta a posicao da distancia com base no centro do circulo
    distancia.setLayoutX(circulo.getCenterX() + 18);
    distancia.setLayoutY(circulo.getCenterY() + 25);

    // Adiciona o circulo e o nome como chave no HashMap
    nosCriados.put(nome, circulo);

    // Adiciona a label de distancia dentro do HashMap
    distancias.put(nome, distancia);

    // Adiciona o circulo/no e a sua respectiva distancia na sub rede
    subrede.getChildren().addAll(circulo, distancia);

    // Retorna o circulo
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
    // Inicio do bloco for
    // Labels sao criadas ate que se atinja o total de nos existentes na sub rede
    for (int i = 0; i < totalNos; i++) {
      // Gera o nome do no
      String nome = gerarNome(i);

      // Cria a label com o nome obtido
      Label label = new Label(nome);
      label.setTextFill(Color.web("#20c113"));
      label.setFont(Font.font("VCR OSD Mono", 15));
      labels.put(nome, label);
    } // Fim do bloco for
  }

  /*
   * ***************************************************************
   * Metodo: gerarAresta
   * Funcao: desenha a aresta existente entre dois roteadores
   * Parametros: Roteador r1 - roteador de origem
                 Roteador r2 - roteador de destino
                 String ida - tempo de ida da aresta
                 String volta - tempo de volta da aresta
   * Retorno: void
   ****************************************************************/

  private void gerarAresta(Roteador r1, Roteador r2, String ida, String volta) {
    // Cria-se uma String para identificar a aresta
    String idConexao = (r1.getNome().compareTo(r2.getNome()) < 0) ? r1.getNome() + r2.getNome() : r2.getNome() + r1.getNome();

    // Inicio do bloco if
    // Se a id nao se encontrar no Set de arestas existentes
    if (!arestasExistentes.containsKey(idConexao)) {
      // A linha e desenhada entre os nos de cada roteador
      Line linha = new Line(r1.getNo().getCenterX(), r1.getNo().getCenterY(), r2.getNo().getCenterX(), r2.getNo().getCenterY());
      linha.setStroke(Color.WHITE);
      linha.setStrokeWidth(3.0);
      linha.setCursor(Cursor.HAND);

      // Adiciona a linha na tela da sub rede
      subrede.getChildren().add(linha);

      // Os roteadores sao marcados como vizinhos um do outro
      r1.adicionarVizinho(r2);
      r2.adicionarVizinho(r1);

      // Atualiza os roteadores
      atualizarRoteador(r1);
      atualizarRoteador(r2);

      // Converte o peso em valor inteiro
      long idaLong = Long.parseLong(ida);
      long voltaLong = Long.parseLong(volta);

      // Cria uma nova instancia de aresta
      Aresta aresta = new Aresta(linha, r1, r2, idaLong, voltaLong);

      linha.setOnMouseClicked(event -> {
        ocultarAresta(event, aresta);
      });

      aresta.setLinha(linha);

      // Coloca a aresta dentro do HashMap
      arestasExistentes.put(idConexao, aresta);

      // Gera as labels de ida e volta da aresta
      Label lblTempo = new Label(ida + ";" + volta);
      lblTempo.setFont(Font.font("VCR OSD Mono", 11));
      lblTempo.setTextFill(Color.WHITE);

      // Calcula a posicao media do peso a partir do centro dos nos
      double xMedio = (r1.getNo().getCenterX() + r2.getNo().getCenterX()) / 2;
      double yMedio = (r1.getNo().getCenterY() + r2.getNo().getCenterY()) / 2;

      // Define a posicao do peso
      lblTempo.setLayoutX(xMedio);
      lblTempo.setLayoutY(yMedio);

      // Adiciona uma translacao para garantir que fique alinhado
      lblTempo.setTranslateX(-7);
      lblTempo.setTranslateY(-7);

      // Adiciona a label dentro da lista de pesos e da sub rede
      tempoArestas.add(lblTempo);

      subrede.getChildren().add(lblTempo);
    } // Fim do bloco if
  }

  private void criarTabelas() {
    for (Roteador r : roteadores) {
      Tab t = new Tab(r.getNome());
      painelTabela.getTabs().add(t);

      TableView<EntradaTabela> tabela = new TableView<>();

      tabela.getStyleClass().add("table-view");
      String css = getClass().getResource("/util/trilha.css").toExternalForm();
      tabela.getStylesheets().add(css);
      
      TableColumn<EntradaTabela, String> destino = new TableColumn<>("Para");
      destino.setCellValueFactory(new PropertyValueFactory<>("destino"));
      centralizarColuna(destino);

      TableColumn<EntradaTabela, String> saida = new TableColumn<>("Saida");
      saida.setCellValueFactory(new PropertyValueFactory<>("linhaSaida"));
      centralizarColuna(saida);

      TableColumn<EntradaTabela, Long> retardo = new TableColumn<>("Retardo");
      retardo.setCellValueFactory(new PropertyValueFactory<>("retardo"));
      centralizarColuna(retardo);

      tabela.getColumns().addAll(destino, saida, retardo);
      tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
      t.setContent(tabela);

      ArrayList<EntradaTabela> entradas = new ArrayList<>();

      for (Roteador rot : roteadores) {
        entradas.add(new EntradaTabela(rot.getNome(), "-", 0));
      }

      TabelaRoteamento tab = new TabelaRoteamento(r.getNome(), tabela, entradas);
      tab.atualizarTabela();

      r.setTabela(tab);
      atualizarRoteador(r);
      alterarRoteadorNosVizinhos(r);
    }
  }

  private <S, T> void centralizarColuna(TableColumn<S, T> coluna) {
    coluna.setCellFactory(tc -> new TableCell<S, T>() {
      @Override
      protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
          setText(null);
        } 
        else {
          setText(item.toString());
          setStyle("-fx-alignment: CENTER;");
        }
      }
    });
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
    // Cria uma nova instancia da classe Roteador
    // e a adiciona dentro da lista de roteadores
    Roteador r = new Roteador(no, nome);
    roteadores.add(r);

    // Retorna o roteador criado
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
    // Inicio do bloco for
    // Realiza-se uma busca na lista de roteadores
    for (Roteador r : roteadores) {
      // Inicio do bloco if
      if (r.getNome().equals(nome)) {
        // Retorna o roteador obtido se ele possuir o rotulo buscado        
        return r;
      } // Fim do bloco if
    } // Fim do bloco for

    // Retorna nulo se o roteador buscado nao for encontrado
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
    // Inicio do bloco for
    // Realiza-se uma busca pela lista de roteadores
    for (int i = 0; i < roteadores.size(); i++) {
      // Obtem-se o rotulo do roteador obtido no instante atual
      String nome = roteadores.get(i).getNome();

      // Inicio do bloco if
      if (r.getNome().equals(nome)) {
        // Realiza a troca se o roteador obtido possuir o rotulo
        // correspondente ao do roteador passado como parametro 
        roteadores.set(i, r);

        // Interrompe o laco
        break;
      } // Fim do bloco if
    } // Fim do bloco for
  }

  /*
   * ***************************************************************
   * Metodo: alterarRoteadorNosVizinhos
   * Funcao: altera a instancia do roteador nos roteadores em que ele for vizinho
   * Parametros: Roteador r - roteador a ser atualizado
   * Retorno: void
   ****************************************************************/

  private void alterarRoteadorNosVizinhos(Roteador r) {
    // Inicio do bloco for
    // Percorremos cada roteador existente na lista de roteadores
    for (int i = 0; i < roteadores.size(); i++) {
      // Obtem o roteador do instante atual
      Roteador rot = roteadores.get(i);

      // Altera a instancia do roteador passado como parametro caso ele for 
      // vizinho do roteador atual
      rot.alterarVizinho(r);

      // Altera o roteador atual na lista de roteadores
      atualizarRoteador(rot);
    } // Fim do bloco for
  }

  /*
   * ***************************************************************
   * Metodo: existeOrigem
   * Funcao: verifica se um roteador ja foi marcado como origem no percurso
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  private boolean existeOrigem() {
    // Inicio do bloco for
    // Realiza-se uma busca dentro da lista de roteadores
    // existentes na topologia
    for (Roteador r : roteadores) {
      // Inicio do bloco if
      if (r.isOrigem()) {
        // Retorna verdadeiro se o roteador tiver sido
        // definido como a origem (ponto inicial) da rota
        return true;
      } // Fim do bloco if
    } // Fim do bloco for

    // Retorna falso caso nenhum ponto de origem
    // tiver sido definido para a rota
    return false;
  }

  /*
   * ***************************************************************
   * Metodo: existeDestino
   * Funcao: verifica se um roteador ja foi marcado como destino no percurso
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  private boolean existeDestino() {
    // Inicio do bloco for
    // Realiza-se uma busca dentro da lista de roteadores
    // existentes na topologia
    for (Roteador r : roteadores) {
      // Inicio do bloco if
      if (r.isDestino()) {
        // Retorna verdadeiro se algum roteador ja tiver sido
        // definido como a origem
        return true;
      } // Fim do bloco if
    } // Fim do bloco for

    // Retorna falso caso o destino nao tiver sido
    // definido anteriormente
    return false;
  }

  /*
   * ***************************************************************
   * Metodo: obterRotuloNo
   * Funcao: obtem e retorna o rotulo do no para determinar o roteador
             que ele representa
   * Parametros: Circle c - no cujo rotulo sera determinado
   * Retorno: String
   ****************************************************************/

  private String obterRotuloNo(Circle c) {
    // Inicio do bloco for
    // Realiza-se uma busca dentro do HashMap de nos existentes na interface
    for (Map.Entry<String, Circle> entrada : nosCriados.entrySet()) {
      // Inicio do bloco if
      if (entrada.getValue().equals(c)) {
        // Retorna o rotulo caso o circulo for encontrado
        // dentro do HashMap
        return entrada.getKey();
      } // Fim do bloco if
    } // Fim do bloco for

    // Retorna nulo caso nao for obtido nenhum retorno
    // a partir da busca
    return null;
  }
}
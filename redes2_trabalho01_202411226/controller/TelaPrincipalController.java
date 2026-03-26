/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 15/03/2026
* Ultima alteracao.: 26/03/2026
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
  private volatile boolean simulacaoAtiva;
  public static volatile TelaPrincipalController controller;
	private Roteador origem;
  private Roteador destino;
  private int quantidadeNos;
	private int versao;
	private int numPacotes;
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
    // Carrega as ArrayLists que armazenarao os roteadores, bem como os pacotes e suas respectivas imagens
    roteadores = new ArrayList<>();
    pacotes = new ArrayList<>();
    imagens = new ArrayList<>();

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

      // Altera o cursor para ele nao ser "selecionavel"     
      c.setCursor(Cursor.DEFAULT);

      /* O circulo nao se torna exatamente "selecionavel"; o cursor eh alterado por motivos visuais, 
      para que nao induza erroneamente o usuario a tentar seleciona-lo novamente, pois ele ja foi marcado 
      como origem/destino em outro momento */

      // Configura o roteador de origem e o atualiza na lista de roteadores
      origem = obterRoteador(nome);
      origem.setOrigem(true);
      origem.setNo(c);
      atualizarRoteador(origem);

      // Inicio do bloco for
      for (int i = 0; i < roteadores.size(); i++) {
        // Atualiza o roteador de origem dentro da lista de vizinhos 
        // de cada roteador antes de atualiza-los na lista de roteadores
        Roteador r = roteadores.get(i);
        r.alterarVizinho(origem);
        atualizarRoteador(r);
      } // Fim do bloco for

      // Exibe o rotulo do roteador na label
      lblOrigem.setText(origem.getNome());
    }
    else if (!existeDestino() && origem != null && !nome.equals(origem.getNome())) { // Porem se um destino nao tiver sido definido
                                                                                     // a origem da rota tiver sido definida
                                                                                     // e o no selecionado possuir um rotulo diferente
                                                                                     // do rotulo do no de origem
      // O contorno do no do roteador de destino se torna vermelho
      c.setStroke(Color.web("#d60b18"));

      // Altera o cursor do no para que ele nao seja mais "selecionavel"
      c.setCursor(Cursor.DEFAULT);

      // Inicio do bloco for
      for (Map.Entry<String, Circle> entrada : nosCriados.entrySet()) {
        // Todos os nos tem seus cursores alterados para que nao sejam mais "selecionaveis"
        Circle circulo = entrada.getValue();
        circulo.setCursor(Cursor.DEFAULT);
      } // Fim do bloco for

      // Configura o roteador de destino e o atualiza na lista de roteadores
      destino = obterRoteador(nome);
      destino.setNo(c);
      destino.setDestino(true);
      atualizarRoteador(destino);

      // Inicio do bloco for
      for (int i = 0; i < roteadores.size(); i++) {
        // Atualiza o roteador de destino dentro da lista de vizinhos 
        // de cada roteador antes de atualiza-los na lista de roteadores
        Roteador r = roteadores.get(i);
        r.alterarVizinho(destino);
        atualizarRoteador(r);
      } // Fim do bloco for

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
    else if (existeOrigem() && existeDestino()) {
      // Interrompe o metodo se uma origem e um destino ja tiverem
      // sido definidos
      return;
    } // Fim do bloco if/else if/else if
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
      subrede.getChildren().add(envelope);
      imagens.add(envelope);

      // Gera um novo pacote, define a sua posicao, o adiciona na lista de pacotes e incrementa a quantidade
      // de pacotes existentes
      Pacote p = criarPacote(envelope, r, r, null, new ArrayList<>());
      p.definirPosicao();
      pacotes.add(p);
      incrementarPacotes();

      // Garante que a Thread seja interrompida caso a janela for fechada
      p.setDaemon(true);

      // Inicia a Thread
      p.start();
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
                 ArrayList<Roteador> roteadoresVisitados - lista de roteadores visitados
   * Retorno: void
   ****************************************************************/

  public void gerarMaisPacotes(Roteador origem, Roteador destino, Roteador vindoDe, ArrayList<Roteador> roteadoresVisitados) {
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
      subrede.getChildren().add(envelope);
      imagens.add(envelope);

      // Gera um novo pacote, define a sua posicao, o adiciona na lista de pacotes e incrementa a quantidade
      // de pacotes existentes
      Pacote p = criarPacote(envelope, origem, destino, vindoDe, roteadoresVisitados);
      p.definirPosicao();
      pacotes.add(p);
      incrementarPacotes();

      // Garante que a Thread seja interrompida caso a janela for fechada
      p.setDaemon(true);

      // Inicia a Thread
      p.start();
    }); // Fim do bloco Platform.runLater
  }

  private Pacote criarPacote(ImageView envelope, Roteador origem, Roteador destino, Roteador vindoDe, ArrayList<Roteador> roteadoresVisitados) {
    // Inicio do bloco switch/case
    // O pacote sera gerado conforme a versao do algoritmo de inundacao selecionada pelo usuario
    switch (versao) {
      case 0: // Retona um pacote para a versao 1.0
        return new Pacote(envelope, this.versao, origem, destino);
      case 1: // Retorna um pacote para a versao 2.0 (inclui a linha de saida pela qual ele chegou)
        return new Pacote(envelope, this.versao, origem, destino, vindoDe);
      case 2: // Retorna um pacote para a versao 3.0 (inclui a linha de saida pela qual ele chegou e o seu tempo de vida na rede)
        return new Pacote(envelope, this.versao, origem, destino, vindoDe, this.tempoDeVida);
      case 3: // Retorna um pacote para a versao 4.0 (inclui a linha de saida pela qual ele chegou, o seu tempo de vida na rede e a lista de roteadores visitados)
        return new Pacote(envelope, this.versao, origem, destino, vindoDe, this.tempoDeVida, roteadoresVisitados);
    } // Fim do bloco switch/case

    // Retorna nulo caso nenhuma das opcoes for atendida
    return null;
  }

  /*
   * ***************************************************************
   * Metodo: reiniciar
   * Funcao: reinicia a simulacao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	private void reiniciar() {
    // Desativa a simulacao
    simulacaoAtiva = false;

    // Inicio do bloco for
    for (Pacote p : pacotes) {
      // Interrompe a Thread de cada pacote ainda presente na rede
      p.interrupt();
    } // Fim do bloco for

    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Inicio do bloco for
      for (ImageView img : imagens) {
        // Remove cada imagem presente na interface
        subrede.getChildren().remove(img);
      } // Fim do bloco for

      // Esvazia a lista de imagens e pacotes gerados
      imagens.clear();
      pacotes.clear();

      // Inicio do bloco if
      // Se a origem tiver sido definida
      if (origem != null) {
        // Desmarca o roteador como origem, o atualiza e o esvazia
        origem.setOrigem(false);
        atualizarRoteador(origem);
        origem = null;
      } // Fim do bloco if

      // Inicio do bloco if
      // Se o destino tiver sido definido
      if (destino != null) {
        // Desmarca o roteador como destino, o atualiza e o esvazia
        destino.setDestino(false);
        atualizarRoteador(destino);
        destino = null;
      } // Fim do bloco if

      // Inicio do bloco try/catch
      try {
        // O processo e posto para dormir por 200 ms
        Thread.sleep(200);
      }
      catch (InterruptedException e) {
        // Em caso de excecao, a Thread e interrompida
        Thread.currentThread().interrupt();
      } // Fim do bloco try/catch
    }); // Fim do bloco Platform.runLater
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

    // Reinicia a simulacao
    reiniciar();

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

    // Zera a quantidade de pacotes presentes e a atualiza na interface
    numPacotes = 0;
    lblPacotes.setText(Integer.toString(numPacotes));

    // Inicio do bloco for
    for (Map.Entry<String, Circle> entrada : nosCriados.entrySet()) {
      // Obtem-se o rotulo e o no correspondente presentes no mapa
      String nome = entrada.getKey();
      Circle c = entrada.getValue();

      // Redefine a cor do contorno e o cursor do no
      c.setStroke(Color.BLACK);
      c.setCursor(Cursor.HAND);

      // Obtem-se o roteador do rotulo correspondente
      Roteador r = obterRoteador(nome);

      // Atualiza o no do roteador
      r.setNo(c);

      // Atualiza o roteador na lista de roteadores
      atualizarRoteador(r);
    } // Fim do bloco for
  }

  /*
   * ***************************************************************
   * Metodo: removerPacote
   * Funcao: remove um pacote da rede
   * Parametros: Pacote p - pacote a ser removido
   * Retorno: void
   ****************************************************************/

  public void removerPacote(Pacote p) {
    // Interrompe a Thread do pacote
    p.interrupt();

    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Obtem e remove a imagem do pacote da interface
      ImageView envelope = p.getEnvelope();
      subrede.getChildren().remove(envelope);

      // Remove o pacote da lista de pacotes
      pacotes.remove(p);
    }); // Fim do bloco Platform.runLater
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
        if (partes.length < 3) continue;

        // Obtem-se os rotulos dos nos e o peso da aresta
        String nome1 = partes[0];
        String nome2 = partes[1];
        String peso = partes[2];

        // Obtem-se os roteadores com base nos rotulos obtidos
        Roteador r1 = obterRoteador(nome1);
        Roteador r2 = obterRoteador(nome2);

        // Desenha a aresta se nenhum dos roteadores for nulo
        if (r1 != null && r2 != null) gerarAresta(r1, r2, peso);
      } // Fim do bloco while
    }
    catch (IOException e) {
      // Em caso de excecao, ela sera exibida no terminal
      // no instante em que o metodo for interrompido
      e.printStackTrace();
    } // Fim do bloco try/catch
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
   * Metodo: calcularPosicaoRoteadoress
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

    // Adiciona o circulo e o nome como chave no HashMap
    nosCriados.put(nome, circulo);

    // Adiciona o circulo na sub rede
    subrede.getChildren().addAll(circulo);

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
      label.setTextFill(Color.web("#2d4180"));
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
                 String peso - peso da aresta
   * Retorno: void
   ****************************************************************/

  private void gerarAresta(Roteador r1, Roteador r2, String peso) {
    // Cria-se uma String para identificar a aresta
    String idConexao = (r1.getNome().compareTo(r2.getNome()) < 0) ? r1.getNome() + r2.getNome() : r2.getNome() + r1.getNome();

    // Inicio do bloco if
    // Se a id nao se encontrar no Set de arestas existentes
    if (!arestasExistentes.contains(idConexao)) {
      // A linha e desenhada entre os nos de cada roteador
      Line linha = new Line(r1.getNo().getCenterX(), r1.getNo().getCenterY(), r2.getNo().getCenterX(), r2.getNo().getCenterY());
      linha.setStroke(Color.WHITE);
      linha.setStrokeWidth(1.0);

      // Adiciona a id da aresta
      arestasExistentes.add(idConexao);

      // Adiciona a linha na tela da sub rede
      subrede.getChildren().add(linha);

      // Os roteadores sao marcados como vizinhos um do outro
      r1.adicionarVizinho(r2);
      r2.adicionarVizinho(r1);
    } // Fim do bloco if
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
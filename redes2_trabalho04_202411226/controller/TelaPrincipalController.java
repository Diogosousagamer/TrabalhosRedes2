/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 02/05/2026
* Ultima alteracao.: 26/05/2026
* Nome.............: TelaPrincipalController
* Funcao...........: Classe que controla os eventos da TelaPrincipal.
                     
*************************************************************** */

package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
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
import model.Echo;
import model.EntradaTabela;
import model.Hello;
import model.Pacote;
import model.PacoteEstadoEnlace;
import model.Roteador;
import model.TabelaRoteamento;

public class TelaPrincipalController implements Initializable {
	// Componentes da interface
	@FXML private AnchorPane painelAlterarRede;
	@FXML private AnchorPane subrede;
	@FXML private Button btnAlterarRede;
  @FXML private Button btnAplicar;
  @FXML private Button btnCancelarEnvio;
  @FXML private Button btnEnviarPacote;
  @FXML private Button btnFecharAlterarRede;
  @FXML private Button btnReiniciar;
	@FXML private Button btnVoltar;
  @FXML private Label lblAviso;
	@FXML private Label lblCaminho;
	@FXML private Label lblDestino;
	@FXML private Label lblOrigem;
	@FXML private Label lblSelecao;
  @FXML private TabPane painelTabela;
	@FXML private TextArea txtBackbone;

	// Variaveis e instancias
  private Pacote p;
	public static volatile TelaPrincipalController controller;
  public static volatile boolean simulacaoAtiva;
  public static volatile boolean houveMudancaNaRede;
  private boolean removeuAresta;
  private boolean alterouSubRede;
  public static volatile boolean convergiu;
	private int quantidadeNos;
	private Roteador origem;
  private Roteador destino;
  private String modelo;
  private CopyOnWriteArrayList<Hello> hellos;
  private CopyOnWriteArrayList<Echo> echos;
  private CopyOnWriteArrayList<PacoteEstadoEnlace> pacotesEnlace;
  private CopyOnWriteArrayList<Roteador> roteadores;
  private HashMap<String, Long> latencias = new HashMap<>();
  private HashMap<String, Label> tempoArestas = new HashMap<>();
  private HashMap<String, Circle> nosCriados = new HashMap<>();
  private HashMap<String, Aresta> arestasExistentes = new HashMap<>();
  private HashMap<String, Label> labels = new HashMap<>();
  private Map<String, double[]> posicaoCirculos = new HashMap<>();

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
    // A simulacao comeca inativa
    simulacaoAtiva = false;

    // Carrega as ArrayLists que armazenarao os roteadores, os tempos de ida e volta das arestas, e 
    roteadores = new CopyOnWriteArrayList<>();
    hellos = new CopyOnWriteArrayList<>();
    echos = new CopyOnWriteArrayList<>();
    pacotesEnlace = new CopyOnWriteArrayList<>();

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
   * Metodo: hoverAresta
   * Funcao: altera a cor da aresta quando o mouse esta selecionando a linha
   * Parametros: MouseEvent event - evento gerado ao aproximar o mouse da linha
                 Line l - linha correspondente a aresta onde o mouse se posicionou
   * Retorno: void
   ****************************************************************/

  @FXML
  private void hoverAresta(MouseEvent event, Line l) {
    l.setStroke(Color.web("#9da1ad"));
  }

  /*
   * ***************************************************************
   * Metodo: exitAresta
   * Funcao: redefine a cor da aresta ao retirar a aresta do alcance do mouse
   * Parametros: MouseEvent event - evento gerado ao sair da linha
                 Line l - linha correspondente a aresta da qual o mouse saiu de alcance
   * Retorno: void
   ****************************************************************/

  @FXML
  private void exitAresta(MouseEvent event, Line l) {
    l.setStroke(Color.WHITE);
  }

  /*
   * ***************************************************************
   * Metodo: ocultarAresta
   * Funcao: oculta a aresta da sub rede, desabilitando-a quando o usuario
             clicar na linha
   * Parametros: MouseEvent event - evento gerado ao clicar na linha
                 Aresta a - aresta na qual o usuario clicou
   * Retorno: void
   ****************************************************************/

  @FXML
  private void ocultarAresta(MouseEvent event, Aresta a) {
    // Obtem os roteadores ligados pela aresta
    Roteador r1 = a.getR1();
    Roteador r2 = a.getR2();

    // Inicio do bloco if
    if (simulacaoAtiva) {
      // Os roteadores param de se tornar vizinhos caso a simulacao estiver ativa
      // (isso porque os roteadores nao poderao ser reescritos)
      r1.removerVizinho(r2);
      r2.removerVizinho(r1);
    } // Fim do bloco if

    // Obtem o nome dos roteadores, bem como o tempo de ida e volta da aresta
    String nome1 = r1.getNome();
    String nome2 = r2.getNome();
    String latencia = Long.toString(a.getLatencia());

    // Remonta a linha correspondente a aresta com base na formatacao do backbone
    String linha = nome1 + "," + nome2 + "," + latencia;

    // Acessa o backbone e cria uma ArrayList para armazenar as linhas que sobrarem do backbone
    File backbone = new File("backbone.txt");
    ArrayList<String> linhasRestantes = new ArrayList<>();

    // Inicio do bloco try/catch
    try {
      // Inicio do bloco if
      if (backbone.exists()) {
        Files.lines(backbone.toPath()).forEach(l -> {
          // Inicio do bloco if
          if (!l.trim().equals(linha)) {
            // Adiciona apenas as linhas que nao correspondem a aresta a ser removida
            linhasRestantes.add(l);
          } // Fim do bloco if
        });
      } // Fim do bloco if

      // Escreve as linhas restantes no arquivo
      Files.write(backbone.toPath(), linhasRestantes);

      // Remove a sub rede para que ela possa ser reconfigurada
      removerSubrede();
    }
    catch (IOException e) {
      // Em caso de excecao, ela eh exibida no terminal
      e.printStackTrace();
    } // Fim do bloco try/catch
  }

  /*
   * ***************************************************************
   * Metodo: enviarPacote
   * Funcao: configura a sub rede para que o pacote seja enviado
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void enviarPacote(ActionEvent event) {
    // Inicio do bloco if
    if (simulacaoAtiva) {
      // Thread que exibe um aviso caso as tabelas nao estiverem completas
      Thread aviso = new Thread(() -> {
        lblAviso.setVisible(true);
        dormir(2000);
        lblAviso.setVisible(false);
      });

      // Inicia a Thread, garantindo que as operacoes sejam encerradas
      // caso o programa for fechado
      aviso.setDaemon(true);
      aviso.start();

      // Sai do metodo
      return;
    } // Fim do bloco if

    // Inicio do bloco for
    for (Aresta a : arestasExistentes.values()) {
      // Impede que a aresta seja removida durante a selecao
      Line l = a.getLinha();
      l.setMouseTransparent(true);
    } // Fim do bloco for

    // Inicio do bloco for
    for (Circle c : nosCriados.values()) {
      c.setMouseTransparent(false);
    } // Fim do bloco for

    // Desabilita o botao para acessar e alterar o backbone
    btnAlterarRede.setDisable(true);

    // Oculta o botao de envio e exibe o botao de cancelamento
    btnEnviarPacote.setVisible(false);
    btnCancelarEnvio.setVisible(true);

    // Exibe a selecao
    lblSelecao.setVisible(true);
  } 

  /*
   * ***************************************************************
   * Metodo: cancelarEnvio
   * Funcao: cancela o envio de um novo pacote na sub rede
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void cancelarEnvio(ActionEvent event) {
    // Inicio do bloco for
    for (Aresta a : arestasExistentes.values()) {
      // Impede que a aresta seja removida durante a selecao
      Line l = a.getLinha();
      l.setMouseTransparent(false);
    } // Fim do bloco for

    // Inicio do bloco for
    for (Circle c : nosCriados.values()) {
      // Reseta os nos
      c.setStroke(Color.BLACK);
      c.setMouseTransparent(true);
    } // Fim do bloco for 

    // Inicio do bloco if
    if (origem != null) {
      // Anula a origem caso ela tiver sido definida
      origem = null;
      lblOrigem.setText("");
    } // Fim do bloco if

    // Oculta a selecao
    lblSelecao.setVisible(false);

    // Habilita o botao para acessar e alterar o backbone
    btnAlterarRede.setDisable(false);

    // Exibe o botao de envio e oculta o botao de cancelamento
    btnEnviarPacote.setVisible(true);
    btnCancelarEnvio.setVisible(false);
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

      // Oculta o botao de cancelamento do envio
      btnCancelarEnvio.setVisible(false);

      // Cria o pacote para ser enviado caso a origem nao for nula
      if (origem != null) criarPacote();
    }
    else if (existeOrigem() && existeDestino()) {
      // Interrompe o metodo se uma origem e um destino ja tiverem
      // sido definidos
      return;
    } // Fim do bloco if/else if/else if
  }

  /*
   * ***************************************************************
   * Metodo: criarPacote
   * Funcao: cria o pacote a ser transportado na sub rede
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void criarPacote() {
    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Inicializa uma nova imagem representando o pacote
      Image mail = new Image(getClass().getResource("/img/Envelope.png").toExternalForm());

      // Configura a imagem do pacote e a adiciona na sub rede
      ImageView envelope = new ImageView(mail);
      envelope.setFitWidth(41);
      envelope.setFitHeight(98);
      envelope.setPreserveRatio(true);
      envelope.setVisible(false);
      subrede.getChildren().add(envelope);

      // Inicializa uma nova Thread correspondente ao pacote
      p = new Pacote(envelope, origem, destino);
      p.setDaemon(true);
      p.start();

      // Roda a montagem do caminho em uma Thread
      Thread caminho = new Thread(() -> {
        obterCaminhoFinal();
      });

      // Inicializa a Thread do caminho, garantindo que ela seja encerrada
      // caso o programa for fechado abruptamente
      caminho.setDaemon(true);
      caminho.start();
    }); // Fim do bloco Platform.runLater
  }

  /*
   * ***************************************************************
   * Metodo: iniciarRoteadores
   * Funcao: inicializa os roteadores para calcularem o roteamento
             por estado de enlace
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void iniciarRoteadores() {
    // Marca a simulacao como ativa
    simulacaoAtiva = true;

    // Inicio do bloco for
    for (Circle c : nosCriados.values()) {
      // Impede que os circulos sejam clicados
      c.setMouseTransparent(true);
    } // Fim do bloco for

    // Inicio do bloco for
    for (Roteador r : roteadores) {
      // Inicia a operacao dos roteadores passando a lista completa
      // de roteadores presentes na topologia
      r.setListaRoteadores(roteadores);
      r.setDaemon(true);
      r.start();
      dormir(100);
    } // Fim do bloco for

    // Inicio do bloco Thread
    Thread pausa = new Thread(() -> {
      // Inicio do bloco while
      // Enquanto alguma tabela estiver incompleta
      while (simulacaoAtiva) {
        // Inicio do bloco try/catch
        try {
          // Poe a Thread para dormir por 200 ms
          Thread.sleep(200);
        }
        catch (InterruptedException e) {
          // Em caso de excecao, a Thread e interrompida
          Thread.currentThread().interrupt();
        } // Fim do bloco try/catch
      } // Fim do bloco while
    }); // Fim do bloco Thread

    // Inicia a Thread, com a garantia de que ela seja encerrada
    // caso o programa seja fechado
    pausa.setDaemon(true);
    pausa.start();
  }

  /*
   * ***************************************************************
   * Metodo: enviarHello
   * Funcao: envia um pacote Hello dentro da sub rede
   * Parametros: Roteador origem - ponto de origem do pacote
                 Roteador destino - ponto de destino do pacote
   * Retorno: Hello
   ****************************************************************/

  public Hello enviarHello(Roteador origem, Roteador destino) {
    ImageView h = new ImageView();

    Hello hello = new Hello(h, origem, destino);
    hello.setDaemon(true);

    Platform.runLater(() -> {
      Image img = new Image(getClass().getResource("/img/hello.png").toExternalForm());
      h.setImage(img);
      h.setFitWidth(50);
      h.setFitHeight(30);
      h.setPreserveRatio(true);
      subrede.getChildren().add(h);

      hello.start();
      hellos.add(hello);
    });

    return hello;
  }

  /*
   * ***************************************************************
   * Metodo: removerHello
   * Funcao: remove um pacote Hello de dentro da sub rede
   * Parametros: Hello h - pacote a ser removido
   * Retorno: void
   ****************************************************************/

  public void removerHello(Hello h) {
    Platform.runLater(() -> {
      h.setChegou(true);
      h.interrupt();
      ImageView img = h.getHello();

      subrede.getChildren().remove(img);
      if (hellos.contains(h)) hellos.remove(h);
    });
  }

  /*
   * ***************************************************************
   * Metodo: enviarEcho
   * Funcao: envia um novo pacote echo dentro da sub rede
   * Parametros: Roteador origem - roteador de origem do pacote
                 Roteador destino - roteador para o qual o pacote sera destinado
   * Retorno: Echo
   ****************************************************************/

  public Echo enviarEcho(Roteador origem, Roteador destino) {
    // Cria uma nova imagem para o pacote
    ImageView request = new ImageView();

    // Instancia um novo pacote echo
    Echo e = new Echo(origem, destino, request);
    e.setDaemon(true);

    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Configura a imagem do pacote e a adiciona na sub rede
      Image echo = new Image(getClass().getResource("/img/Echo.png").toExternalForm());
      request.setImage(echo);
      request.setFitWidth(21);
      request.setFitHeight(61);
      request.setPreserveRatio(true);
      subrede.getChildren().add(request);

      // Inicializa o pacote de solicitacao
      e.start();

      // Adiciona o pacote de solicitacao na lista
      echos.add(e);
    }); // Fim do bloco Platform.runLater

    // Retorna o pacote
    return e;
  }

  /*
   * ***************************************************************
   * Metodo: removerEcho
   * Funcao: remove um pacote echo da sub rede
   * Parametros: Echo e - pacote echo a ser removido
   * Retorno: void
   ****************************************************************/

  public void removerEcho(Echo e) {
    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Encerra o pacote, interrompe a Thread e remove a imagem da sub rede
      e.setEncerrou(true);
      e.interrupt();
      ImageView envelope = e.getEnvelope();
      subrede.getChildren().remove(envelope);

      // Remove o pacote de solicitacao da lista
      if (echos.contains(e)) echos.remove(e);
    }); // Fim do bloco Platform.runLater
  }

  /*
   * ***************************************************************
   * Metodo: enviarPacoteEnlace
   * Funcao: envia um novo pacote de estado de enlace dentro da sub rede
   * Parametros: Roteador origem - roteador de origem do pacote
                 Roteador destino - roteador para o qual o pacote sera destinado
   * Retorno: Echo
   ****************************************************************/

  public PacoteEstadoEnlace enviarPacoteEnlace(Roteador origem, Roteador destino, Roteador linhaChegada, int numeroSequencia, int idade) {
    ImageView enlace = new ImageView();

    PacoteEstadoEnlace pacoteEnlace = new PacoteEstadoEnlace(enlace, numeroSequencia, idade, origem, destino, linhaChegada);
    pacoteEnlace.setDaemon(true);

    Platform.runLater(() -> {
      Image olho = new Image("/img/link.png");
      enlace.setImage(olho);
      enlace.setFitWidth(30);
      enlace.setFitHeight(61);
      enlace.setPreserveRatio(true);
      subrede.getChildren().add(enlace);

      pacotesEnlace.add(pacoteEnlace);
    });

    return pacoteEnlace;
  }

  public void removerPacoteEnlace(PacoteEstadoEnlace pacoteEnlace) {
    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Encerra o pacote, interrompe a Thread e remove a imagem da sub rede
      pacoteEnlace.interrupt();
      ImageView olho = pacoteEnlace.getLink();
      subrede.getChildren().remove(olho);

      // Remove o pacote de estado de enlace da lista
      if (pacotesEnlace.contains(pacoteEnlace)) pacotesEnlace.remove(pacoteEnlace);
    }); // Fim do bloco Platform.runLater
  }

  public synchronized boolean verificarEncontrouVizinhos() {
    for (Roteador r : roteadores) {
      if (!r.encontrouVizinhos()) {
        return false;
      }
    }

    return true;
  }

  public synchronized boolean verificarMediuRetardos() {
    for (Roteador r : roteadores) {
      if (!r.mediuRetardos()) {
        return false;
      }
    }

    return true;
  }

  public synchronized boolean verificarPacotesEnlace() {
    if (!pacotesEnlace.isEmpty()) return false;
    return true;
  }

  /*
   * ***************************************************************
   * Metodo: verificarTabelasCompletas
   * Funcao: verifica se as tabelas de todos os roteadores estao completas
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  public synchronized boolean verificarTabelasCompletas() {
    // Inicio do bloco for
    for (Roteador r : roteadores) {
      // Retorna falso caso alguma tabela nao estiver completa
      if (!r.isTabelaCompleta()) return false;
    } // Fim do bloco for

    // Retorna verdadeiro caso nenhuma tabela estiver incompleta
    return true;
  }

  /*
   * ***************************************************************
   * Metodo: obterCaminhoFinal
   * Funcao: monta o caminho final a ser percorrido pelo pacote
   * Parametros: Pacote p - pacote cujo caminho sera montado
   * Retorno: void
   ****************************************************************/

  private void obterCaminhoFinal() {
    // Comeca pela origem
    Roteador passo = origem;

    // Quantidade maxima de iteracoes
    int passos = roteadores.size();

    // Contador de iteracoes
    int contador = 0;
    
    // Inicio do bloco while
    // Enquanto o passo for nulo e nao corresponder ao destino
    while (passo != null && !passo.getNome().equals(destino.getNome())) {
      // Inicio do bloco if
      if (contador > passos) {
        // Interrompe a montagem do caminho em caso de formacao de ciclo
        erroCicloCaminho();
        break;
      } // Fim do bloco if

      // Obtem a tabela de roteamento do roteador para rastrear a linha referente ao destino
      TabelaRoteamento tabela = passo.getTabela();
      EntradaTabela entradaDestino = tabela.obterEntrada(destino.getNome());

      // Inicio do bloco if
      if (entradaDestino == null || entradaDestino.getLinhaSaida().equals("-")) {
        // Interrompe a montagem do caminho caso nao encontrar uma linha de saida para este destino
        erroRota(passo);
        break;
      } // Fim do bloco if

      // Obtem a linha de saida (direta ou indireta) para o destino
      String linhaSaida = entradaDestino.getLinhaSaida().trim();
      Roteador saida = obterRoteador(linhaSaida);

      // Inicio do bloco if
      if (saida == null) {
        // Interrompe a montagem do caminho caso o roteador da linha de saida nao for encontrado
        erroRota(passo);
        break;
      } // Fim do bloco if

      // Obtem-se a aresta do caminho
      Aresta a = obterAresta(passo, saida);

      // Armazena o roteador atual e o roteador de saida em constantes
      final Roteador rPasso = passo;
      final Roteador rSaida = saida;

      // Inicio do bloco if/else
      // Se a aresta nao for nula
      if (a != null) {
        // Inicio do bloco Platform.runLater
        Platform.runLater(() -> {
          // Adiciona o roteador ao caminho, concatena a label de caminho 
          // e marca a aresta a ser percorrida
          this.p.adicionarRoteadorAoCaminho(rSaida);
          concatenarCaminho(rPasso);
          a.marcarParteCaminho();
        }); // Fim do bloco Platform.runLater

        // Coloca o processo para dormir por 500 ms
        dormir(500);

        // O roteador a ser visitado sera a linha de saida obtida
        passo = saida;

        // Incrementa o contador de iteracoes
        contador++;

        // Inicio do bloco if
        if (passo.getNome().equals(destino.getNome())) {
          // Concatena o passo atual no caminho caso ele for o destino
          final Roteador passoFinal = passo;
          Platform.runLater(() -> concatenarCaminho(passoFinal));

          // Coloca o processo para dormir por 500 ms
          dormir(500);
        } // Fim do bloco if
      }
      else {
        // Interrompe a montagem do caminho e emite um erro caso a aresta for nula
        erroRota(passo);
        break;
      } // Fim do bloco if/else
    } // Fim do bloco while

    // Libera o pacote para percorrer o caminho
    p.liberar();
  }

  /*
   * ***************************************************************
   * Metodo: erroCicloCaminho
   * Funcao: sinaliza um erro na montagem do caminho no terminal
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void erroCicloCaminho() {
    // Emite o erro no terminal e interrompe a simulacao
    System.out.println("Ciclo encontrado ou caminho inacessível!");
    TelaPrincipalController.controller.interromper(this.p);
  }

  /*
   * ***************************************************************
   * Metodo: erroRota
   * Funcao: sinaliza um erro caso nao tenha sido encontrada uma rota para o destino 
             a partir de certo roteador
   * Parametros: Roteador passo - roteador onde nenhuma rota foi encontrada
   * Retorno: void
   ****************************************************************/

  private void erroRota(Roteador passo) {
    // Emite o erro no terminal e interrompe a simulacao
    System.out.println("Nenhuma rota encontrada para o destino em: " + passo.getNome());
    TelaPrincipalController.controller.interromper(this.p);
  }

  /*
   * ***************************************************************
   * Metodo: obterAresta
   * Funcao: obtem uma aresta especifica dentro do grafo
   * Parametros: Roteador r1 - primeiro roteador
                 Roteador r2 - segundo roteador
   * Retorno: Aresta
   ****************************************************************/

  private Aresta obterAresta(Roteador r1, Roteador r2) {
    // Obtem a id da aresta e retona a aresta correspondente dentro do HashMap
    String id = (r1.getNome().compareTo(r2.getNome()) < 0) ? r1.getNome() + r2.getNome() : r2.getNome() + r1.getNome();
    return arestasExistentes.get(id);
  }

  /*
   * ***************************************************************
   * Metodo: dormir
   * Funcao: poe o processo para dormir
   * Parametros: long valor - valor de sono
   * Retorno: void
   ****************************************************************/

  private void dormir(long valor) {
    // Inicio do bloco try/catch
    try {
      // O processo eh posto para dormir por um certo tempo 
      // (determinado pelo valor passado como parametro)
      Thread.sleep(valor);
    }
    catch (InterruptedException e) {
      // Em caso de excecao, o processo eh interrompido
      Thread.currentThread().interrupt();
    } // Fim do bloco try/catch
  }

  /*
   * ***************************************************************
   * Metodo: concatenarCaminho
   * Funcao: monta o caminho a ser percorrido na Label
   * Parametros: Roteador r - roteador a ser adicionado no caminho
   * Retorno: void
   ****************************************************************/

  private void concatenarCaminho(Roteador r) {
    // Obtemos o texto atual
    String textoAtual = lblCaminho.getText();

    // Gera um novo trecho (a seta e adicionada se o roteador nao corresponder ao destino)
    String novoTrecho = (!r.isDestino()) ? r.getNome() + " -> " : r.getNome();

    // Exibe o novo trecho no inicio junto com o texto anterior
    lblCaminho.setText(textoAtual + novoTrecho);
  }

  /*
   * ***************************************************************
   * Metodo: interromper
   * Funcao: interrompe o pacote e o remove da sub rede
   * Parametros: Pacote p - pacote a ser interrompido
   * Retorno: void
   ****************************************************************/

  public void interromper(Pacote p) {
    // Interrompe o pacote
    p.interrupt();

    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Remove a imagem do pacote
      ImageView envelope = p.getEnvelope();
      subrede.getChildren().remove(envelope);

      // Exibe o botao para reiniciar a topologia
      btnReiniciar.setVisible(true);
    }); // Fim do bloco Platform.runLater
  }

  /*
   * ***************************************************************
   * Metodo: reiniciar
   * Funcao: reinicia a topologia apos o fim da simulacao
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void reiniciar(ActionEvent event) {
    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Anula o pacote
      p = null;

      // Oculta o botao de reinicio e exibe o botao para alterar a topologia
      btnReiniciar.setVisible(false);
      btnAlterarRede.setDisable(false);

      // Oculta a origem e o destino na Label
      lblOrigem.setText("");
      lblDestino.setText("");

      // Oculta o caminho percorrido
      lblCaminho.setText("");
      lblCaminho.setVisible(false);

      // Exibe o botao de envio do pacote
      btnEnviarPacote.setVisible(true);

      // Inicio do bloco for
      for (Roteador r : roteadores) {
        // Interrompe a Thread
        r.interrupt();

        // Desmarca o roteador como origem e/ou destino do percurso
        if (r.isOrigem()) r.setOrigem(false);
        if (r.isDestino()) r.setDestino(false);

        // Permite que o no seja clicavel novamente
        Circle c = r.getNo();
        c.setMouseTransparent(false);

        // Reseta a cor do no
        r.resetarNo();

        // Reseta as entradas da tabela e a desmarca como completa
        r.resetarEntradas();
        r.setTabelaCompleta(false);

        // Atualiza o roteador na lista e nos vizinhos
        atualizarRoteador(r);
        alterarRoteadorNosVizinhos(r);
      } // Fim do bloco for
 
      // Inicio do bloco for
      for (Aresta a : arestasExistentes.values()) {
        // Reseta as linhas apos a finalizacao do caminho
        a.resetarLinha();
        Line l = a.getLinha();
        l.setMouseTransparent(false);
      } // Fim do bloco for

      // Anula a origem e o destino
      origem = null;
      destino = null;

      // Remove a sub rede para iniciar a simulacao novamente
      removerSubrede();
    }); // Fim do bloco Platform.runLater
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

      // Para a simulacao e marca que a sub rede foi alterada 
      // para reiniciar a simulacao
      simulacaoAtiva = false;
      alterouSubRede = true;

      // Inicio do bloco for
      for (Roteador r : roteadores) {
        // Interrompe os roteadores
        r.interrupt();
      } // Fim do bloco for

      if (!hellos.isEmpty()) {
        for (Hello h : hellos) {
          h.interrupt();
        }
      }

      // Inicio do bloco if
      if (!echos.isEmpty()) {
        // Inicio do bloco for
        for (Echo e : echos) {
          // Interrompe os pacotes de solicitacao
          e.interrupt();
        } // Fim do bloco for
      } // Fim do bloco if

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

      // Inicio do bloco if
      if (!simulacaoAtiva || alterouSubRede) {
        // Gera os nos, labels e roteadores se a simulacao nao estiver ativa e/ou a sub rede for alterada
        // atraves do backbone
        calcularPosicaoNos(quantidadeNos);
        gerarLabels(quantidadeNos);
        criarRoteadores();
        calcularPosicaoLabels(quantidadeNos);
        calcularPosicaoRoteadores(quantidadeNos);
      } // Fim do bloco if

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

        // Obtem-se os roteadores com base nos rotulos obtidos
        Roteador r1 = obterRoteador(nome1);
        Roteador r2 = obterRoteador(nome2);

        // Desenha a aresta se nenhum dos roteadores for nulo
        if (r1 != null && r2 != null) gerarAresta(r1, r2);
      } // Fim do bloco while

      // Joga os roteadores para a frente da sub rede
      jogarRoteadoresParaFrente();

      // Inicio do bloco if
      if (!simulacaoAtiva || alterouSubRede) {
        // Marca a flag de alteracao na sub rede como falsa
        if (alterouSubRede) alterouSubRede = false;

        // Cria as tabelas e inicia os roteadores
        criarTabelas();
        iniciarRoteadores();
      } // Fim do bloco if
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
    // Reseta qualquer mudanca que houver na sub rede
    houveMudancaNaRede = false;

    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Lista que armazenara os itens a serem removidos da sub rede
      ArrayList<Node> itensParaRemover = new ArrayList<>();

      // Inicio do bloco for
      for (Aresta a : arestasExistentes.values()) {
        // Marca as arestas para serem removidas
        itensParaRemover.add(a.getLinha());
      } // Fim do bloco for

      // Inicio do bloco for
      for (Label t : tempoArestas.values()) {
        // Marca os tempos de ida e volta das arestas para serem removidos
        itensParaRemover.add(t);
      } // Fim do bloco for

      // Inicio do bloco if
      // Se a simulacao nao estiver ativa ou a sub rede tiver sido alterada
      if (!simulacaoAtiva || alterouSubRede) {
        // Inicio do bloco for
        for (Map.Entry<String, Circle> entrada : nosCriados.entrySet()) {
          // Marca os nos presentes na topologia da subrede para serem removidos
          itensParaRemover.add(entrada.getValue());
        } // Fim do bloco for

        // Inicio do bloco for  
        for (Map.Entry<String, Label> entrada : labels.entrySet()) {
          // Marca as labels para serem removidas
          itensParaRemover.add(entrada.getValue());
        } // Fim do bloco for

        if (!hellos.isEmpty()) {
          for (Hello h : hellos) {
            ImageView hello = h.getHello();
            itensParaRemover.add(hello);
          }
        }
  
        // Inicio do bloco if
        // Se a lista de pacotes de solicitacao nao estiver vazia
        if (!echos.isEmpty()) {
          // Inicio do bloco for
          for (Echo e : echos) {
            // Marca a imagem do pacote de solicitacao para ser removida
            ImageView echo = e.getEnvelope();
            itensParaRemover.add(echo);
          } // Fim do bloco for
        } // Fim do bloco if
      } // Fim do bloco if

      // Remove a sub rede e limpa as listas logicas
      subrede.getChildren().removeAll(itensParaRemover);
      limparListas();

      // Reconfigura a sub rede
      configurarSubrede();
    }); // Fim do bloco Platform.runLater
  }

  /*
   * ***************************************************************
   * Metodo: limparListas
   * Funcao: esvazia as listas contendo os componentes da sub rede
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void limparListas() {
    // Inicio do bloco if
    if (!simulacaoAtiva || alterouSubRede) {
      // Estes itens sao removidos apenas se a simulacao nao estiver ativa
      // e/ou o backbone da sub rede for alterado em algum momento
      roteadores.clear();
      hellos.clear(); 
      echos.clear();
      painelTabela.getTabs().clear();
      nosCriados.clear();
      posicaoCirculos.clear();
      labels.clear();
    } // Fim do bloco if

    // Remove as arestas e os seus retardos (ida e volta)
    // em qualquer caso
    arestasExistentes.clear();
    tempoArestas.clear();
  }

  public synchronized long gerarLatenciaAleatoria(Roteador r1, Roteador r2) {
    String id = (r1.getNome().compareTo(r2.getNome()) < 0) ? r1.getNome() + r2.getNome() : r2.getNome() + r1.getNome();
    long latencia = ThreadLocalRandom.current().nextLong(1, 501);

    if (latencias.containsKey(id)) return latencias.get(id);
    latencias.put(id, latencia);

    Aresta a = obterAresta(r1, r2);
    a.setLatencia(latencia);

    final String backbone = "backbone.txt";
    ArrayList<String> linhas = new ArrayList<>();

    try {
      BufferedReader br = new BufferedReader(new FileReader(backbone));
      String linha = "";

      while ((linha = br.readLine()) != null) {
        String[] partes = linha.split(",");

        if (partes.length < 3) {
          linhas.add(linha);
          continue;
        }

        String origem = partes[0];
        String destino = partes[1];

        boolean ehArestaAlvo = (origem.equals(r1.getNome()) && destino.equals(r2.getNome())) ||
                               (origem.equals(r2.getNome()) && destino.equals(r1.getNome()));

        if (ehArestaAlvo) {
          String novaLinha = origem + "," + destino + "," + latencia;
          linhas.add(novaLinha);
        }
        else {
          linhas.add(linha);
        }
      }

      BufferedWriter bw = new BufferedWriter(new FileWriter(backbone));

      for (String l : linhas) {
        bw.write(l);
        bw.newLine();
      } 

      bw.flush();
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    return latencia;
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

    // Adiciona o circulo e o nome como chave no HashMap
    nosCriados.put(nome, circulo);

    // Adiciona o circulo/no na sub rede
    subrede.getChildren().add(circulo);

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
      label.setTextFill(Color.web("#ae11cd"));
      label.setFont(Font.font("VCR OSD Mono", 15));
      labels.put(nome, label);
    } // Fim do bloco for
  }
 
  /*
   * ***************************************************************
   * Metodo: criarRoteadores
   * Funcao: cria os roteadores presentes na topologia da sub rede
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void criarRoteadores() {
    // Inicio do bloco if
    // Se a quantidade de nos presentes na rede nao for nula
    if (quantidadeNos != 0) {
      // Inicio do bloco for
      for (int i = 0; i < quantidadeNos; i++) {
        // Gera o rotulo de acordo com o indice
        String nome = gerarNome(i);

        // Cria o no correspondente ao rotulo gerado
        Circle c = criarNo(nome);

        // Cria uma nova instancia do roteador
        Roteador r = criarRoteador(c, nome);
      } // Fim do bloco for
    } // Fim do bloco if
  }

  /*
   * ***************************************************************
   * Metodo: gerarAresta
   * Funcao: desenha a aresta existente entre dois roteadores
   * Parametros: Roteador r1 - roteador de origem
                 Roteador r2 - roteador de destino
                 String latencia - latencia da aresta
   * Retorno: void
   ****************************************************************/

  private void gerarAresta(Roteador r1, Roteador r2) {
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

      // Cria uma nova instancia de aresta
      Aresta aresta = new Aresta(linha, r1, r2);

      // Adiciona os devidos eventos a linha
      linha.setOnMouseClicked(event -> {
        ocultarAresta(event, aresta);
      });

      linha.setOnMouseEntered(event -> {
        hoverAresta(event, linha);
      });

      linha.setOnMouseExited(event -> {
        exitAresta(event, linha);
      });

      // Atualiza a linha dentro da aresta
      aresta.setLinha(linha);

      // Deixa a aresta desativada por padrao
      aresta.desativarAresta();

      // Adiciona dentro das extremidades a serem percorridas
      r1.adicionarExtremidade(aresta);
      r2.adicionarExtremidade(aresta);

      // Coloca a aresta dentro do HashMap
      arestasExistentes.put(idConexao, aresta);
    } // Fim do bloco if
  }

  /*
   * ***************************************************************
   * Metodo: criarTabelas
   * Funcao: cria as tabelas de roteamento para cada roteador
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void criarTabelas() {
    // Inicio do bloco for
    for (Roteador r : roteadores) {
      // Cria a tab correspondente ao roteador e a adiciona no painel
      Tab t = new Tab(r.getNome());
      painelTabela.getTabs().add(t);

      // Cria a tabela para o roteador
      TableView<EntradaTabela> tabela = new TableView<>();

      // Configura o CSS da tabela
      tabela.getStyleClass().add("table-view");
      String css = getClass().getResource("/util/trilha.css").toExternalForm();
      tabela.getStylesheets().add(css);
      
      // Cria as colunas da tabela (linha de destino, linha de saida, linha de retardo)
      TableColumn<EntradaTabela, String> destino = new TableColumn<>("Para");
      destino.setCellValueFactory(new PropertyValueFactory<>("destino"));
      centralizarColuna(destino);

      TableColumn<EntradaTabela, String> saida = new TableColumn<>("Saida");
      saida.setCellValueFactory(new PropertyValueFactory<>("linhaSaida"));
      centralizarColuna(saida);

      TableColumn<EntradaTabela, String> retardo = new TableColumn<>("Retardo");
      retardo.setCellValueFactory(new PropertyValueFactory<>("retardo"));
      centralizarColuna(retardo);
 
      // Adiciona as colunas na tabela
      tabela.getColumns().addAll(destino, saida, retardo);

      // Garante que 
      tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

      // Adiciona a tabela na tab
      t.setContent(tabela);

      // Instancia a tabela de roteamento e a adiciona no roteador
      TabelaRoteamento tab = new TabelaRoteamento(r, tabela);
      r.setTabela(tab);
      atualizarRoteador(r);
      alterarRoteadorNosVizinhos(r);
    } // Fim do bloco for
  }

  /*
   * ***************************************************************
   * Metodo: centralizarColuna
   * Funcao: centraliza o campo de texto de uma coluna
   * Parametros: TableColumn<S,T> coluna - coluna a ser centralizada
   * Retorno: void
   ****************************************************************/

  private <S, T> void centralizarColuna(TableColumn<S, T> coluna) {
    // Metodo responsavel por alinhar as celulas da coluna no centro
    coluna.setCellFactory(tc -> new TableCell<S, T>() {
      @Override
      protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        // Inicio do bloco if/else
        if (item == null || empty) {
          // O texto fica nulo se o tipo de dado nao for informado
          setText(null);
        } 
        else {
          // Caso contrario, converte o item em string e o alinha no centro
          setText(item.toString());
          setStyle("-fx-alignment: CENTER;");
        } // Fim do bloco if/else
      }
    });
  }

  public void inserirRetardo(Roteador r1, Roteador r2, long latencia) {
    // Obtem a id da aresta e verifica se os retardos dela ja estao registrados na 
    String idAresta = (r1.getNome().compareTo(r2.getNome()) < 0) ? r1.getNome() + r2.getNome() : r2.getNome() + r1.getNome();
    if (tempoArestas.containsKey(idAresta)) return;

    // Gera as labels de ida e volta da aresta
    Label lblTempo = new Label(Long.toString(latencia));
    lblTempo.setFont(Font.font("VCR OSD Mono", 13));
    lblTempo.setTextFill(Color.web("#f5e940"));

    // Calcula a posicao media do peso a partir do centro dos nos
    double xMedio = (r1.getNo().getCenterX() + r2.getNo().getCenterX()) / 2;
    double yMedio = (r1.getNo().getCenterY() + r2.getNo().getCenterY()) / 2;

    // Define a posicao do peso
    lblTempo.setLayoutX(xMedio);
    lblTempo.setLayoutY(yMedio);

    // Adiciona uma translacao para garantir que fique alinhado
    lblTempo.setTranslateX(-7);
    lblTempo.setTranslateY(-7);

    // Adiciona a label dentro do HashMap de pesos e da sub rede
    tempoArestas.put(idAresta, lblTempo);
    subrede.getChildren().add(lblTempo);
  }

  /*
   * ***************************************************************
   * Metodo: jogarRoteadoresParaFrente
   * Funcao: posiciona os roteadores acima das arestas
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void jogarRoteadoresParaFrente() {
    // Inicio do bloco for
    for (Circle c : nosCriados.values()) {
      // Joga os nos dos roteadores para frente
      c.toFront();
    } // Fim do bloco for

    // Inicio do bloco for
    for (Label l : labels.values()) {
      // Joga as labels dos roteadores para frente
      l.toFront();
    } // Fim do bloco for
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

  public Roteador obterRoteador(String nome) {
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

  public void atualizarRoteador(Roteador r) {
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

    // Inicio do bloco for
    for (Roteador rot : roteadores) {
      // Atualiza as listas de roteadores de cada roteador
      rot.setListaRoteadores(roteadores);
    } // Fim do bloco for
  }

  /*
   * ***************************************************************
   * Metodo: alterarRoteadorNosVizinhos
   * Funcao: altera a instancia do roteador nos roteadores em que ele for vizinho
   * Parametros: Roteador r - roteador a ser atualizado
   * Retorno: void
   ****************************************************************/

  public void alterarRoteadorNosVizinhos(Roteador r) {
    // Inicio do bloco for
    // Percorremos cada roteador existente na lista de roteadores
    for (int i = 0; i < roteadores.size(); i++) {
      // Obtem o roteador do instante atual
      Roteador rot = roteadores.get(i);

      // Evita atualizar o roteador em uma lista de vizinhos vazia
      if (rot.getVizinhos().isEmpty()) continue;

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
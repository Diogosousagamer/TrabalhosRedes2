/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 29/03/2026
* Ultima alteracao.: 08/04/2026
* Nome.............: TelaPrincipalController
* Funcao...........: Classe que controla os eventos da TelaPrincipal.
                     
*************************************************************** */

package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.Thread;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import javafx.scene.control.TextArea;
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
import model.Roteador;
import model.Pacote;

public class TelaPrincipalController implements Initializable {
	// Componentes da interface
  @FXML private AnchorPane painelAlterarRede;
  @FXML private AnchorPane painelInterrupcao;
  @FXML private AnchorPane popupAlterarRede;
	@FXML private AnchorPane subrede;
  @FXML private Button btnAlterarRede;
  @FXML private Button btnAplicar;
  @FXML private Button btnContinuar;
  @FXML private Button btnFechar;
	@FXML private Button btnVoltar;
  @FXML private Label lblCaminho;
  @FXML private Label lblOrigem;
  @FXML private Label lblDestino;
  @FXML private Label lblNoAtivo;
  @FXML private Label lblResultados;
	@FXML private Label lblSelecao;
  @FXML private TextArea txtBackbone;
  @FXML private VBox listaNos;

	// Variaveis e instancias
	public static volatile TelaPrincipalController controller;
	private int quantidadeNos;
	private Roteador origem;
  private Roteador destino;
  private String modelo;
  private ArrayList<Label> pesosArestas;
  private ArrayList<Roteador> roteadores;
  private HashMap<String, Circle> nosCriados = new HashMap<>();
  private Map<String, double[]> posicaoCirculos = new HashMap<>();
  private HashMap<String, Aresta> arestasExistentes = new HashMap<>();
  private HashMap<String, Label> labels = new HashMap<>();
  private HashMap<String, Label> distancias = new HashMap<>();
  private HashMap<String, Label> rotulos = new HashMap<>();

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
    // Carrega a label de modelo para os resultados
    modelo = lblResultados.getText();

    // Carrega as ArrayLists que armazenarao os roteadores e os pesos das arestas, respectivamente
    roteadores = new ArrayList<>();
    pesosArestas = new ArrayList<>();

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

      // Impede que o botao seja clicado
      btnAlterarRede.setDisable(true);
 
      // Inicia a simulacao se a origem nao for nula
      if (origem != null) iniciarSimulacao(); 
    }
    else if (existeOrigem() && existeDestino()) {
      // Interrompe o metodo se uma origem e um destino ja tiverem
      // sido definidos
      return;
    } // Fim do bloco if/else if/else if
  } 

  /*
   * ***************************************************************
   * Metodo: inciarSimulacao
   * Funcao: inicia a simulacao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void iniciarSimulacao() {
    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Gera a imagem e a adiciona dentro da subrede
      Image mail = new Image(getClass().getResource("/img/Envelope.png").toExternalForm());
      ImageView envelope = new ImageView(mail);
      envelope.setFitWidth(41);
      envelope.setFitHeight(98);
      envelope.setLayoutX(origem.getPosX());
      envelope.setLayoutY(origem.getPosY());
      envelope.setPreserveRatio(true);
      subrede.getChildren().add(envelope);

      // Gera uma nova Thread para o pacote e a inicializa
      Pacote p = new Pacote(envelope, origem, destino);
      p.setDaemon(true); // Garante que a Thread seja interrompida com o fechamento do programa
      p.start();

      // Passa o pacote como parametro para obter o caminho mais curto
      // da origem ate o destino
      calcularCaminhoMaisCurto(p);
    }); // Fim do bloco Platform.runLater
  }

  /*
   * ***************************************************************
   * Metodo: calcularCaminhoMaisCurto
   * Funcao: calcula o caminho mais curto a ser percorrido pelo pacote
             da origem ate o destino
   * Parametros: Pacote p - pacote cujo caminho sera montado
   * Retorno: void
   ****************************************************************/

  private void calcularCaminhoMaisCurto(Pacote p) {
    // Inicio do bloco Thread
    Thread calculo = new Thread(() -> {
      // Define a distancia da origem
      origem.setDistancia(0);
      Platform.runLater(() -> alterarDistancia(origem));

      // Carrega a ArrayList contendo os roteadores a serem visitados
      // ao longo do algoritmo
      ArrayList<Roteador> abertos = new ArrayList<>();

      // O roteador de origem eh adicionado na lista de roteadores propensos
      // a visita
      abertos.add(origem);

      // Inicio do bloco while
      // Enquanto ainda houver roteadores a serem visitados
      while (!abertos.isEmpty()) {
        // Obtem o primeiro roteador contido na lista
        Roteador atual = abertos.get(0);

        // Inicio do bloco for
        for (Roteador r : abertos) {
          // Inicio do bloco if
          if (r.getDistancia() < atual.getDistancia()) {
            // Altera o no ativo caso for obtido um roteador cuja distancia
            // seja menor que a do no atual  
            final Roteador rAtivo = atual;
            Platform.runLater(() -> rAtivo.resetarNo());
            atual = r;
          } // Fim do bloco if
        } // Fim do bloco for

        // Remove o no ativo da lista de roteadores abertos
        // e define ele como permanente
        abertos.remove(atual);
        atual.setPermanente();

        // Guarda o no ativo em uma constante
        final Roteador rAtivo = atual;

        // Inicio do bloco Platform.runLater
        Platform.runLater(() -> {
          // Marca o no como ativo e altera o seu rotulo na interface
          rAtivo.marcarNoAtivo();
          alterarNoAtivo(rAtivo);
          alterarRotulo(rAtivo, "PERM.");

          // Atualiza o roteador na lista de roteadores e nos seus vizinhos
          atualizarRoteador(rAtivo);
          alterarRoteadorNosVizinhos(rAtivo);
        }); // Fim do bloco Platform.runLater

        // Inicio do bloco if
        if (atual.getAntecessor() != null) {
          // Obtem a aresta e a marca como intermediaria (candidata para o caminho final)
          // caso o no ativo possuir algum antecessor definido anteriormente
          final Aresta a = obterAresta(atual, atual.getAntecessor());

          // Inicio do bloco Platform.runLater
          Platform.runLater(() -> {
            a.setIntermediario(true);
            a.marcarIntermediario();
          }); // Fim do bloco Platform.runLater
        } // Fim do bloco if

        // O processo eh posto para dormir por 500 ms
        dormir(500);
 
        // Inicio do bloco for
        // Visitamos todos os vizinhos do no ativo
        for (Roteador v : atual.getVizinhos()) {
          // Obtem a aresta e a marca para sinalizar que ela esta sendo visitada
          Aresta a = obterAresta(atual, v);
          Platform.runLater(() -> a.marcarVisitando());

          // Poe o processo para dormir por 400 ms
          dormir(400);

          // Calcula a nova distancia entre o no atual e o vizinho visitado
          int novaDistancia = atual.getDistancia() + a.getPeso();

          // Inicio do bloco if
          if (!v.isPermanente() && (novaDistancia < v.getDistancia())) {
            // Altera a distancia e o antecessor do vizinho caso ele nao for
            // permanente e a nova distancia calculada for menor que a distancia anterior
            v.setDistancia(novaDistancia);
            v.setAntecessor(atual);

            // Adiciona o vizinho na lista de roteadores abertos para visita
            // caso ele nao se encontrar nela
            if (!abertos.contains(v)) abertos.add(v);

            // Inicio do bloco Platform.runLater
            Platform.runLater(() -> {
              // Altera a distancia do vizinho na interface e o atualiza 
              // na lista de roteadores e nos seus vizinhos
              alterarDistancia(v);
              atualizarRoteador(v);
              alterarRoteadorNosVizinhos(v);
            }); // Fim do bloco Platform.runLater
          } // Fim do bloco if

          // Reseta a linha da aresta para a sua cor original e poe o processo para dormir por 400 ms
          Platform.runLater(() -> a.resetarLinha());
          dormir(400);
        } // Fim do bloco for

        // Marca o no ativo como permanente e poe o processo para dormir por 300 ms
        if (rAtivo.isPermanente()) Platform.runLater(() -> rAtivo.marcarNoPermanente());
        dormir(300);
      } // Fim do bloco while

      dormir(1000);
      montarCaminhoFinal(p);
    }); // Fim do bloco Thread

    // Garante que a Thread seja interrompida caso o programa for fechado
    calculo.setDaemon(true);

    // Inicia a Thread
    calculo.start();
  }

  /*
   * ***************************************************************
   * Metodo: montarCaminhoFinal
   * Funcao: monta o caminho final a ser percorrido pelo pacote
   * Parametros: Pacote p - pacote cujo caminho sera montado
   * Retorno: void
   ****************************************************************/

  private void montarCaminhoFinal(Pacote p) {
    // Esvazia o texto do no ativo
    Platform.runLater(() -> lblNoAtivo.setText(""));

    // Marca as arestas que fazem parte do caminho final
    marcarArestasPermanentes();

    // Inicio do bloco for
    for (Roteador r : roteadores) {
      // Reseta o contorno de cada no na interface
      r.resetarNo();
    } // Fim do bloco for

    // Inicio do bloco for
    for (Aresta a : arestasExistentes.values()) {
      // Inicio do bloco if
      // Se a aresta nao for permanente (nao fizer parte do caminho final)
      if (!a.isPermanente()) {
        // Desmarca a aresta como intermediaria caso ela tiver sido marcada anteriormente
        if (a.isIntermediario()) a.setIntermediario(false);

        // Reseta a cor da linha da aresta
        a.resetarLinha();
      } // Fim do bloco if
    } // Fim do bloco for

    // Poe o processo para dormir por 500 ms
    dormir(500);

    // Comecamos pelo destino
    Roteador passo = destino;

    // Inicio do bloco while
    // Enquanto houver algum passo no caminho
    while (passo != null) {
      // Adiciona o roteador no caminho do pacote e o concatena na interface
      p.adicionarRoteadorAoCaminho(passo);
      final Roteador rPasso = passo;
      Platform.runLater(() -> concatenarCaminho(rPasso));

      // Inicio do bloco if
      if (passo.getAntecessor() != null) {
        // Obtem a aresta do roteador com o seu antecessor e marca como permanente
        Aresta a = obterAresta(passo, passo.getAntecessor());
        Platform.runLater(() -> a.marcarPermanente());
      } // Fim do bloco if

      // Obtem o antecessor do roteador atual e poe o processo para dormir por 800 ms
      passo = passo.getAntecessor();
      dormir(800);
    } // Fim do bloco while

    // Libera o pacote depois que o caminho for montado
    p.liberar();
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
   * Metodo: alterarNoAtivo
   * Funcao: exibe o nome do no ativo atual na label
   * Parametros: Roteador r - roteador marcado como no ativo
   * Retorno: void
   ****************************************************************/

  private void alterarNoAtivo(Roteador r) {
    lblNoAtivo.setText(r.getNome());
  }

  /*
   * ***************************************************************
   * Metodo: alterarDistancia
   * Funcao: altera a distancia do no visitado
   * Parametros: Roteador r - roteador cuja distancia sera alterada
   * Retorno: void
   ****************************************************************/

  private void alterarDistancia(Roteador r) {
    // Inicio do bloco for
    for (Map.Entry<String, Label> entrada : distancias.entrySet()) {
      // Inicio do bloco if
      if (entrada.getKey().equals(r.getNome())) {
        // Altera a label de distancia correspondente ao nome do roteador
        Label d = entrada.getValue();
        String modelo = ("(" + r.getNome() + ", " + r.getDistancia() + ")");
        d.setText(modelo);

        // Interrompe o laco
        break;
      } // Fim do bloco if
    } // Fim do bloco for
  }

  /*
   * ***************************************************************
   * Metodo: alterarRotulo
   * Funcao: altera o rotulo de estado do roteador (provisorio ou permanente)
   * Parametros: Roteador r - roteador cujo estado sera alterado
                 String rotulo - rotulo de estado a ser definido
   * Retorno: void
   ****************************************************************/

  private void alterarRotulo(Roteador r, String rotulo) {
    // Inicio do bloco for
    for (Map.Entry<String, Label> entrada : rotulos.entrySet()) {
      // Inicio do bloco if
      if (entrada.getKey().equals(r.getNome())) {
        // Atualiza a label do rotulo cuja chave seja correspondente ao nome do roteador
        Label rotuloNo = entrada.getValue();
        rotuloNo.setText(rotulo);

        // Marca a cor do rotulo como branco (caso o roteador for provisorio) ou como verde
        // (caso o roteador for permanente)
        rotuloNo.setTextFill((rotulo.equals("PERM.")) ? Color.web("#1fdb18") : Color.WHITE);

        // Interrompe o laco
        break;
      } // Fim do bloco if
    } // Fim do bloco for
  }

  /*
   * ***************************************************************
   * Metodo: marcarArestasPermanentes
   * Funcao: marca todas as arestas que fazem parte do caminho final
             como permanentes
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void marcarArestasPermanentes() {
    // Comecamos pelo destino
    Roteador passo = destino;

    // Inicio do bloco for
    while (passo != null) {
      // Inicio do bloco if
      if (passo.getAntecessor() != null) {
        // Obtem-se a aresta do roteador atual com o seu antecessor
        // e a marca como permanente caso o roteador atual tiver um antecessor
        Aresta a = obterAresta(passo, passo.getAntecessor());
        a.setIntermediario(false);
        a.setPermanente(true);
      } // Fim do bloco if

      // Obtem-se o antecessor do roteador atual
      passo = passo.getAntecessor();
    } // Fim do bloco for
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

    // Gera um novo trecho (a seta e adicionada se o roteador nao corresponder a origem)
    String novoTrecho = (r.isOrigem()) ? r.getNome() : " -> " + r.getNome();

    // Exibe o novo trecho no inicio junto com o texto anterior
    lblCaminho.setText(novoTrecho + textoAtual);
  }

  /*
   * ***************************************************************
   * Metodo: interromper
   * Funcao: interrompe a simulacao
   * Parametros: Pacote p - pacote a ser removido da tela
   * Retorno: void
   ****************************************************************/

  public void interromper(Pacote p) {
    // Armazena o pacote em uma constante
    final Pacote pacote = p;

    // Anula a variavel do pacote
    p = null;

    // Interrompe a Thread do pacote, armazenada em uma constante
    pacote.interrupt();

    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Remove a imagem do pacote da tela
      ImageView envelope = pacote.getEnvelope();
      subrede.getChildren().remove(envelope);

      // Inicio do bloco for
      for (Roteador r : roteadores) {
        // Reseta os parametros de cada roteador
        r.setProvisorio();
        r.setDistancia(Integer.MAX_VALUE);
        r.setAntecessor(null);
        r.setOrigem(false);
        r.setDestino(false);
        atualizarRoteador(r);
      } // Fim do bloco for

      // Carrega a Label de resultados trocando as letras X e Y pelos rotulos dos roteadores de origem e destino
      String resultados = modelo.replace("X", origem.getNome()).replace("Y", destino.getNome());
      lblResultados.setText(resultados);
 
      // Reseta os roteadores de origem e destino
      origem = null;
      destino = null;

      // Exibe o painel de interrupcao em cima do grafo
      painelInterrupcao.toFront();
      painelInterrupcao.setVisible(true);
    }); // Fim do bloco Platform.runLater
  }

  /*
   * ***************************************************************
   * Metodo: continuar
   * Funcao: reinicia a selecao
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void continuar(ActionEvent event) {
    // Oculta o painel de interrupcao
    painelInterrupcao.setVisible(false);

    // Exibe a Label de selecao
    lblSelecao.setVisible(true);

    // Reinicia as Labels de origem, destino e caminho
    lblOrigem.setText("");
    lblDestino.setText("");
    lblCaminho.setText("");

    // Ativa o menu de alteracao da rede
    btnAlterarRede.setDisable(false);

    // Inicio do bloco for
    // Percorremos cada no existente dentro do grafo
    for (Map.Entry<String, Circle> entrada : nosCriados.entrySet()) {
      // Obtemos o no e o seu rotulo
      Circle c = entrada.getValue();
      String nome = entrada.getKey();

      // Reinicia os circulos
      c.setStroke(Color.BLACK);
      c.setMouseTransparent(false);

      // Obtem o roteador, atualiza o no 
      // e atualiza a instancia do roteador
      Roteador r = obterRoteador(nome);
      r.setNo(c);
      atualizarRoteador(r);

      // O roteador volta a ser rotulado como provisorio
      alterarRotulo(r, "PROV.");
    } // Fim do bloco for

    // Inicio do bloco for
    for (Map.Entry<String, Label> entrada : distancias.entrySet()) {
      // Reseta as distancias de cada no
      Label d = entrada.getValue();
      String modelo = "(" + entrada.getKey() + ", ?)";
      d.setText(modelo);
    } // Fim do bloco for  

    // Inicio do bloco for
    for (Aresta a : arestasExistentes.values()) {
      // Obtem a aresta e a reseta
      if (a.isPermanente()) a.setPermanente(false);
      a.resetarLinha();
    } // Fim do bloco for
  }

  /*
   * ***************************************************************
   * Metodo: hoverAlterarRede
   * Funcao: exibe um popup quando o mouse esta em cima do btnAlterarRede
   * Parametros: MouseEvent event - evento gerado ao colocar o mouse de cima do botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void hoverAlterarRede(MouseEvent event) {
    // Exibe o popup se o botao nao estiver bloqueado
    popupAlterarRede.setVisible(true);
  }

  /*
   * ***************************************************************
   * Metodo: exitAlterarRede
   * Funcao: oculta o popup quando o mouse sai do alcance do btnAlterarRede
   * Parametros: MouseEvent event - evento gerado ao tirar o mouse de cima do botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void exitAlterarRede(MouseEvent event) {
    popupAlterarRede.setVisible(false);
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
   * Metodo: fechar
   * Funcao: oculta o painel de alteracao da sub rede
   * Parametros: ActionEvent event - evento gerado ao clicar no botao
   * Retorno: void
   ****************************************************************/

  @FXML
  private void fechar(ActionEvent event) {
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
      for (Label p : pesosArestas) {
        // Remove os pesos das arestas
        subrede.getChildren().remove(p);
      } // Fim do bloco for

      // Inicio do bloco for
      for (Label d : distancias.values()) {
        // Remove as distancias de cada no
        subrede.getChildren().remove(d);
      } // Fim do bloco for

      // Limpa a lista de nos
      listaNos.getChildren().clear();

      // Inicio do bloco for
      for (Roteador r : roteadores) {
        // Esvazia os roteadores
        r = null;
      } // Fim do bloco for

      // Esvazia as listas e os HashMaps
      roteadores.clear();
      nosCriados.clear();
      posicaoCirculos.clear();
      arestasExistentes.clear();
      labels.clear();
      distancias.clear();
      rotulos.clear();
      pesosArestas.clear();

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

    // Cria a label correspondente a distancia do no, obtida durante
    // o calculo do caminho mais curto
    Label distancia = new Label("(" + nome + ", ?)");
    distancia.setFont(Font.font("VCR OSD Mono", 13));
    distancia.setTextFill(Color.BLACK);

    // Seta a posicao da distancia com base no centro do circulo
    distancia.setLayoutX(circulo.getCenterX() + 18);
    distancia.setLayoutY(circulo.getCenterY() + 25);

    // Adiciona o circulo e o nome como chave no HashMap
    nosCriados.put(nome, circulo);

    // Adiciona a label de distancia dentro do HashMap
    distancias.put(nome, distancia);

    // Adiciona o circulo/no e a sua respectiva distancia na sub rede
    subrede.getChildren().addAll(circulo, distancia);

    // Cria uma caixa horizontal para guardar as informacoes do no
    HBox infoNo = new HBox();
    infoNo.setSpacing(20);
    infoNo.setAlignment(Pos.CENTER);

    // Cria a label correspondente ao nome do no
    Label nomeNo = new Label(nome);
    nomeNo.setFont(Font.font("VCR OSD Mono", 16));
    nomeNo.setTextFill(Color.WHITE);

    // Cria a label correspondente ao rotulo do no
    Label rotuloNo = new Label("PROV.");
    rotuloNo.setFont(Font.font("VCR OSD Mono", 16));
    rotuloNo.setTextFill(Color.WHITE);

    // Adiciona o rotulo no HashMap
    rotulos.put(nome, rotuloNo);

    // Adiciona o nome e o rotulo do no dentro da HBox
    infoNo.getChildren().addAll(nomeNo, rotuloNo);

    // Adiciona a HBox dentro da lista de nos
    listaNos.getChildren().add(infoNo); 

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
      label.setTextFill(Color.web("#009fe3"));
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
    if (!arestasExistentes.containsKey(idConexao)) {
      // A linha e desenhada entre os nos de cada roteador
      Line linha = new Line(r1.getNo().getCenterX(), r1.getNo().getCenterY(), r2.getNo().getCenterX(), r2.getNo().getCenterY());
      linha.setStroke(Color.WHITE);
      linha.setStrokeWidth(1.0);

      // Adiciona a linha na tela da sub rede
      subrede.getChildren().add(linha);

      // Os roteadores sao marcados como vizinhos um do outro
      r1.adicionarVizinho(r2);
      r2.adicionarVizinho(r1);

      // Atualiza os roteadores
      atualizarRoteador(r1);
      atualizarRoteador(r2);

      // Converte o peso em valor inteiro
      int pesoInt = Integer.parseInt(peso);

      // Cria uma nova instancia de aresta
      Aresta aresta = new Aresta(linha, r1, r2, pesoInt);

      // Coloca a aresta dentro do HashMap
      arestasExistentes.put(idConexao, aresta);

      // Gera a label de peso
      Label lblPeso = new Label(peso);
      lblPeso.setFont(Font.font("VCR OSD Mono", 11));
      lblPeso.setTextFill(Color.BLACK);

      // Calcula a posicao media do peso a partir do centro dos nos
      double xMedio = (r1.getNo().getCenterX() + r2.getNo().getCenterX()) / 2;
      double yMedio = (r1.getNo().getCenterY() + r2.getNo().getCenterY()) / 2;

      // Define a posicao do peso
      lblPeso.setLayoutX(xMedio);
      lblPeso.setLayoutY(yMedio);

      // Adiciona uma translacao para garantir que fique alinhado
      lblPeso.setTranslateX(-7);
      lblPeso.setTranslateY(-7);

      // Adiciona a label dentro da lista de pesos e da sub rede
      pesosArestas.add(lblPeso);
      subrede.getChildren().add(lblPeso);
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
}
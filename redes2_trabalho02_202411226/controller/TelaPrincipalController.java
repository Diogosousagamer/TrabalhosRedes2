/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 29/03/2026
* Ultima alteracao.: 31/03/2026
* Nome.............: TelaPrincipalController
* Funcao...........: Classe que controla os eventos da TelaPrincipal.
                     
*************************************************************** */

package controller;

import java.io.BufferedReader;
import java.io.FileReader;
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
import model.Aresta;
import model.Roteador;
import model.Pacote;

public class TelaPrincipalController implements Initializable {
	// Componentes da interface
  @FXML private AnchorPane painelInterrupcao;
	@FXML private AnchorPane subrede;
  @FXML private Button btnContinuar;
	@FXML private Button btnVoltar;
  @FXML private Label lblCaminho;
  @FXML private Label lblOrigem;
  @FXML private Label lblDestino;
  @FXML private Label lblResultados;
	@FXML private Label lblSelecao;

	// Variaveis e instancias
	public static volatile TelaPrincipalController controller;
	private int quantidadeNos;
	private Roteador origem;
  private Roteador destino;
  private String modelo;
  private ArrayList<Roteador> roteadores;
  private HashMap<String, Circle> nosCriados = new HashMap<>();
  private Map<String, double[]> posicaoCirculos = new HashMap<>();
  private HashMap<String, Aresta> arestasExistentes = new HashMap<>();
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
    // Carrega a label de modelo para os resultados
    modelo = lblResultados.getText();

    // Carrega a ArrayList que armazenara os roteadores
    roteadores = new ArrayList<>();

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
      alterarRoteadorNosVizinhos(origem);
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
      alterarRoteadorNosVizinhos(destino);
      lblDestino.setText(destino.getNome());

      // Oculta a label de selecao
      lblSelecao.setVisible(false);
 
      // Inicio do bloco if
      // Se o roteador de origem nao for nulo
      if (origem != null) {
        iniciarSimulacao();
      } // Fim do bloco if
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
      envelope.setLayoutX(origem.getPosX());
      envelope.setLayoutY(origem.getPosY());
      envelope.setPreserveRatio(true);
      subrede.getChildren().add(envelope);

      Pacote p = new Pacote(envelope, origem, destino);
      p.setDaemon(true);
      p.start();

      calcularCaminhoMaisCurto(p);
    });
  }

  private void calcularCaminhoMaisCurto(Pacote p) {
    Thread calculo = new Thread(() -> {
      // Define a distancia da origem
      origem.setDistancia(0);

      // Carrega a ArrayList contendo roteadores abertos
      ArrayList<Roteador> abertos = new ArrayList<>();
      abertos.add(origem);

      while (!abertos.isEmpty()) {
        Roteador atual = abertos.get(0);

        for (Roteador r : abertos) {
          if (r.getDistancia() < atual.getDistancia()) atual = r;
        }

        abertos.remove(atual);
        atual.setPermanente();
        final Roteador r = atual;

        Platform.runLater(() -> {
          atualizarRoteador(r);
          alterarRoteadorNosVizinhos(r);
        });

        if (atual.equals(destino)) {
          Roteador passo = destino;

          while (passo != null) {
            p.adicionarRoteadorAoCaminho(passo);
            final Roteador rPasso = passo;
            Platform.runLater(() -> concatenarCaminho(rPasso));

            if (passo.getAntecessor() != null) {
              Aresta a = obterAresta(passo, passo.getAntecessor());
              Platform.runLater(() -> a.marcarLinha());
            }

            passo = passo.getAntecessor();

            try {
              Thread.sleep(500);
            }
            catch (InterruptedException e) {
              Thread.currentThread().interrupt();
            }
          }

          break;
        }

        for (Roteador v : atual.getVizinhos()) {
          Aresta a = obterAresta(atual, v);
          int novaDistancia = atual.getDistancia() + a.getPeso();

          if (novaDistancia < v.getDistancia()) {
            v.setDistancia(novaDistancia);
            v.setAntecessor(atual);

            if (!abertos.contains(v)) abertos.add(v);

            Platform.runLater(() -> {
              atualizarRoteador(v);
              alterarRoteadorNosVizinhos(v);
            });
          }
        }
      }

      p.liberar();
    });

    calculo.setDaemon(true);
    calculo.start();
  }

  private Aresta obterAresta(Roteador r1, Roteador r2) {
    String id = (r1.getNome().compareTo(r2.getNome()) < 0) ? r1.getNome() + r2.getNome() : r2.getNome() + r1.getNome();
    return arestasExistentes.get(id);
  }

  private void alterarRoteadorNosVizinhos(Roteador r) {
    for (int i = 0; i < roteadores.size(); i++) {
      Roteador rot = roteadores.get(i);
      rot.alterarVizinho(r);
      atualizarRoteador(rot);
    }
  }

  private void concatenarCaminho(Roteador r) {
    String textoAtual = lblCaminho.getText();
    String novoTrecho = (r.isOrigem()) ? r.getNome() : " -> " + r.getNome();
    lblCaminho.setText(novoTrecho + textoAtual);
  }

  public void interromper(Pacote p) {
    final Pacote pacote = p;
    p = null;

    pacote.interrupt();

    Platform.runLater(() -> {
      ImageView envelope = pacote.getEnvelope();
      subrede.getChildren().remove(envelope);

      for (Roteador r : roteadores) {
        r.setProvisorio();
        r.setDistancia(Integer.MAX_VALUE);
        r.setOrigem(false);
        r.setDestino(false);
      }

      for (Map.Entry<String, Aresta> aresta : arestasExistentes.entrySet()) {
        Aresta a = aresta.getValue();
        a.resetarLinha();
      }

      String resultados = modelo.replace("X", origem.getNome()).replace("Y", destino.getNome());
      origem = null;
      destino = null;
      lblResultados.setText(resultados);

      painelInterrupcao.toFront();
      painelInterrupcao.setVisible(true);
    });
  }

  @FXML
  private void continuar(ActionEvent event) {
    painelInterrupcao.setVisible(false);
    lblSelecao.setVisible(true);

    lblOrigem.setText("");
    lblDestino.setText("");
    lblCaminho.setText("");

    for (Map.Entry<String, Circle> entrada : nosCriados.entrySet()) {
      Circle c = entrada.getValue();
      String nome = entrada.getKey();

      c.setStroke(Color.BLACK);
      c.setCursor(Cursor.HAND);

      Roteador r = obterRoteador(nome);
      r.setNo(c);
      atualizarRoteador(r);
    }
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
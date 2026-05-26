/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 02/05/2026
* Ultima alteracao.: 25/05/2026
* Nome.............: Roteador
* Funcao...........: Thread que gerencia as operacoes de cada roteador.
                     
*************************************************************** */

package model;

import controller.TelaPrincipalController;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Roteador extends Thread {
	// Variaveis e instancias
	private Circle no;
	private double posX;
  private double posY;
  private CopyOnWriteArrayList<Aresta> extremidades;
	private CopyOnWriteArrayList<Roteador> vizinhos;
  private CopyOnWriteArrayList<Roteador> listaRoteadores;
  private CopyOnWriteArrayList<Hello> hellosEnviados;
  private CopyOnWriteArrayList<Echo> echosEnviados;
  private TabelaRoteamento tabela;
  private BufferEnlace bufferEnlace;
  private boolean encontrouVizinhos;
  private boolean mediuRetardos;
  private boolean tabelaCompleta;
	private String nome;
	private boolean origem;
	private boolean destino;
  private int contadorSeq;
  private final long INFINITO = 30;
  private final long CUSTO_INFINITO = 100000;

  /*
   * ***************************************************************
   * Metodo: Roteador
   * Funcao: inicializa uma nova instancia da classe Roteador
   * Parametros: Circle no - no que representa o roteador na interface
                 String nome - rotulo do roteador para facilitar identificacao
   * Retorno: nenhum
   ****************************************************************/

	public Roteador(Circle no, String nome) {
		this.no = no;
		this.nome = nome;
		vizinhos = new CopyOnWriteArrayList<>();
    listaRoteadores = new CopyOnWriteArrayList<>();
    extremidades = new CopyOnWriteArrayList<>();
    hellosEnviados = new CopyOnWriteArrayList<>();
    echosEnviados = new CopyOnWriteArrayList<>();
    encontrouVizinhos = false;
    tabelaCompleta = false;
		origem = false;
		destino = false;
    contadorSeq = 0;
	}

  /*
   * ***************************************************************
   * Metodo: run
   * Funcao: metodo que executa as operacoes da Thread enquanto ela
             estiver ativa
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted() && TelaPrincipalController.controller.simulacaoAtiva) {
      // Interrompe a Thread caso ela for interrompida
      if (Thread.currentThread().isInterrupted()) break;

      // O roteador conhece os seus vizinhos
      conhecerVizinhos();

      // Interrompe a Thread caso ela for interrompida
      if (Thread.currentThread().isInterrupted()) break;

      // Mensura os retardos das extremidades
      medirRetardos();

      // Interrompe a Thread caso ela for interrompida
      if (Thread.currentThread().isInterrupted()) break;

      processarEstadosEnlace();

      if (Thread.currentThread().isInterrupted()) break;

      // Interrompe o laco
      break;
    }
  }

  /*
   * ***************************************************************
   * Metodo: conhecerVizinhos
   * Funcao: o roteador passa a conhecer os seus vizinhos
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void conhecerVizinhos() {
    // Inicio do bloco try/catch
    try {
      // Inicio do bloco for
      for (Aresta a : extremidades) {
        // Interrompe o metodo se a Thread for interrompida
        if (Thread.currentThread().isInterrupted()) return;

        // Obtem-se os roteadores da aresta atual
        Roteador r1 = a.getR1();
        Roteador r2 = a.getR2();

        // Obtem-se o destino do pacote Hello, o encaminha na sub rede e o adiciona
        // na lista interna de pacotes Hello
        Roteador destinoHello = (!r1.getNome().equals(this.getNome())) ? r1 : r2;
        Hello h = TelaPrincipalController.controller.enviarHello(this, destinoHello);
        hellosEnviados.add(h);

        // O roteador dorme por 200 ms
        dormir(200);
      } // Fim do bloco for
 
      // Permanece dormindo enquanto todos os Hellos nao encerrarem suas operacoes  
      while (!checarHellos()) dormir(100);

      // Sinaliza que os seus vizinhos foram encontrados
      encontrouVizinhos = true;

      // Interrompe o metodo se a Thread for interrompida
      if (Thread.currentThread().isInterrupted()) return;

      // Lista os vizinhos obtidos
      debugVizinhos();

      // Interrompe o metodo se a Thread for interrompida
      if (Thread.currentThread().isInterrupted()) return;
 
      // Inicio do bloco for
      for (Aresta a : extremidades) {
        // Interrompe o metodo se a Thread for interrompida
        if (Thread.currentThread().isInterrupted()) return;

        // Pula a aresta caso ela ja tiver sido ativado
        if (!a.estaDesativada()) continue; 

        // Ativa a aresta caso ela estiver desativada
        Platform.runLater(() -> a.ativarAresta());
        dormir(500);
      } // Fim do bloco for

      // Interrompe o metodo se a Thread for interrompida
      if (Thread.currentThread().isInterrupted()) return;

      // O roteador permanece dormindo aguardando os demais roteadores a encontrarem todos os seus vizinhos
      while (!TelaPrincipalController.controller.verificarEncontrouVizinhos()) dormir(100);
    }
    catch (InterruptedException e) {
      // Em caso de excecao, a Thread eh interrompida
      Thread.currentThread().interrupt();
    } // Fim do bloco try/catch
  }

  /*
   * ***************************************************************
   * Metodo: medirRetardos
   * Funcao: o roteador medir os retardos dos caminhos para cada vizinho
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void medirRetardos() {
    // Inicio do bloco try/catch
    try {
      // Inicio do bloco for
      for (Roteador v : vizinhos) {
        // Interrompe o metodo se a Thread for interrompida
        if (Thread.currentThread().isInterrupted()) return;

        // Encaminha um novo pacote Echo para o vizinho e o adiciona na lista
        Echo e = TelaPrincipalController.controller.enviarEcho(this, v);
        echosEnviados.add(e); 

        // Dorme por 200 ms 
        dormir(200);
      } // Fim do bloco for

      // Interrompe o metodo se a Thread for interrompida
      if (Thread.currentThread().isInterrupted()) return;

      // Aguarda os pacotes Echo enviados encerrarem as operacoes
      while (!checarEchos()) dormir(100);

      // Interrompe o metodo se a Thread for interrompida
      if (Thread.currentThread().isInterrupted()) return;

      // Sinaliza que mediu todos os seus retardos dos caminhos
      // que o levam para seus vizinhos
      mediuRetardos = true;

      // Interrompe o metodo se a Thread for interrompida
      if (Thread.currentThread().isInterrupted()) return;
 
      // Aguarda os roteadores medirem todos os retardos dos caminhos
      // que o levam para seus vizinhos
      while (!TelaPrincipalController.controller.verificarMediuRetardos()) dormir(100);
    }
    catch (InterruptedException e) {
      // Em caso de excecao, a Thread eh interrompida
      Thread.currentThread().interrupt();
    } // Fim do bloco try/catch
  }

  /*
   * ***************************************************************
   * Metodo: processarEstadosEnlace
   * Funcao: o roteador procura enviar e processar pacotes referentes
             ao estado de enlace da sub rede
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void processarEstadosEnlace() {
    // Inicio do bloco try/catch
    try {
      // Cria o buffer com as entradas iniciais
      bufferEnlace = new BufferEnlace(this, this.listaRoteadores);
      bufferEnlace.criarEntradasIniciais();

      // Cria as entradas iniciais da tabela e dorme por um segundo
      Platform.runLater(() -> tabela.definirEntradasIniciais(listaRoteadores));
      dormir(1000);

      // Realiza o envio dos pacotes de estado de enlace
      enviarPacotesEnlace();
    }
    catch (InterruptedException e) {
      // Em caso de excecao, a Thread eh interrompida
      Thread.currentThread().interrupt();
    } // Fim do bloco try/catch
  }
 
  private void calcularRotas() {
    try {
      long[][] matrizAdjacencia = gerarMatrizAdjacencia(listaRoteadores.size());
      int indiceRot = gerarIndice(this.nome);

      for (Roteador v : vizinhos) {
        int indiceVizinho = gerarIndice(v.getNome());
        matrizAdjacencia[indiceRot][indiceVizinho] = tabela.ps1(v);
      }

      executarDijkstra(matrizAdjacencia, indiceRot, listaRoteadores);
      while (!TelaPrincipalController.controller.verificarTabelasCompletas()) dormir(100);
      TelaPrincipalController.controller.simulacaoAtiva = false;
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /*
   * ***************************************************************
   * Metodo: debugVizinhos
   * Funcao: o roteador medir os retardos dos caminhos para cada vizinho
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void debugVizinhos() {
    // Inicio do bloco if/else
    // Se a lista de vizinhos nao estiver vazia
    if (!vizinhos.isEmpty()) {
      // Cria-se uma String para concatenar os vizinhos
      String listaVizinhos = "";

      // Inicio do bloco for
      for (Roteador v : vizinhos) {
        // Adiciona o nome de cada vizinho na lista
        listaVizinhos += v.getNome() + " ";
      } // Fim do bloco for

      // Exibe a lista de vizinhos completa no terminal
      System.out.println("Vizinhos do roteador " + this.getNome() + ": " + listaVizinhos);
    }
    else {
      // Caso contrario, exibe essa informacao
      System.out.println("Nenhum vizinho encontrado para o roteador " + this.getNome());
    } // Fim do bloco if/else
  }

  /*
   * ***************************************************************
   * Metodo: enviarPacotesEnlace
   * Funcao: o roteador envia pacotes de estado de enlace para todos os seus vizinhos
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  public void enviarPacotesEnlace() {
    try {
      for (Roteador v : vizinhos) {
        PacoteEstadoEnlace link = TelaPrincipalController.controller.enviarPacoteEnlace(this, v, this, contadorSeq, 60);
        for (Roteador rot : vizinhos) link.adicionarCustoVizinho(rot, tabela.ps1(rot));
        link.definirPosicao(this);
        link.start();
        dormir(300);
      }

      contadorSeq++;
      while (!TelaPrincipalController.controller.verificarPacotesEnlace()) dormir(100);
      debugBuffer();
      calcularRotas();
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public void debugBuffer() {
    String buffer = "Buffer do Roteador " + this.nome + "\n" + "Roteador:" + "\t" + "Seq. do pacote:" 
                    + "\t" + "Flags de confirmacao:" + "\t" + "Flags de transmissao:" + "\n";
    CopyOnWriteArrayList<EntradaBuffer> entradasBuffer = bufferEnlace.getEntradas();

    for (EntradaBuffer e : entradasBuffer) {
      HashMap<Roteador, Boolean> flagsConfirmacao = e.getFlagsConfirmacao();
      HashMap<Roteador, Boolean> flagsTransmissao = e.getFlagsTransmissao();

      String trans = "";
      String con = "";

      for (Map.Entry<Roteador, Boolean> flag : flagsConfirmacao.entrySet()) {
        Roteador rot = flag.getKey();
        boolean valor = flag.getValue();
        con += rot.getNome() + " " + "(" + ((valor) ? "1" : "0") + ")" + " ";
      } 

      for (Map.Entry<Roteador, Boolean> flag : flagsTransmissao.entrySet()) {
        Roteador rot = flag.getKey();
        boolean valor = flag.getValue();
        trans += rot.getNome() + " " + "(" + ((valor) ? "1" : "0") + ")" + " ";
      } 

      int sequenciaExibida = (e.getPacoteAtual() != null) ? e.getPacoteAtual().getNumeroSequencia() : 0;
      buffer += e.getRoteadorEntrada().getNome() + "\t" + sequenciaExibida + "\t" + con + "\t" + trans + "\n";
    }

    System.out.println(buffer + "\n");
  }

  private long[][] gerarMatrizAdjacencia(int total) {
    long[][] matriz = new long[total][total];

    for (int i = 0; i < total; i++) {
      for (int j = 0; j < total; j++) {
        if (i == j) matriz[i][j] = 0;
        else matriz[i][j] = CUSTO_INFINITO;
      }
    }

    for (EntradaBuffer entrada : bufferEnlace.getEntradas()) {
      Roteador origem = entrada.getRoteadorEntrada();
      PacoteEstadoEnlace pacote = entrada.getPacoteAtual();

      if (pacote != null) {
        HashMap<Roteador, Long> custos = pacote.getCustoVizinhos();
        int i = gerarIndice(origem.getNome());

        for (Map.Entry<Roteador, Long> link : custos.entrySet()) {
          Roteador vizinho = link.getKey();
          int j = gerarIndice(vizinho.getNome());
          matriz[i][j] = link.getValue();
        }
      }
    }

    return matriz;
  }

  private void executarDijkstra(long[][] matriz, int raiz, CopyOnWriteArrayList<Roteador> listaRoteadores) {
    int n = matriz.length;
    long[] distancia = new long[n];
    int[] predecessor = new int[n];
    boolean[] permanente = new boolean[n];

    for (int i = 0; i < n; i++) {
      distancia[i] = CUSTO_INFINITO;
      predecessor[i] = -1;
      permanente[i] = false;
    }

    distancia[raiz] = 0;

    for (int i = 0; i < n - 1; i++) {
      int u = encontrarMenorDistancia(distancia, permanente);
      if (u == -1) break;

      permanente[u] = true;

      for (int v = 0; v < n; v++) {
        if (!permanente[v] && matriz[u][v] != CUSTO_INFINITO) {
          long novaDistancia = distancia[u] + matriz[u][v];

          if (novaDistancia < distancia[v]) {
            distancia[v] = novaDistancia;
            predecessor[v] = u;
          }
        }
      }
    }

    tabela.preencherTabela(distancia, predecessor, raiz, listaRoteadores);
    tabelaCompleta = true;
  }

  private int encontrarMenorDistancia(long[] distancia, boolean[] permanente) {
    long min = 100000;
    int minIndice = -1;

    for (int i = 0; i < distancia.length; i++) {
      if (!permanente[i] && distancia[i] <= min) {
        min = distancia[i];
        minIndice = i;
      }
    }

    return minIndice;
  }

  private int gerarIndice(String nome) {
    if (nome == null || nome.isEmpty()) return -1;
    return nome.charAt(0) - 'A';
  }

  /*
   * ***************************************************************
   * Metodo: definirPosicao
   * Funcao: define a posicao do roteador nos eixos X e Y
   * Parametros: double x - posicao no eixo X
                 double y - posicao no eixo Y
   * Retorno: void
   ****************************************************************/

	public void definirPosicao(double x, double y) {
		this.posX = x;
		this.posY = y;
	}

	/*
   * ***************************************************************
   * Metodo: adicionarVizinho
   * Funcao: adiciona um novo vizinho dentro da lista de vizinhos
   * Parametros: Roteador v - roteador vizinho a ser adicionado
   * Retorno: void
   ****************************************************************/

  public void adicionarVizinho(Roteador v) {
    // Adiciona o roteador na lista de vizinhos apenas se ele nao estiver presente
    // para evitar problemas de redundancia
    if (!vizinhos.contains(v)) vizinhos.add(v);
  }

  /*
   * ***************************************************************
   * Metodo: removerVizinho
   * Funcao: remove um vizinho da lista de vizinhos
   * Parametros: Roteador v - roteador vizinho a ser removido
   * Retorno: void
   ****************************************************************/

  public void removerVizinho(Roteador v) {
    // Inicio do bloco for
    for (int i = 0; i < vizinhos.size(); i++) {
      // Obtem o vizinho no instante atual
      Roteador atual = vizinhos.get(i); 
      
      // Inicio do bloco if
      if (atual.getNome().equals(v.getNome())) {
        // Remove o vizinho caso ele for encontrado na lista de vizinhos
        // e interrompe o laco
        vizinhos.remove(v);
        break;
      }
    } // Fim do bloco for

    // Altera a entrada para marcar o retardo como infinito
    tabela.alterarEntrada(new EntradaTabela(v, v.getNome(), "-", Long.toString(INFINITO)));
  }

  /*
   * ***************************************************************
   * Metodo: alterarVizinho
   * Funcao: substitui o vizinho por uma nova instancia deste caso ele for
             alterado externamente
   * Parametros: Roteador v - roteador vizinho a ser alterado/substituido
   * Retorno: void
   ****************************************************************/

  public void alterarVizinho(Roteador v) {
    // Inicio do bloco for
    // Percorremos toda a lista de vizinhos ate achar a instancia correspondente
    // ao vizinho passado como parametro
    for (int i = 0; i < vizinhos.size(); i++) {
      // Guardamos o vizinho localizado na posicao atual em uma variavel
      Roteador r = vizinhos.get(i);

      // Inicio do bloco if
      if (r.getNome().equals(v.getNome())) {
        // Troca o vizinho da posicao atual pelo vizinho passado como parametro
        // se eles possuirem o mesmo rotulo, efetuando assim a modificacao
        vizinhos.set(i, v);

        // Interrompe o laco
        break;
      } // Fim do bloco if
    } // Fim do bloco for
  }

  /*
   * ***************************************************************
   * Metodo: adicionarExtremidade
   * Funcao: adiciona uma aresta de extremidade
   * Parametros: Aresta a - extremidade a ser adicionada
   * Retorno: void
   ****************************************************************/

  public void adicionarExtremidade(Aresta a) {
    // Adiciona a aresta na lista de extremidades apenas se ela nao estiver presente
    // para evitar problemas de redundancia
    if (!extremidades.contains(a)) extremidades.add(a);
  }

  /*
   * ***************************************************************
   * Metodo: checarHellos
   * Funcao: verifica se todos os pacotes Hello enviados 
             ja completaram suas operacoes
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  private boolean checarHellos() {
    // Inicio do bloco for
    for (Hello h : hellosEnviados) {
      // Inicio do bloco if
      if (!h.chegou()) {
        // Retorna falso se algum pacote Hello nao tiver encerrado
        return false;
      } // Fim do bloco if
    } // Fim do bloco for

    // Retorna verdadeiro se todos os pacotes Hello tiverem encerrado suas operacoes
    return true;
  }

  /*
   * ***************************************************************
   * Metodo: checarEchos
   * Funcao: verifica se todos os pacotes Echo enviados 
             ja completaram suas operacoes
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  private boolean checarEchos() {
    // Inicio do bloco for
    for (Echo e : echosEnviados) {
      // Inicio do bloco if
      if (!e.encerrou()) {
        // Retorna falso se algum pacote Echo nao tiver encerrado
        return false;
      } // Fim do bloco if
    } // Fim do bloco for

    // Retorna verdadeiro se todos os pacotes Echo tiverem encerrado suas operacoes
    return true;
  }

  /*
   * ***************************************************************
   * Metodo: resetarNo
   * Funcao: marca o contorno do no com a cor padrao (preto)
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  public void resetarNo() {
    no.setStroke(Color.BLACK);
  }

  /*
   * ***************************************************************
   * Metodo: resetarEntradas
   * Funcao: redefine as entradas da tabela apos a finalizacao do algoritmo
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  public void resetarEntradas() {
    tabela.redefinirEntradas();
  }

  /*
   * ***************************************************************
   * Metodo: completo
   * Funcao: sinaliza que o roteador concluiu seu processamento
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void completo() {
    System.out.println("Roteador " + this.getNome() + " completo");
  }

  /*
   * ***************************************************************
   * Metodo: dormir
   * Funcao: coloca a Thread para dormir por alguns milissegundos
   * Parametros: long valor - tempo de sono da Thread em milissegundos
   * Retorno: void
   ****************************************************************/

  private void dormir(long valor) throws InterruptedException {
    // O roteador eh posto para dormir por alguns ms
    Thread.sleep(valor);
  }

  /*
   * ***************************************************************
   * Metodo: setNo
   * Funcao: define o no do roteador
   * Parametros: Circle no - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setNo(Circle no) {
    this.no = no;
  }

  /*
   * ***************************************************************
   * Metodo: getNo
   * Funcao: retorna o no do roteador
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Circle
   ****************************************************************/

  public Circle getNo() {
    return no;
  }

  /*
   * ***************************************************************
   * Metodo: setNome
   * Funcao: define o rotulo do roteador
   * Parametros: String nome - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setNome(String nome) {
    this.nome = nome;
  }

  /*
   * ***************************************************************
   * Metodo: getNome
   * Funcao: retorna o rotulo do roteador
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

  public String getNome() {
    return nome;
  }

  /*
   * ***************************************************************
   * Metodo: setVizinhos
   * Funcao: define o conjunto de vizinhos do roteador
   * Parametros: CopyOnWriteArrayList<Roteador> vizinhos - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setVizinhos(CopyOnWriteArrayList<Roteador> vizinhos) {
    this.vizinhos = vizinhos;
  }

  /*
   * ***************************************************************
   * Metodo: getVizinhos
   * Funcao: retorna o conjunto de vizinhos do roteador
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: CopyOnWriteArrayList<Roteador>
   ****************************************************************/

  public CopyOnWriteArrayList<Roteador> getVizinhos() {
    return vizinhos;
  }

  /*
   * ***************************************************************
   * Metodo: setPosX
   * Funcao: define a posicao no eixo X
   * Parametros: double posX - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setPosX(double posX) {
    this.posX = posX;
  }

  /*
   * ***************************************************************
   * Metodo: getPosX
   * Funcao: retorna a posicao no eixo X
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: double
   ****************************************************************/

  public double getPosX() {
    return posX;
  }

  /*
   * ***************************************************************
   * Metodo: setPosY
   * Funcao: define a posicao no eixo Y
   * Parametros: double posY - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setPosY(double posY) {
    this.posY = posY;
  }

  /*
   * ***************************************************************
   * Metodo: getPosY
   * Funcao: define a posicao do host
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: double
   ****************************************************************/

  public double getPosY() {
    return posY;
  }

  /*
   * ***************************************************************
   * Metodo: setOrigem
   * Funcao: define se o roteador eh origem ou nao
   * Parametros: boolean origem - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setOrigem(boolean origem) {
    this.origem = origem;
  }

  /*
   * ***************************************************************
   * Metodo: isOrigem
   * Funcao: retorna se o roteador eh origem ou nao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  public boolean isOrigem() {
    return origem;
  }

  /*
   * ***************************************************************
   * Metodo: setDestino
   * Funcao: define se o roteador eh destino ou nao
   * Parametros: Circle no - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setDestino(boolean destino) {
    this.destino = destino;
  }

  /*
   * ***************************************************************
   * Metodo: isDestino
   * Funcao: retorna se o roteador eh destino ou nao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  public boolean isDestino() {
    return destino;
  }

  /*
   * ***************************************************************
   * Metodo: setTabela
   * Funcao: define a tabela de roteamento do roteador
   * Parametros: TabelaRoteamento tabela - tabela a ser definida
   * Retorno: void
   ****************************************************************/

  public void setTabela(TabelaRoteamento tabela) {
    this.tabela = tabela;
  }

  /*
   * ***************************************************************
   * Metodo: getTabela
   * Funcao: retorna a tabela de roteamento do roteador
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: TabelaRoteamento
   ****************************************************************/

  public TabelaRoteamento getTabela() {
    return tabela;
  }

  /*
   * ***************************************************************
   * Metodo: setBufferEnlace
   * Funcao: define o buffer de estado de enlace do roteador
   * Parametros: BufferEnlace buffer - buffer a ser definido
   * Retorno: void
   ****************************************************************/

  public void setBufferEnlace(BufferEnlace buffer) {
    this.bufferEnlace = buffer;
  }

  /*
   * ***************************************************************
   * Metodo: getBufferEnlace
   * Funcao: retorna o buffer de estado de enlace do roteador
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: BufferEnlace
   ****************************************************************/

  public BufferEnlace getBufferEnlace() {
    return bufferEnlace;
  }

  /*
   * ***************************************************************
   * Metodo: setListaRoteadores
   * Funcao: define a lista de roteadores da sub rede
   * Parametros: CopyOnWriteArrayList<Roteadores> tabela - tabela a ser definida
   * Retorno: void
   ****************************************************************/

  public void setListaRoteadores(CopyOnWriteArrayList<Roteador> lr) {
    this.listaRoteadores = lr;
  }

  /*
   * ***************************************************************
   * Metodo: getListaRoteadores
   * Funcao: retorna a lista de roteadores da sub rede
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: CopyOnWriteArrayList<Roteador>
   ****************************************************************/

  public CopyOnWriteArrayList<Roteador> getListaRoteadores() {
    return listaRoteadores;
  }

  /*
   * ***************************************************************
   * Metodo: setEncontrouVizinhos
   * Funcao: define se os vizinhos foram descobertos ou nao
   * Parametros: boolean v - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setEncontrouVizinhos(boolean v) {
    this.encontrouVizinhos = v;
  }

  /*
   * ***************************************************************
   * Metodo: encontrouVizinhos
   * Funcao: retorna se os vizinhos foram descobertos ou nao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  public boolean encontrouVizinhos() {
    return encontrouVizinhos;
  }

  /*
   * ***************************************************************
   * Metodo: setMediuRetardos
   * Funcao: define se os retardos foram mensurados ou nao
   * Parametros: boolean m - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setMediuRetardos(boolean m) {
    this.mediuRetardos = m;
  }

  /*
   * ***************************************************************
   * Metodo: mediuRetardos
   * Funcao: retorna se os retardos foram mensurados ou nao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  public boolean mediuRetardos() {
    return mediuRetardos;
  }

  /*
   * ***************************************************************
   * Metodo: setTabelaCompleta
   * Funcao: define se a tabela de roteamento esta completa ou nao
   * Parametros: boolean tabelaCompleta - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setTabelaCompleta(boolean tabelaCompleta) {
    this.tabelaCompleta = tabelaCompleta;
  }

  /*
   * ***************************************************************
   * Metodo: getTabelaCompleta
   * Funcao: retorna se a tabela de roteamento esta completa ou nao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  public boolean isTabelaCompleta() {
    return tabelaCompleta;
  }
}
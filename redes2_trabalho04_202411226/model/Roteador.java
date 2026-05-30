/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 02/05/2026
* Ultima alteracao.: 30/05/2026
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
  private HashMap<Roteador, Long> custoVizinhos;
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
    custoVizinhos = new HashMap<>();
    encontrouVizinhos = false;
    mediuRetardos = false;
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
    // Inicio do bloco while
    // Enquanto a Thread e a simulacao nao forem interrompidas
    while (!Thread.currentThread().isInterrupted() && TelaPrincipalController.controller.simulacaoAtiva) {
      // Interrompe a Thread caso ela for interrompida
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;

      // O roteador conhece os seus vizinhos
      conhecerVizinhos();

      // Interrompe a Thread caso ela for interrompida
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;

      // Mensura os retardos das extremidades
      medirRetardos();

      // Interrompe a Thread caso ela for interrompida
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;

      // Distribui e processa os pacotes de estado de enlace
      processarEstadosEnlace();

      // Interrompe a Thread caso ela for interrompida
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;

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
      // Inicio do bloco if
      if (extremidades.isEmpty()) {
        // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

        // Sinaliza que os seus vizinhos foram encontrados
        encontrouVizinhos = true;

        // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

        // Lista os vizinhos obtidos
        debugVizinhos();

        // O roteador permanece dormindo aguardando os demais roteadores a encontrarem todos os seus vizinhos
        while (!TelaPrincipalController.controller.verificarEncontrouVizinhos()) {
          if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;
          dormir(100);
        }

        // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

        // Sai do metodo
        return;
      } // Fim do bloco if

      // Inicio do bloco for
      for (Aresta a : extremidades) {
        // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

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
      while (!checarHellos()) {
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;
        dormir(100);
      }

      // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Sinaliza que os seus vizinhos foram encontrados
      encontrouVizinhos = true;

      // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Lista os vizinhos obtidos
      debugVizinhos();

      // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;
 
      // Inicio do bloco for
      for (Aresta a : extremidades) {
        // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;

        // Pula a aresta caso ela ja tiver sido ativado
        if (!a.estaDesativada()) continue; 

        // Ativa a aresta caso ela estiver desativada
        Platform.runLater(() -> a.ativarAresta());
        dormir(500);
      } // Fim do bloco for

      // Interrompe o metodo se a Thread for interrompida
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // O roteador permanece dormindo aguardando os demais roteadores a encontrarem todos os seus vizinhos
      while (!TelaPrincipalController.controller.verificarEncontrouVizinhos()) {
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;
        dormir(100);
      }

      // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;
    }
    catch (InterruptedException e) {
      // Em caso de excecao, a Thread eh interrompida
      Thread.currentThread().interrupt();
    } // Fim do bloco try/catch
  }

  /*
   * ***************************************************************
   * Metodo: medirRetardos
   * Funcao: o roteador envia pacotes Echo para medir 
             os retardos dos caminhos para cada vizinho
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void medirRetardos() {
    // Inicio do bloco try/catch
    try {
      // Inicio do bloco if
      // Se o roteador nao possuir nenhum vizinho
      if (vizinhos.isEmpty()) {
        // Ativa a flag para impedir que a simulacao fique em loop
        mediuRetardos = true;

        // Aguarda os roteadores medirem todos os retardos dos caminhos
        // que o levam para seus vizinhos
        while (!TelaPrincipalController.controller.verificarMediuRetardos()) {
          if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;
          dormir(100);
        }

        // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

        // Exibe os custos dos vizinhos do roteador
        debugCustos();

        // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

        // Sai do metodo
        return; 
      } // Fim do bloco if

      // Inicio do bloco for
      for (Roteador v : vizinhos) {
        // Interrompe o laco se a Thread ou a simulacao forem interrompidas
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

        // Encaminha um novo pacote Echo para o vizinho e o adiciona na lista
        Echo e = TelaPrincipalController.controller.enviarEcho(this, v);
        echosEnviados.add(e); 

        // Dorme por 200 ms 
        dormir(200);
      } // Fim do bloco for

      // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Aguarda os pacotes Echo enviados encerrarem as operacoes
      while (!checarEchos()) {
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;
        dormir(100);
      }

      // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Sinaliza que mediu todos os seus retardos dos caminhos
      // que o levam para seus vizinhos
      mediuRetardos = true;

      // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Gera as Labels dos retardos dos caminhos do roteador para os seus vizinhos
      TelaPrincipalController.controller.gerarRetardos(this);

      // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;
 
      // Aguarda os roteadores medirem todos os retardos dos caminhos
      // que o levam para seus vizinhos
      while (!TelaPrincipalController.controller.verificarMediuRetardos()) {
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;
        dormir(100);
      }

      // Exibe os custos dos vizinhos do roteador
      debugCustos();
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
      // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Cria o buffer com as entradas iniciais
      bufferEnlace = new BufferEnlace(this, this.listaRoteadores);
      bufferEnlace.criarEntradasIniciais();

      // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Cria as entradas iniciais da tabela e dorme por um segundo
      Platform.runLater(() -> tabela.definirEntradasIniciais(listaRoteadores));
      dormir(1000);

      // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Realiza o envio dos pacotes de estado de enlace
      enviarPacotesEnlace();
    }
    catch (InterruptedException e) {
      // Em caso de excecao, a Thread eh interrompida
      Thread.currentThread().interrupt();
    } // Fim do bloco try/catch
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
      // Interrompe o metodo se a Thread for interrompida
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Inicio do bloco if
      // Se o roteador nao possuir nenhum vizinho
      if (vizinhos.isEmpty()) {
        // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

        // Aguarda todos os pacotes de estado de enlace da sub rede serem processados ou descartados
        while (!TelaPrincipalController.controller.verificarPacotesEnlace()) {
          if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;
          dormir(100);
        }

        // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

        // Calcula as rotas finais para cada roteador
        calcularRotas();

        // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

        // Sai do metodo
        return;
      } // Fim do bloco if
 
      // Inicio do bloco for
      for (Roteador v : vizinhos) {
        // Interrompe o laco se a Thread for interrompida
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;

        // Encaminha um pacote de estado de enlace para cada vizinho
        PacoteEstadoEnlace link = TelaPrincipalController.controller.enviarPacoteEnlace(this, v, this, contadorSeq);
        link.setCustoVizinhos(this.custoVizinhos);
        link.definirPosicao(this);
        link.start();
        dormir(300);
      } // Fim do bloco for

      // Interrompe o metodo se a Thread for interrompida
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Incrementa o contador de numeros de sequencia
      contadorSeq++;

      // Interrompe o metodo se a Thread for interrompida
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Aguarda todos os pacotes de estado de enlace da sub rede serem processados ou descartados
      while (!TelaPrincipalController.controller.verificarPacotesEnlace()) {
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;
        dormir(100);
      }

      // Interrompe o metodo se a Thread for interrompida
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Exibe os resultados finais do buffer
      debugBuffer();

      // Interrompe o metodo se a Thread for interrompida
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Calcula as rotas finais para cada roteador
      calcularRotas();

      // Interrompe o metodo se a Thread for interrompida
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;
    }
    catch (InterruptedException e) {
      // Em caso de excecao, a Thread eh interrompida
      Thread.currentThread().interrupt();
    } // Fim do bloco try/catch
  }
 
  /*
   * ***************************************************************
   * Metodo: calcularRotas
   * Funcao: calcula as rotas para cada destino possivel
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void calcularRotas() {
    // Inicio do bloco try/catch
    try {
      // Inicio do bloco if
      // Se o roteador nao tiver nenhum vizinho
      if (vizinhos.isEmpty()) {
        // Marca a tabela como completa
        tabelaCompleta = true;

        // Dorme por um tempo enquanto as demais tabelas nao estiverem concluidas
        while (!TelaPrincipalController.controller.verificarTabelasCompletas()) {
          if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;
          dormir(100);
        }

        // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;
 
        // Desativa a simulacao
        TelaPrincipalController.controller.simulacaoAtiva = false;

        // Sai do metodo
        return;
      } // Fim do bloco if

      // Gera a matriz de adjacencia com base nos aprendizados adquiridos no estado de enlace
      long[][] matrizAdjacencia = gerarMatrizAdjacencia(listaRoteadores.size());

      // Obtem o indice do roteador
      int indiceRot = gerarIndice(this.nome);

      // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Calcula o caminho mais curto para cada destino com o algoritmo de Dijkstra
      executarDijkstra(matrizAdjacencia, indiceRot, listaRoteadores);

      // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

      // Dorme por um tempo enquanto as demais tabelas nao estiverem concluidas
      while (!TelaPrincipalController.controller.verificarTabelasCompletas()) {
        if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) break;
        dormir(100);
      }

      // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
      if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;
 
      // Encerra a simulacao
      TelaPrincipalController.controller.simulacaoAtiva = false;
    }
    catch (InterruptedException e) {
      // Em caso de excecao, a Thread eh interrompida
      Thread.currentThread().interrupt();
    } // Fim do bloco try/catch
  }

  /*
   * ***************************************************************
   * Metodo: debugVizinhos
   * Funcao: exibe os vizinhos do roteador
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
   * Metodo: debugCustos
   * Funcao: exibe os retardos estimados dos caminhos para cada vizinho
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void debugCustos() {
    // Inicio do bloco if/else
    // Se houver custo para os vizinhos
    if (!custoVizinhos.isEmpty()) {
      // Reserva uma String para formatar os custos dos vizinhos do roteador
      String custos = "Custo dos vizinhos de " + this.nome + ": \n";

      // Formata cada custo dentro da String
      for (Map.Entry<Roteador, Long> custo : custoVizinhos.entrySet()) {
        custos += custo.getKey().getNome() + ": " + custo.getValue() + "\n";
      }

      // Exibe a String
      System.out.println(custos);
    }
    else {
      // Exibe uma mensagem informando que nenhum custo foi estimado caso contrario 
      System.out.println("Nenhum custo estimado para os vizinhos de " + this.nome);
    } // Fim do bloco if/else
  }
 
  /*
   * ***************************************************************
   * Metodo: debugBuffer
   * Funcao: exibe o buffer completo do roteador
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  public void debugBuffer() {
    // String reservada para formatar o buffer
    String buffer = "Buffer do Roteador " + this.nome + ": " + "\n";

    // Obtem as entradas do buffer do roteador
    CopyOnWriteArrayList<EntradaBuffer> entradasBuffer = bufferEnlace.getEntradas();

    // Inicio do bloco for
    for (EntradaBuffer e : entradasBuffer) {
      // Obtem as flags da entrada atual
      HashMap<Roteador, Boolean> flagsConfirmacao = e.getFlagsConfirmacao();
      HashMap<Roteador, Boolean> flagsTransmissao = e.getFlagsTransmissao();

      // Strings reservadas para a formatacao das flags de transmissao e confirmacao, respectivamente
      String trans = "";
      String con = "";

      // Formata as flags de confirmacao dessa entrada
      for (Map.Entry<Roteador, Boolean> flag : flagsConfirmacao.entrySet()) {
        Roteador rot = flag.getKey();
        boolean valor = flag.getValue();
        con += rot.getNome() + " " + "(" + ((valor) ? "1" : "0") + ")" + " ";
      } 

      // Formata as flags de transmissao dessa entrada
      for (Map.Entry<Roteador, Boolean> flag : flagsTransmissao.entrySet()) {
        Roteador rot = flag.getKey();
        boolean valor = flag.getValue();
        trans += rot.getNome() + " " + "(" + ((valor) ? "1" : "0") + ")" + " ";
      } 

      // Obtem o numero de sequencia do pacote mantido pela entrada
      int sequenciaExibida = (e.getPacoteAtual() != null) ? e.getPacoteAtual().getNumeroSequencia() : -1;

      // Formata a linha da entrada dentro do texto do buffer
      buffer += "Roteador: " + e.getRoteadorEntrada().getNome() + "\t" + "Num. de Sequencia: " + sequenciaExibida + "\t" 
             + "Flags de confirmacao: " + con + "\t" + "Flags de transmissao: " + trans + "\n";
    } // Fim do bloco for

    // Imprime o buffer completo do roteador
    System.out.println(buffer + "\n");
  }

  /*
   * ***************************************************************
   * Metodo: gerarMatrizAdjacencia
   * Funcao: gera a matriz de adjacencia do roteador com base nas informacoes
             coletadas durante a distribuicao de pacotes de estado de enlace
   * Parametros: int total - total de roteadores presentes na sub rede
   * Retorno: long[][]
   ****************************************************************/

  private long[][] gerarMatrizAdjacencia(int total) {
    // Gera uma matriz com tamanho totalxtotal (ex: com 8 roteadores, teremos 8 linhas e 8 colunas)
    long[][] matriz = new long[total][total];

    // Inicio do bloco for
    for (int i = 0; i < total; i++) {
      // Inicio do bloco for
      for (int j = 0; j < total; j++) {
        // Atribui custo 0 a entradas cujos indices de linha e coluna
        // sejam iguais
        if (i == j) matriz[i][j] = 0;
        // Caso contrario atribui um custo infinito (suficientemente grande
        // para o calculo de Dijkstra)
        else matriz[i][j] = CUSTO_INFINITO;
      } // Fim do bloco for
    } // Fim do bloco for

    // Inicio do bloco for
    for (EntradaBuffer entrada : bufferEnlace.getEntradas()) {
      // Obtem o roteador da entrada atual no buffer
      Roteador origem = entrada.getRoteadorEntrada();

      // Obtem o pacote guardado na entrada atual
      PacoteEstadoEnlace pacote = entrada.getPacoteAtual();

      // Inicio do bloco if
      // Se o pacote nao for nulo
      if (pacote != null) {
        // Obtem os custos guardados pelo pacote
        HashMap<Roteador, Long> custos = pacote.getCustoVizinhos();

        // Obtem o indice do roteador atual
        int i = gerarIndice(origem.getNome());

        // Inicio do bloco for
        for (Map.Entry<Roteador, Long> link : custos.entrySet()) {
          // Obtem o roteador vizinho da entrada de custo
          Roteador vizinho = link.getKey();

          // Obtem o indice correspondente ao roteador vizinho na matriz
          // de adjacencia
          int j = gerarIndice(vizinho.getNome());

          // Guarda o custo dentro da entrada ixj da matriz
          matriz[i][j] = link.getValue();
        } // Fim do bloco for
      } // Fim do bloco if
    } // Fim do bloco for

    // Obtem o indice correspondente ao roteador na matriz
    int raiz = gerarIndice(this.nome);

    // Guarda os custos dos vizinhos diretos do roteador
    for (Map.Entry<Roteador, Long> link : this.custoVizinhos.entrySet()) {
      int vizinho = gerarIndice(link.getKey().getNome());
      matriz[raiz][vizinho] = link.getValue();
    }

    // Retorna a matriz de adjacencia completa
    return matriz;
  }

  /*
   * ***************************************************************
   * Metodo: executarDijkstra
   * Funcao: executa o algoritmo de Dijkstra (caminho mais curto) para estimar 
             as rotas finais
   * Parametros: long[][] matriz - matriz de adjacencia do roteador
                 int raiz - indice correspondente ao roteador dentro da matriz
                 CopyOnWriteArrayList<Roteador> - lista de roteadores presentes na sub rede
   * Retorno: void
   ****************************************************************/

  private void executarDijkstra(long[][] matriz, int raiz, CopyOnWriteArrayList<Roteador> listaRoteadores) {
    // Obtem o tamanho da matriz para criar os vetores contendo as distancias, os predecessores
    // os predecessores para cada roteador, e os roteadores ja visitados, respectivamente
    int n = matriz.length;
    long[] distancia = new long[n];
    int[] predecessor = new int[n];
    boolean[] permanente = new boolean[n];

    // Inicio do bloco for
    for (int i = 0; i < n; i++) {
      // A distancia inicializa como infinito
      distancia[i] = CUSTO_INFINITO;

      // Nenhum predecessor eh registrado inicialmente
      predecessor[i] = -1;

      // Todos os roteadores sao marcados como provisorios
      permanente[i] = false;
    } // Fim do bloco for

    // A raiz comeca com distancia 0
    distancia[raiz] = 0;

    // Inicio do bloco for
    for (int i = 0; i < n - 1; i++) {
      // Obtem o indice com a menor distancia, interrompendo o laco 
      // caso retornar um valor indeterminado
      int u = encontrarMenorDistancia(distancia, permanente);
      if (u == -1) break;

      // Marca o indice "u" como permanente
      permanente[u] = true;

      // Inicio do bloco for
      for (int v = 0; v < n; v++) {
        // Inicio do bloco if
        // Se o roteador atual nao estiver rotulado como permanente e a entrada do roteador de menor distancia
        // com o roteador atual nao tiver um valor infinito
        if (!permanente[v] && matriz[u][v] != CUSTO_INFINITO) {
          // Calcula a nova distancia somando a distancia de u com a entrada correspondente na matirz
          long novaDistancia = distancia[u] + matriz[u][v];

          // Inicio do bloco if
          // Se a nova distancia for menor que a distancia atual de v
          if (novaDistancia < distancia[v]) {
            // Troca a distancia e o predecessor
            distancia[v] = novaDistancia;
            predecessor[v] = u;
          } // Fim do bloco if
        } // Fim do bloco if
      } // Fim do bloco for
    } // Fim do bloco for

    // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
    if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

    // Apos o termino do algoritmo, preenche a tabela
    tabela.preencherTabela(distancia, predecessor, raiz, listaRoteadores);

    // Interrompe o metodo se a Thread ou a simulacao forem interrompidas
    if (Thread.currentThread().isInterrupted() || !TelaPrincipalController.controller.simulacaoAtiva) return;

    // Marca a tabela como completa
    tabelaCompleta = true;
  }

  /*
   * ***************************************************************
   * Metodo: encontrarMenorDistancia
   * Funcao: encontra o indice do roteador com a menor distancia possivel
   * Parametros: long[] distancia - vetor de distancias
                 boolean[] permanente - vetor contendo roteadores ja visitados
   * Retorno: int
   ****************************************************************/

  private int encontrarMenorDistancia(long[] distancia, boolean[] permanente) {
    // O menor custo eh infinito
    long min = 100000;

    // O menor indice nao foi registrado
    int minIndice = -1;

    // Inicio do bloco for
    for (int i = 0; i < distancia.length; i++) {
      // Inicio do bloco if
      // Se o roteador i nao estiver rotulado como permanente
      // e a distancia ate esse roteador i for menor ou igual ao menor valor possivel
      if (!permanente[i] && distancia[i] <= min) {
        // A distancia do roteador i passa a ser o menor custo
        min = distancia[i];

        // Atualiza o menor indice com o indice do roteador atual
        minIndice = i;
      } // Fim do bloco if
    } // Fim do bloco for

    // Retorna o menor indice obtido apos o fim do laco
    return minIndice;
  }

  /*
   * ***************************************************************
   * Metodo: gerarIndice
   * Funcao: gera o indice do roteador correspondente a matriz de adjacencia
   * Parametros: String nome - nome do roteador
   * Retorno: int
   ****************************************************************/

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

  public void removerExtremidade(Aresta a) {
    extremidades.remove(a);
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
   * Metodo: adicionarCustoVizinho
   * Funcao: registra o custo para um determinado vizinho do roteador
   * Parametros: Roteador r - vizinho cujo custo sera registrado
                 long custo - custo a ser registrado
   * Retorno: void
   ****************************************************************/

  public void adicionarCustoVizinho(Roteador r, long custo) {
    // Adiciona o roteador e seu respectivo custo no mapa se (e somente se) 
    // ainda nao houver um registro correspondente
    if (!custoVizinhos.containsKey(r)) custoVizinhos.put(r, custo);
  }

  /*
   * ***************************************************************
   * Metodo: removerCustoVizinho
   * Funcao: remove o registro do custo para um certo vizinho
   * Parametros: Roteador r - vizinho a ser removido da lista
   * Retorno: void
   ****************************************************************/

  public void removerCustoVizinho(Roteador r) {
    custoVizinhos.remove(r);
  }

  /*
   * ***************************************************************
   * Metodo: obterCustoVizinho
   * Funcao: retorna o custo para um determinado vizinho do roteador
   * Parametros: Roteador r - vizinho cujo custo sera retornado
   * Retorno: long
   ****************************************************************/

  public long obterCustoVizinho(Roteador r) {
    return custoVizinhos.get(r);
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
   * Funcao: retorna a posicao no eixo Y
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
   * Parametros: CopyOnWriteArrayList<Roteadores> lr - lista a ser definida
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
   * Metodo: setCustoVizinhos
   * Funcao: define a lista de custos para cada vizinho do roteador
   * Parametros: CopyOnWriteArrayList<Roteadores> custoVizinhos - lista a ser definida
   * Retorno: void
   ****************************************************************/

  public void setCustoVizinhos(HashMap<Roteador, Long> custoVizinhos) {
    this.custoVizinhos = custoVizinhos;
  }

  /*
   * ***************************************************************
   * Metodo: getCustoVizinhos
   * Funcao: retorna a lista de custos para cada vizinho do roteador
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: HashMap<Roteador, Long>
   ****************************************************************/

  public HashMap<Roteador, Long> getCustoVizinhos() {
    return custoVizinhos;
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
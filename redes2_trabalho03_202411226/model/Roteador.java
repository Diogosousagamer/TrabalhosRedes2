/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 16/04/2026
* Ultima alteracao.: 30/04/2026
* Nome.............: Roteador
* Funcao...........: Thread que gerencia as operacoes de cada roteador.
                     
*************************************************************** */

package model;

import controller.TelaPrincipalController;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Roteador extends Thread {
	// Variaveis e instancias
	private Circle no;
	private double posX;
  private double posY;
	private CopyOnWriteArrayList<Roteador> vizinhos;
  private CopyOnWriteArrayList<Roteador> listaRoteadores;
  private TabelaRoteamento tabela;
  private boolean tabelaCompleta;
	private String nome;
  private boolean echo;
	private boolean origem;
	private boolean destino;

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
    tabelaCompleta = false;
		origem = false;
		destino = false;
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
    Platform.runLater(() -> definirEntradasIniciais());
    dormir(1000);

    int maxIteracoes = listaRoteadores.size();

    for (int i = 0; i < maxIteracoes; i++) {
      for (Roteador v : vizinhos) {
        TelaPrincipalController.controller.enviarSolicitacao(this, v);
        while (this.echo) dormir(100);

        ArrayList<EntradaTabela> entradasVizinho = new ArrayList<>();

        synchronized(v.getTabela()) {
          entradasVizinho = new ArrayList<>(v.getTabela().getEntradas());
        }

        processarVetor(v, entradasVizinho);
        dormir(500);
      }
    }

    this.tabelaCompleta = true;
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
   * Metodo: definirEntradasIniciais
   * Funcao: inicializa as entradas iniciais da tabela no inicio da simulacao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void definirEntradasIniciais() {
    tabela.definirEntradasIniciais(listaRoteadores);
  }

  /*
   * ***************************************************************
   * Metodo: inserirEntrada
   * Funcao: insere uma nova entrada na tabela de roteamento
   * Parametros: EntradaTabela e - entrada a ser inserida
   * Retorno: void
   ****************************************************************/

  public void inserirEntrada(EntradaTabela e) {
    tabela.inserirEntrada(e);
  }

  /*
   * ***************************************************************
   * Metodo: modificarEntrada
   * Funcao: modifica uma certa entrada na tabela de roteamento
   * Parametros: String destino - linha de destino
                 String saida - linha de saida
                 long retardo - retardo do caminho a ser percorrido
                                ate o destino
   * Retorno: void
   ****************************************************************/

  public void modificarEntrada(Roteador rDestino, String destino, String saida, long retardo) {
    tabela.alterarEntrada(new EntradaTabela(rDestino, destino, saida, Long.toString(retardo)));
  }

  /*
   * ***************************************************************
   * Metodo: processarVetor
   * Funcao: coloca a tabela para processar a tabela do vizinho
   * Parametros: Roteador emissor - roteador que enviou a tabela
                 ArrayList<EntradaTabela> entradasEmissor - conjunto de entradas da tabela do emissor
   * Retorno: void
   ****************************************************************/

  private void processarVetor(Roteador emissor, ArrayList<EntradaTabela> entradasEmissor) {
    tabela.processarVetor(emissor, entradasEmissor);
  }

  /*
   * ***************************************************************
   * Metodo: ping
   * Funcao: retorna o retardo de um caminho entre dois roteadores
   * Parametros: Roteador destino - roteador de destino
   * Retorno: long
   ****************************************************************/

  public long ping(Roteador destino) {
    long distancia = 0;

    // Inicio do bloco try/catch
    try (BufferedReader br = new BufferedReader(new FileReader("backbone.txt"))) {
      String linha = "";

      // Inicio do bloco while
      while ((linha = br.readLine()) != null) {
        String[] partes = linha.split(",");

        if (partes.length < 4) continue;

        String nome1 = partes[0];
        String nome2 = partes[1];

        // Inicio do bloco if/else if
        if (nome1.equals(this.getNome()) && nome2.equals(destino.getNome())) {
          // Retorna o tempo de ida e interrompe o laco
          distancia = Long.parseLong(partes[2]);
          break;
        }
        else if (nome1.equals(destino.getNome()) && nome2.equals(this.getNome())) {
          // Retorna o tempo de volta e interrompe o laco
          distancia = Long.parseLong(partes[3]);
          break;
        } // Fim do bloco if/else if
      } // Fim do bloco while
    }
    catch (IOException e) {
      // Em caso de excecao, ela sera exibida no terminal
      e.printStackTrace();
    } // Fim do bloco try/catch

    // Retorna a distancia
    return distancia;
  }

  /*
   * ***************************************************************
   * Metodo: resetarEntradas
   * Funcao: redefine as entradas apos a finalizacao do algoritmo
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  public void resetarEntradas() {
    tabela.redefinirEntradas();
  }

  /*
   * ***************************************************************
   * Metodo: dormir
   * Funcao: coloca a Thread para dormir por alguns milissegundos
   * Parametros: long valor - tempo de sono da Thread em milissegundos
   * Retorno: void
   ****************************************************************/

  private void dormir(long valor) {
    // Inicio do bloco try/catch
    try {
      // O roteador eh posto para dormir por alguns ms
      Thread.sleep(valor);
    }
    catch (InterruptedException e) {
      // Em caso de excecao, a Thread e interrompida
      Thread.currentThread().interrupt();
    } // Fim do bloco try/catch
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
   * Metodo: setEcho
   * Funcao: define se um pacote de solicitacao foi enviado ou nao
   * Parametros: boolean echo - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setEcho(boolean echo) {
    this.echo = echo;
  }

  /*
   * ***************************************************************
   * Metodo: isEcho
   * Funcao: retorna se um pacote de solicitacao foi enviado ou nao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  public boolean isEcho() {
    return echo;
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
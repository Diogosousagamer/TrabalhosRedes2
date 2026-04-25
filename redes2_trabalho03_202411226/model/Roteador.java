/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 16/04/2026
* Ultima alteracao.: 24/04/2026
* Nome.............: Roteador
* Funcao...........: Classe que gerencia as operacoes de cada roteador.
                     
*************************************************************** */

package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Roteador {
	// Variaveis e instancias
	private Circle no;
	private double posX;
  private double posY;
	private ArrayList<Roteador> vizinhos;
  private TabelaRoteamento tabela;
	private String nome;
	private boolean origem;
	private boolean destino;
  private long distancia;
  private Roteador antecessor;

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
		vizinhos = new ArrayList<>();
		origem = false;
		destino = false;
    distancia = Integer.MAX_VALUE;
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
    vizinhos.add(v);
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
   * Metodo: marcarVisitando
   * Funcao: marca o no do roteador com um contorno azul escuro para sinalizar
             que ele esta sendo visitado
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  public void marcarVisitando() {
    no.setStroke(Color.web("#3d7996"));
  }

  /*
   * ***************************************************************
   * Metodo: resetarNo
   * Funcao: marca o no cor a cor do contorno anterior
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  public void resetarNo() {
    // Inicio do bloco if/else if/else
    if (this.isOrigem()) {
      // Reverte para a cor verde caso o no corresponder ao roteador
      // de origem
      no.setStroke(Color.web("#1fdb18"));
    }
    else if (this.isDestino()) {
      // Reverte para a cor vermelho caso o no corresponder ao roteador
      // de destino
      no.setStroke(Color.web("#d60b18"));
    }
    else {
      // Para os demais casos, a cor sera revertida para preto
      no.setStroke(Color.BLACK);
    } // Fim do bloco if/else if/else
  }

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

  public boolean processarVetor(Roteador emissor, ArrayList<EntradaTabela> entradasEmissor) {
    return tabela.processarVetor(this, emissor, entradasEmissor);
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

    try (BufferedReader br = new BufferedReader(new FileReader("backbone.txt"))) {
      String linha = "";

      while ((linha = br.readLine()) != null) {
        String[] partes = linha.split(",");

        if (partes.length < 4) continue;

        String nome1 = partes[0];
        String nome2 = partes[1];

        if (nome1.equals(this.getNome()) && nome2.equals(destino.getNome())) {
          distancia = Long.parseLong(partes[2]);
          break;
        }
        else if (nome1.equals(destino.getNome()) && nome2.equals(this.getNome())) {
          distancia = Long.parseLong(partes[3]);
          break;
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }

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
   * Parametros: ArrayList<Roteador> vizinhos - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setVizinhos(ArrayList<Roteador> vizinhos) {
    this.vizinhos = vizinhos;
  }

  /*
   * ***************************************************************
   * Metodo: getVizinhos
   * Funcao: retorna o conjunto de vizinhos do roteador
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: ArrayList<Roteador>
   ****************************************************************/

  public ArrayList<Roteador> getVizinhos() {
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
   * Metodo: setDistancia
   * Funcao: define a distancia do roteador dentro do caminho
   * Parametros: long distancia - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setDistancia(long distancia) {
    this.distancia = distancia;
  }

  /*
   * ***************************************************************
   * Metodo: getDistancia
   * Funcao: retorna a distancia atual do roteador dentro do caminho
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: long
   ****************************************************************/

  public long getDistancia() {
    return distancia;
  }

  /*
   * ***************************************************************
   * Metodo: setAntecessor
   * Funcao: define o antecessor desse roteador para que o caminho
             final seja montado
   * Parametros: Roteador a - valor a ser definido 
   * Retorno: void
   ****************************************************************/

  public void setAntecessor(Roteador a) {
    this.antecessor = a;
  }

  /*
   * ***************************************************************
   * Metodo: getAntecessor
   * Funcao: retorna o antecessor atual do roteador
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

  public Roteador getAntecessor() {
    return antecessor;
  }
}
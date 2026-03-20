/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 15/03/2026
* Ultima alteracao.: 20/03/2026
* Nome.............: Roteador
* Funcao...........: Classe que gerencia as operacoes de cada roteador.
                     
*************************************************************** */

package model;

import java.util.ArrayList;
import javafx.scene.shape.Circle;

public class Roteador {
  // Variaveis e instancias 
  private Circle no;
  private double posX;
  private double posY;
  private String nome;
  private ArrayList<Roteador> vizinhos;
  private boolean ocupado;
  private boolean intermediario;
  private Host hostProximo;
  private boolean destino;

  /*
   * ***************************************************************
   * Metodo: Roteador
   * Funcao: inicializa uma nova instancia da classe Roteador
   * Parametros: Circle no - no que representa o roteador na interface
   * Retorno: nenhum
   ****************************************************************/

  public Roteador(Circle no, String nome) {
    this.no = no;
    this.nome = nome;
    vizinhos = new ArrayList<>();
    ocupado = false;
    intermediario = false;
  }

  public void definirPosicao(double x, double y) {
    this.posX = x;
    this.posY = y;
  }

  public void adicionarVizinho(Roteador v) {
    vizinhos.add(v);
  }

  public void alterarVizinho(Roteador v) {
    for (int i = 0; i < vizinhos.size(); i++) {
      Roteador r = vizinhos.get(i);

      if (r.getNome().equals(v.getNome())) {
        vizinhos.set(i, v);
        break;
      }
    }
  }

  public void setNo(Circle no) {
    this.no = no;
  }

  public Circle getNo() {
    return no;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

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
   * Metodo: ocupar
   * Funcao: ocupa o roteador, evitando que outro pacote entre nele 
             (se o usuario selecionar a versao 2 em diante)
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  public void ocupar() {
    this.ocupado = true;
  }

  /*
   * ***************************************************************
   * Metodo: getOcupado
   * Funcao: retorna se o roteador ta ocupado ou nao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  public boolean getOcupado() {
    return ocupado;
  }

  public boolean getIntermediario() {
    return intermediario;
  }

  public void setHostProximo(Host h) {
    this.hostProximo = h;
    intermediario = true;
  }

  public Host getHostProximo() {
    return hostProximo;
  }
}
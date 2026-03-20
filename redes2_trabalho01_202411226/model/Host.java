/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 16/03/2026
* Ultima alteracao.: 17/03/2026
* Nome.............: Host
* Funcao...........: Classe que gerencia as operacoes de cada host.
                     
*************************************************************** */

package model;

public class Host {
	// Variaveis e instancias
	private double posX;
	private double posY;
	private boolean transmissor;
  private boolean recebeu;

  /*
   * ***************************************************************
   * Metodo: Host
   * Funcao: inicializa uma nova instancia da classe Host
   * Parametros: double posX - posicao no eixo X
   							 double posY - posicao no eixo Y
   * Retorno: nenhum
   ****************************************************************/

	public Host(double posX, double posY) {
		this.posX = posX;
		this.posY = posY;
    this.recebeu = false;
	}

	/*
   * ***************************************************************
   * Metodo: setTransmissor
   * Funcao: define se o host sera transmissor ou nao
   * Parametros: boolean t - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setTransmissor(boolean t) {
		this.transmissor = t;
	}

	/*
   * ***************************************************************
   * Metodo: getTransmissor
   * Funcao: retorna se o host eh transmissor ou nao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public boolean getTransmissor() {
		return transmissor;
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

  public void setRecebeu(boolean recebeu) {
    this.recebeu = recebeu;
  }

  public boolean getRecebeu() {
    return recebeu;
  }
}
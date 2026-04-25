/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 25/04/2026
* Ultima alteracao.: 25/04/2026
* Nome.............: Echo
* Funcao...........: Thread que gerencia as operacoes de cada pacote de solicitacao enviados
                     entre os roteadores.
                     
*************************************************************** */

package model;

import controller.TelaPrincipalController;
import java.lang.Thread;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class Echo extends Thread {
	// Variaveis e instancias
	private Roteador origem;
	private Roteador destino;
	private ImageView envelope;
	private double posX;
	private double posY;

	public Echo(Roteador origem, Roteador destino, ImageView envelope) {
		this.origem = origem;
		this.destino = destino;
		this.envelope = envelope;
	}

  @Override
	public void run() {
		definirPosicao();
		movimentar(this.destino);
		TelaPrincipalController.controller.removerSolicitacao(this);
	}

	private void definirPosicao() {
		// Armazena as posicoes iniciais da imagem nos eixos X e Y
		// nos contadores de posicao
		posX = origem.getPosX();
		posY = origem.getPosY();

		// Inicio do bloco Platform.runLater
		Platform.runLater(() -> {
			// Define a posicao da imagem
			envelope.setLayoutX(posX);
			envelope.setLayoutY(posY);
		}); // Fim do bloco Platform.runLater
	}

	private void movimentar(Roteador r) {
		// Obtem-se a posicao (X e Y) do roteador de destino
		double destinoX = r.getPosX();
		double destinoY = r.getPosY();

  	// Calcula-se a distancia (em termos de posicao) entre a origem e o destino
		double deltaX = destinoX - posX;
		double deltaY = destinoY - posY;
 
  	// Calcula a quantidade de passos a serem realizados ate o pacote alcancar o destino
		int passos = Math.max((int) Math.abs(deltaX), (int) Math.abs(deltaY));

  	// Interrompe o metodo caso nao ser necessario realizar nenhum passo
  	if (passos == 0) return;

    // Calcula o valor dos incrementos a serem feitos nos eixos X e Y para assim, atingir o destino final
		double passoX = deltaX / passos;
		double passoY = deltaY / passos;

    // Inicio do bloco for
    // O laco e realizado ate que sejam realizados todos os passos ou a Thread seja interrompida
		for (int i = 0; i < passos && !Thread.currentThread().isInterrupted(); i++) {
			// Os contadores de posicao sao incrementados
			posX += passoX;
			posY += passoY;

      // As posicoes sao armazenadas em constantes para serem usadas no 
      // Platform.runLater
			final int xInt = (int) Math.round(posX);
			final int yInt = (int) Math.round(posY);
 
      // Inicio do bloco Platform.runLater
			Platform.runLater(() -> {
				// Altera a posicao da imagem do pacote atraves dos valores finais 
				// das posicoes que foram obtidas naquele instante
				envelope.setLayoutX(xInt);
				envelope.setLayoutY(yInt);
			}); // Fim do bloco Platform.runLater

      // Inicio do bloco try/catch
			try {
				// A Thread e posta para dormir por 16 ms
				Thread.sleep(16);
			}
			catch (InterruptedException e) {
				// Em caso de excecao, a Thread e interrompida
				Thread.currentThread().interrupt();
			} // Fim do bloco try/catch
		} // Fim do bloco for

    // Armazena as posicoes finais dentro dos contadores de posicao
    // para garantir que o pacote atinja, com exatidao, o destino final
		posX = destinoX;
		posY = destinoY;

    // Inicio do bloco Platform.runLater
		Platform.runLater(() -> {
			// Altera a posicao da imagem do pacote atraves dos contadores de posicao
			envelope.setLayoutX(posX);
			envelope.setLayoutY(posY);
		}); // Fim do bloco Platform.runLater
	}

	/*
   * ***************************************************************
   * Metodo: setEnvelope
   * Funcao: define a imagem do pacote de solicitacao
   * Parametros: ImageView envelope - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setEnvelope(ImageView envelope) {
		this.envelope = envelope;
	}

  /*
   * ***************************************************************
   * Metodo: getEnvelope
   * Funcao: retorna a imagem do pacote de solicitacao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: ImageView
   ****************************************************************/

	public ImageView getEnvelope() {
		return envelope;
	}

  /*
   * ***************************************************************
   * Metodo: setOrigem
   * Funcao: define o roteador de origem do percurso do pacote de solicitacao
   * Parametros: Roteador origem - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setOrigem(Roteador origem) {
		this.origem = origem;
	}

  /*
   * ***************************************************************
   * Metodo: getOrigem
   * Funcao: retorna o roteador de origem do percurso do pacote de solicitacao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getOrigem() {
		return origem;
	}

  /*
   * ***************************************************************
   * Metodo: setDestino
   * Funcao: define o roteador de destino do percurso do pacote de solicitacao
   * Parametros: Roteador destino - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setDestino(Roteador destino) {
		this.destino = destino;
	}
 
  /*
   * ***************************************************************
   * Metodo: getDestino
   * Funcao: retorna o roteador de destino do percurso do pacote de solicitacao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getDestino() {
		return destino;
	}
}
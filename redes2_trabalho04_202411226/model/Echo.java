/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 02/05/2026
* Ultima alteracao.: 28/05/2026
* Nome.............: Echo
* Funcao...........: Thread que gerencia as operacoes dos pacotes echo, responsaveis
										 por informarem o custo de cada enlace presente na sub rede.
                     
*************************************************************** */

package model;

import controller.TelaPrincipalController;
import java.lang.Thread;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Echo extends Thread {
	// Variaveis e instancias
	private Roteador origem;
	private Roteador destino;
	private ImageView envelope;
	private long latencia;
	private double posX;
	private double posY;
	private boolean encerrou;
	private static final Image echoanswered = new Image(Echo.class.getResource("/img/echoanswered.gif").toExternalForm());

  /*
   * ***************************************************************
   * Metodo: Echo
   * Funcao: inicializa uma nova instancia da classe Echo
   * Parametros: Roteador origem - roteador do qual o pacote de solicitacao se originou
                 Roteador destino - roteador para o qual o pacote de solicitacao sera encaminhado
	               ImageView envelope - imagem do pacote de solicitacao
   * Retorno: nenhum
   ****************************************************************/

	public Echo(Roteador origem, Roteador destino, ImageView envelope) {
		this.origem = origem;
		this.destino = destino;
		this.envelope = envelope;
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
		// Enquanto a Thread nao for interrompida
		while (!Thread.currentThread().isInterrupted() && TelaPrincipalController.controller.simulacaoAtiva) {
			// Define a posicao
			definirPosicao();

			// Exibe o pacote
	  	exibirPacote();

		  // Se movimenta ate o destino
		  movimentar(this.destino);

		  // Processa o pacote
		  processar();

		  // Se movimenta ate a origem
		  movimentar(this.origem);

      // Remove o pacote echo da subrede
		  Platform.runLater(() -> TelaPrincipalController.controller.removerEcho(this));

      // Interrompe o laco
			break;
		} // Fim do bloco while
	}

  /*
   * ***************************************************************
   * Metodo: definirPosicao
   * Funcao: define a posicao do pacote de solicitacao na sub rede
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: nenhum
   ****************************************************************/

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

  /*
   * ***************************************************************
   * Metodo: exibirPacote
   * Funcao: torna a imagem do pacote visivel na sub rede
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: nenhum
   ****************************************************************/

	private void exibirPacote() {
		envelope.setVisible(true);
	}

  /*
   * ***************************************************************
   * Metodo: movimentar
   * Funcao: movimenta o pacote echo para o roteador de destino
   * Parametros: Roteador r - roteador para o qual o pacote echo
                              sera encaminhado
   * Retorno: void
   ****************************************************************/

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
				// A Thread e posta para dormir por 20 ms
				Thread.sleep(20);
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
   * Metodo: processar
   * Funcao: realiza o processamento do pacote Echo
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	private void processar() {
		// Obtem a latencia da aresta que liga os roteadores
		latencia = TelaPrincipalController.controller.ps(origem, destino);

		// Adiciona o custo da origem ate o destino
		origem.adicionarCustoVizinho(destino, latencia);

    // A Thread dorme por 300 ms
		dormir(300);

		// Troca a imagem do pacote Echo
		Platform.runLater(() -> envelope.setImage(echoanswered));
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
			// Coloca a Thread para dormir por alguns ms
			Thread.sleep(valor);
		}
		catch (InterruptedException e) {
			// Em caso de excecao, a Thread eh interrompida
			Thread.currentThread().interrupt();
		} // Fim do bloco try/catch
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
   * Funcao: define o roteador de origem do percurso do pacote Echo
   * Parametros: Roteador origem - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setOrigem(Roteador origem) {
		this.origem = origem;
	}

  /*
   * ***************************************************************
   * Metodo: getOrigem
   * Funcao: retorna o roteador de origem do percurso do pacote Echo
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getOrigem() {
		return origem;
	}

  /*
   * ***************************************************************
   * Metodo: setDestino
   * Funcao: define o roteador de destino do percurso do pacote Echo
   * Parametros: Roteador destino - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setDestino(Roteador destino) {
		this.destino = destino;
	}
 
  /*
   * ***************************************************************
   * Metodo: getDestino
   * Funcao: retorna o roteador de destino do percurso do pacote Echo
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getDestino() {
		return destino;
	}

  /*
   * ***************************************************************
   * Metodo: setEncerrou
   * Funcao: define se o pacote Echo encerrou suas operacoes ou nao
   * Parametros: boolean e - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setEncerrou(boolean e) {
		this.encerrou = e;
	}

  /*
   * ***************************************************************
   * Metodo: encerrou
   * Funcao: retorna se o pacote Echo encerrou suas operacoes ou nao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

	public boolean encerrou() {
		return encerrou;
	}
}
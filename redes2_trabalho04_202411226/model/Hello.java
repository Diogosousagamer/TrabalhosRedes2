/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/05/2026
* Ultima alteracao.: 09/05/2026
* Nome.............: Hello
* Funcao...........: Thread que gerencia os pacotes Hello, usados para conhecer 
                     os vizinhos de cada roteador.
                     
*************************************************************** */

package model;

import controller.TelaPrincipalController;
import java.lang.Thread;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Hello extends Thread {
	// Variaveis e instancias
	private ImageView hello;
	private Roteador origem;
	private Roteador destino;
	private double posX;
	private double posY;
	private boolean chegou;
	private static final Image perolaVolta = new Image(Hello.class.getResource("/img/helloanswered.png").toExternalForm());

	public Hello(ImageView hello, Roteador origem, Roteador destino) {
		this.hello = hello;	
		this.origem = origem;
		this.destino = destino;
		chegou = false;
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
		while (!Thread.currentThread().isInterrupted()) {
			// Define a posicao
			definirPosicao();

			// Movimenta o pacote ate o destino
			movimentar(destino);

			// Processa o pacote
			processar();

			// Movimenta ele de volta pra origem
			movimentar(origem);

			// Remove o pacote da sub rede
			TelaPrincipalController.controller.removerHello(this);

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
			hello.setLayoutX(posX);
			hello.setLayoutY(posY);
		}); // Fim do bloco Platform.runLater
	}

  /*
   * ***************************************************************
   * Metodo: movimentar
   * Funcao: movimenta o pacote hello para o roteador de destino
   * Parametros: Roteador r - roteador para o qual o pacote hello
                              sera encaminhado
   * Retorno: void
   ****************************************************************/

	private void movimentar(Roteador r) {
		// Obtem-se a posicao (X e Y) do destino
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
				hello.setLayoutX(xInt);
				hello.setLayoutY(yInt);
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
			hello.setLayoutX(posX);
			hello.setLayoutY(posY);
		}); // Fim do bloco Platform.runLater
	}

  /*
   * ***************************************************************
   * Metodo: processar
   * Funcao: realiza o processamento do pacote Hello
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	private void processar() {
		// Marca o destino como novo vizinho da origem
		origem.adicionarVizinho(destino);

		// Dorme por 300 ms
		dormir(300);

    // Inicio do bloco Platform.runLater
		Platform.runLater(() -> {
			// Atualiza a origem
			TelaPrincipalController.controller.atualizarRoteador(origem);
			TelaPrincipalController.controller.alterarRoteadorNosVizinhos(origem);

			// Troca a imagem
			hello.setImage(perolaVolta);
		}); // Fim do bloco Platform.runLater
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
   * Metodo: setHello
   * Funcao: define a imagem do pacote Hello
   * Parametros: ImageView h - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setHello(ImageView h) {
		this.hello = h;
	}

  /*
   * ***************************************************************
   * Metodo: getHello
   * Funcao: retorna a imagem do pacote Hello
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: ImageView
   ****************************************************************/

	public ImageView getHello() {
		return hello;
	}

	/*
   * ***************************************************************
   * Metodo: setOrigem
   * Funcao: define o roteador de origem do percurso do pacote Hello
   * Parametros: Roteador origem - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setOrigem(Roteador origem) {
		this.origem = origem;
	}

  /*
   * ***************************************************************
   * Metodo: getOrigem
   * Funcao: retorna o roteador de origem do percurso do pacote Hello
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getOrigem() {
		return origem;
	}

  /*
   * ***************************************************************
   * Metodo: setDestino
   * Funcao: define o roteador de destino do percurso do pacote Hello
   * Parametros: Roteador destino - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setDestino(Roteador destino) {
		this.destino = destino;
	}
 
  /*
   * ***************************************************************
   * Metodo: getDestino
   * Funcao: retorna o roteador de destino do percurso do pacote Hello
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getDestino() {
		return destino;
	}

  /*
   * ***************************************************************
   * Metodo: setChegou
   * Funcao: define se o pacote Hello chegou ao objetivo ou nao
   * Parametros: boolean chegou - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setChegou(boolean chegou) {
		this.chegou = chegou;
	}

  /*
   * ***************************************************************
   * Metodo: chegou
   * Funcao: retorna se o pacote Hello chegou ao objetivo ou nao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

	public boolean chegou() {
		return chegou;
	}
}
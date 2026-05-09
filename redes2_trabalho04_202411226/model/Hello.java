/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/05/2026
* Ultima alteracao.: 08/05/2026
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
	private ImageView hello;
	private Roteador origem;
	private Roteador destino;
	private double posX;
	private double posY;
	private boolean chegou;
	private final Image perolaVolta = new Image(getClass().getResource("/img/answered.png").toExternalForm());

	public Hello(ImageView hello, Roteador origem, Roteador destino) {
		this.hello = hello;	
		this.origem = origem;
		this.destino = destino;
		chegou = false;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			definirPosicao();
			movimentar(destino);
			processar();
			movimentar(origem);
			setChegou(true);
			TelaPrincipalController.controller.removerHello(this);
			break;
		}
	}

	private void definirPosicao() {
		this.posX = origem.getPosX();
		this.posY = origem.getPosY();

		Platform.runLater(() -> {
			hello.setLayoutX(posX);
			hello.setLayoutY(posY);
		});
	}

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
				// A Thread e posta para dormir por 25 ms
				Thread.sleep(25);
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

	private void processar() {
		destino.adicionarVizinho(origem);

		Platform.runLater(() -> {
			TelaPrincipalController.controller.atualizarRoteador(destino);
			TelaPrincipalController.controller.alterarRoteadorNosVizinhos(destino);
			hello.setImage(perolaVolta);
		});
	}

	public void setHello(ImageView h) {
		this.hello = h;
	}

	public ImageView getHello() {
		return hello;
	}

	public void setOrigem(Roteador o) {
		this.origem = o;
	}

	public Roteador getOrigem() {
		return origem;
	}

	public void setDestino(Roteador d) {
		this.destino = d;
	}

	public Roteador getDestino() {
		return destino;
	}

	public void setChegou(boolean chegou) {
		this.chegou = chegou;
	}

	public boolean chegou() {
		return chegou;
	}
}
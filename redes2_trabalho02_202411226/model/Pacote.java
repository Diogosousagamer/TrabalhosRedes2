/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 30/03/2026
* Ultima alteracao.: 30/03/2026
* Nome.............: Pacote
* Funcao...........: Thread que gerencia as operacoes de cada pacote.
                     
*************************************************************** */

package model;

import java.lang.Thread;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class Pacote extends Thread {
	private double posX;
	private double posY;
	private ImageView envelope;
	private Roteador origem;
	private Roteador destino;
	private ArrayList<Roteador> caminho;
	private int ponteiro = 0;
	private boolean liberado;

	public Pacote(ImageView envelope, Roteador origem, Roteador destino) {
		this.envelope = envelope;
		this.origem = origem;
		this.destino = destino;
		caminho = new ArrayList<>();
		liberado = false;
	}

	@Override 
	public void run() {
		// Carrega a posicao
		definirPosicao();

		// Aguarda o calculo do caminho para assim percorre-lo
		aguardar();

		// Percorre o caminho completo
		percorrerCaminho();
	}

	private void percorrerCaminho() {
		Roteador proximoDestino = null;

		while (ponteiro < caminho.size()) {
			proximoDestino = caminho.get(ponteiro);
			movimentar(proximoDestino);
			ponteiro++;

			try {
				Thread.sleep(300);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		ponteiro = 0;
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

	private void definirPosicao() {
		posX = envelope.getLayoutX();
		posY = envelope.getLayoutY();
	}

	public void adicionarRoteadorAoCaminho(Roteador r) {
		caminho.add(r);
	}

	private void aguardar() {
		while (!liberado) {
			try {
				Thread.sleep(200);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public void liberar() {
		liberado = true;
	}
}
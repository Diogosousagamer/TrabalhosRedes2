/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 16/03/2026
* Ultima alteracao.: 20/03/2026
* Nome.............: Pacote
* Funcao...........: Thread que gerencia as operacoes de cada pacote.
                     
*************************************************************** */

package model;

import java.util.ArrayList;
import java.lang.Thread;
import javafx.scene.image.ImageView;
import javafx.application.Platform;
import controller.TelaPrincipalController;

public class Pacote extends Thread {
	private ImageView envelope;
	private double posX;
	private double posY;
	private Roteador roteadorInicial;
	private int versao;
	private int tempoDeVida;

	public Pacote(ImageView envelope, int versao, Roteador destino) {
		this.envelope = envelope;
		this.versao = versao;
		this.roteadorInicial = roteadorInicial;
	}

	public Pacote(ImageView envelope, int versao, Roteador destino, int tempoDeVida) {
		this.envelope = envelope;
		this.versao = versao;
		this.roteadorInicial = roteadorInicial;
		this.tempoDeVida = tempoDeVida;
	}

  @Override
	public void run() {
		if (destino.isOrigem()) {
			encaminharPacotesVizinhos();
		}
		else {
			movimentar(destino);
		}
	}

	public void definirPosicao() {
		posX = envelope.getLayoutX();
		posY = envelope.getLayoutY();
	}

	private void movimentar(Roteador roteador) {
		double destinoX = roteador.getPosX();
		double destinoY = roteador.getPosY();

		double deltaX = destinoX - posX;
		double deltaY = destinoY - posY;

		int passos = Math.max((int) Math.abs(deltaX), (int) Math.abs(deltaY));

		if (passos == 0) {
			return;
		}

		double passoX = deltaX / passos;
		double passoY = deltaY / passos;

		for (int i = 0; i < passos && !Thread.currentThread().isInterrupted(); i++) {
			posX += passoX;
			posY += passoY;

			final int xInt = (int) Math.round(posX);
			final int yInt = (int) Math.round(posY);

			Platform.runLater(() -> {
				envelope.setLayoutX(xInt);
				envelope.setLayoutY(yInt);
			});

			try {
				Thread.sleep(16);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		posX = destinoX;
		posY = destinoY;

		Platform.runLater(() -> {
			envelope.setLayoutX(posX);
			envelope.setLayoutY(posY);
		});		

		if (versao != 0) roteador.ocupar();
		if ((versao == 2) || (versao == 3)) decrementarTempoDeVida();

		gerarVizinhos();
	}

	private void chegarNoDestino(Host destino) {
		double destinoX = destino.getPosX();
		double destinoY = destino.getPosY();

		double deltaX = destinoX - posX;
		double deltaY = destinoY - posY;

		int passos = Math.max((int) Math.abs(deltaX), (int) Math.abs(deltaY));

		if (passos == 0) {
			return;
		}

		double passoX = deltaX / passos;
		double passoY = deltaY / passos;

		for (int i = 0; i < passos && !Thread.currentThread().isInterrupted(); i++) {
			posX += passoX;
			posY += passoY;

			final int xInt = (int) Math.round(posX);
			final int yInt = (int) Math.round(posY);

			Platform.runLater(() -> {
				envelope.setLayoutX(xInt);
				envelope.setLayoutY(yInt);
			});

			try {
				Thread.sleep(16);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		posX = destinoX;
		posY = destinoY;

		Platform.runLater(() -> {
			envelope.setLayoutX(posX);
			envelope.setLayoutY(posY);
		});	

		if ((versao == 2) || (versao == 3)) decrementarTempoDeVida();
	}

	private void decrementarTempoDeVida() {
		tempoDeVida--;
	}

	private void encaminharPacotesVizinhos() {
		if (roteadorInicial.getIntermediario() && !roteadorInicial.getHostProximo().getTransmissor()) {
			Host destino = roteadorInicial.getHostProximo();
			chegarNoDestino(destino);

			if (!roteadorInicial.getHostProximo().getTransmissor() && roteadorInicial.getHostProximo().getRecebeu()) {
				TelaPrincipalController.controller.interromper();
				return;
			}
		}

		ArrayList<Roteador> vizinhos = roteadorInicial.getVizinhos();

		for (Roteador v : vizinhos) {
			TelaPrincipalController.controller.gerarMaisPacotes(roteadorInicial, v);
		}
	}
}
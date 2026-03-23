/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 16/03/2026
* Ultima alteracao.: 23/03/2026
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
	private double posX;
	private double posY;
	private ImageView envelope;
	private int tempoDeVida;
	private int versao;
	private Roteador origem;
	private Roteador destino;
	private Roteador vindoDe;

	public Pacote(ImageView envelope, int versao, Roteador origem, Roteador destino) {
		this.envelope = envelope;
		this.versao = versao;
		this.origem = origem;
		this.destino = destino;
	}

	public Pacote(ImageView envelope, int versao, Roteador origem, Roteador destino, Roteador vindoDe) {
		this.envelope = envelope;
		this.versao = versao;
		this.origem = origem;
		this.destino = destino;
		this.vindoDe = vindoDe;
	}

	public Pacote(ImageView envelope, int versao, Roteador origem, Roteador destino, Roteador vindoDe, int tempoDeVida) {
		this.envelope = envelope;
		this.versao = versao;
		this.origem = origem;
		this.destino = destino;
		this.vindoDe = vindoDe;
		this.tempoDeVida = tempoDeVida;
	}

  @Override
	public void run() {
		if (vindoDe == null && destino.isOrigem()) {
			encaminharPacotesVizinhos();
			return;
		}

		movimentar(destino);
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

    if (versao > 1) decrementarTempoDeVida();
		encaminharPacotesVizinhos();
	}

	private void encaminharPacotesVizinhos() {
		if (destino.isDestino()) {
			TelaPrincipalController.controller.interromper();
			return;
		}

		if (Thread.currentThread().isInterrupted()) return;

		if (versao > 1 && this.tempoDeVida <= 0) {
			TelaPrincipalController.controller.removerPacote(this);
			return;
		}

    Roteador proximoDestino = null;
		ArrayList<Roteador> vizinhos = destino.getVizinhos();

		for (Roteador v : vizinhos) {
			if (versao > 0 && (vindoDe != null && v.equals(vindoDe))) {
				continue;
			}

			if (proximoDestino == null) {
				proximoDestino = v;
			}
			else {
				TelaPrincipalController.controller.gerarMaisPacotes(destino, v, destino);
			}
		}

		if (proximoDestino != null) {
			this.vindoDe = destino;
			this.destino = proximoDestino;
			movimentar(destino);
		}
		else {
			TelaPrincipalController.controller.removerPacote(this);
		}
	}

	private void decrementarTempoDeVida() {
		tempoDeVida--;
	}

	public void setEnvelope(ImageView envelope) {
		this.envelope = envelope;
	}

	public ImageView getEnvelope() {
		return envelope;
	}
}
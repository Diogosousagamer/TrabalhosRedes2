/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 19/04/2026
* Ultima alteracao.: 19/04/2026
* Nome.............: Pacote
* Funcao...........: Thread que gerencia as operacoes de cada pacote.
                     
*************************************************************** */

package model;

import controller.TelaPrincipalController;
import java.lang.Thread;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class Pacote extends Thread {
	// Variaveis e instancias
	private double posX;
	private double posY;
	private ImageView envelope;
	private Roteador origem;
	private Roteador destino;
	private ArrayList<Roteador> caminho;
	private int ponteiro = 0;
	private boolean liberado;

	/*
   * ***************************************************************
   * Metodo: Pacote
   * Funcao: inicializa uma nova instancia da classe Pacote (V1 do algoritmo de inundacao)
   * Parametros: ImageView envelope - imagem do pacote
                 Roteador origem - roteador do qual se originou
                 Roteador destino - roteador para o qual o pacote sera encaminhado
   * Retorno: nenhum
   ****************************************************************/

	public Pacote(ImageView envelope, Roteador origem, Roteador destino) {
		this.envelope = envelope;
		this.origem = origem;
		this.destino = destino;
		caminho = new ArrayList<>();
		liberado = false;
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
		// Carrega a posicao
		definirPosicao();

		// Aguarda o calculo do caminho para assim percorre-lo
		aguardar();

		// Percorre o caminho completo
		percorrerCaminho();
	}

	/*
   * ***************************************************************
   * Metodo: definirPosicao
   * Funcao: define o valor inicial dos contadores de posicao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: nenhum
   ****************************************************************/

	private void definirPosicao() {
		Platform.runLater(() -> {
			envelope.setLayoutX(origem.getPosX());
			envelope.setLayoutY(origem.getPosY());

			// Armazena as posicoes iniciais da imagem nos eixos X e Y
			// nos contadores de posicao
			posX = envelope.getLayoutX();
			posY = envelope.getLayoutY();
		});
	}

	/*
   * ***************************************************************
   * Metodo: percorrerCaminho
   * Funcao: executa a percursao do caminho
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	private void percorrerCaminho() {
		// Roteador que representa o proximo destino dentro
		// do caminho do roteador
		Roteador proximoDestino = null;

    // Inicio do bloco while
    // Enquanto o ponteiro nao apontar para o destino final
		while (ponteiro < caminho.size()) {
			// Obtem o roteador apontado pelo valor atual do ponteiro
			proximoDestino = caminho.get(ponteiro);

			// Movimenta o pacote ate o destino atual
			movimentar(proximoDestino);

			// Incrementa o ponteiro
			ponteiro++;

      // Inicio do bloco try/catch
			try {
				// O pacote eh posto para dormir por 300 ms
				Thread.sleep(300);
			}
			catch (InterruptedException e) {
				// Em caso de excecao, a Thread eh interrompida
				Thread.currentThread().interrupt();
			} // Fim do bloco try/catch
		} // Fim do bloco while

    // Interrompe a simulacao apos o fim do while
		// TelaPrincipalController.controller.interromper(this);
	}

  /*
   * ***************************************************************
   * Metodo: movimentar
   * Funcao: movimenta um pacote para um roteador
   * Parametros: Roteador r - roteador para o qual o pacote sera encaminhado
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
   * Metodo: adicionarRoteadorAoCaminho
   * Funcao: adiciona um roteador no inicio do caminho a ser percorrido
   * Parametros: Roteador r - roteador a ser adicionado
   * Retorno: void
   ****************************************************************/

	public void adicionarRoteadorAoCaminho(Roteador r) {
		caminho.add(0, r);
	}

	/*
   * ***************************************************************
   * Metodo: aguardar
   * Funcao: coloca o pacote para aguardar enquanto o caminho estiver
             sendo montado
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	private void aguardar() {
		// Inicio do bloco while
		// Enquanto o pacote nao for liberado para realizar o seu percurso
		while (!liberado) {
			// Inicio do bloco try/catch
			try {
				// O pacote eh posto para dormir por 200 ms
				Thread.sleep(200);
			}
			catch (InterruptedException e) {
				// Em caso de excecao, a Thread eh interrompida
				Thread.currentThread().interrupt();
			} // Fim do bloco try/catch
		} // Fim do bloco while
	}

  /*
   * ***************************************************************
   * Metodo: liberar
   * Funcao: libera o pacote para realizar o seu percurso
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void liberar() {
		liberado = true;
	}

  /*
   * ***************************************************************
   * Metodo: setEnvelope
   * Funcao: define a imagem do pacote
   * Parametros: ImageView envelope - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setEnvelope(ImageView envelope) {
		this.envelope = envelope;
	}

  /*
   * ***************************************************************
   * Metodo: getEnvelope
   * Funcao: retorna a imagem do pacote
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: ImageView
   ****************************************************************/

	public ImageView getEnvelope() {
		return envelope;
	}

  /*
   * ***************************************************************
   * Metodo: setOrigem
   * Funcao: define o roteador de origem do percurso do pacote
   * Parametros: Roteador origem - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setOrigem(Roteador origem) {
		this.origem = origem;
	}

  /*
   * ***************************************************************
   * Metodo: getOrigem
   * Funcao: retorna o roteador de origem do percurso do pacote
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getOrigem() {
		return origem;
	}

  /*
   * ***************************************************************
   * Metodo: setDestino
   * Funcao: define o roteador de destino do percurso do pacote
   * Parametros: Roteador destino - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setDestino(Roteador destino) {
		this.destino = destino;
	}
 
  /*
   * ***************************************************************
   * Metodo: getDestino
   * Funcao: retorna o roteador de destino do percurso do pacote
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getDestino() {
		return destino;
	}
}
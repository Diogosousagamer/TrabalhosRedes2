/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/05/2026
* Ultima alteracao.: 25/05/2026
* Nome.............: PacoteEstadoEnlace
* Funcao...........: Thread que gerencia os pacotes de estado de enlace.
                     
*************************************************************** */

package model;

import controller.TelaPrincipalController;
import java.lang.Thread;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class PacoteEstadoEnlace extends Thread {
	// Variaveis e instancias
	private ImageView link;
	private int numeroSequencia;
	private int idade;
	private HashMap<Roteador, Long> custoVizinhos;
	private Roteador origem;
  private Roteador linhaChegada;
	private Roteador destino;
	private double posX;
	private double posY;

	public PacoteEstadoEnlace(ImageView link, int numeroSequencia, int idade, Roteador origem, Roteador destino, Roteador linhaChegada) {
		this.link = link;
		this.numeroSequencia = numeroSequencia;
		this.idade = idade;
		this.origem = origem;
		this.destino = destino;
    this.linhaChegada = linhaChegada;
		custoVizinhos = new HashMap<>();
	}

	@Override
	public void run() {
		movimentar(this.destino);
    processar();
	}

	public void definirPosicao(Roteador r) {
		posX = r.getPosX();
		posY = r.getPosY();

		Platform.runLater(() -> {
			link.setLayoutX(posX);
			link.setLayoutY(posY);
		});	
	}

  /*
   * ***************************************************************
   * Metodo: movimentar
   * Funcao: movimenta o pacote de estado de enlace para o roteador de destino
   * Parametros: Roteador r - roteador para o qual o pacote de estado
                              de enlace sera encaminhado
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
				link.setLayoutX(xInt);
				link.setLayoutY(yInt);
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
			link.setLayoutX(posX);
			link.setLayoutY(posY);
		}); // Fim do bloco Platform.runLater
	}

	private void processar() {
		BufferEnlace bufferDestino = destino.getBufferEnlace();
		EntradaBuffer entradaOrigem = bufferDestino.obterEntrada(origem);
		
		PacoteEstadoEnlace pacoteEntradaOrigem = entradaOrigem.getPacoteAtual();
		int seqAtual = (pacoteEntradaOrigem != null) ? pacoteEntradaOrigem.getNumeroSequencia() : 0;

		if (pacoteEntradaOrigem == null || this.numeroSequencia > seqAtual) {
			entradaOrigem.setPacoteAtual(this);
			bufferDestino.alterarEntrada(entradaOrigem);
			CopyOnWriteArrayList<Roteador> vizinhosDestino = destino.getVizinhos();

			for (Roteador v : vizinhosDestino) {
				if (v.getNome().equals(linhaChegada.getNome())) {
					bufferDestino.alterarFlagTransmissao(origem, v, false);
					bufferDestino.alterarFlagConfirmacao(origem, v, true);
				}
				else {
					bufferDestino.alterarFlagTransmissao(origem, v, true);
					bufferDestino.alterarFlagConfirmacao(origem, v, false);
				}
			}

			encaminharParaOsVizinhos();
		}
		else {
			BufferEnlace bufferChegada = linhaChegada.getBufferEnlace();
			if (bufferChegada != null) bufferChegada.alterarFlagConfirmacao(origem, destino, true);
			Platform.runLater(() -> TelaPrincipalController.controller.removerPacoteEnlace(this));
		}
	}

	private void encaminharParaOsVizinhos() {
		CopyOnWriteArrayList<Roteador> vizinhosDestino = destino.getVizinhos();
		BufferEnlace bufferDestino = destino.getBufferEnlace();

		for (Roteador v : vizinhosDestino) {
			if (v.getNome().equals(linhaChegada.getNome())) continue;
      PacoteEstadoEnlace pacoteEnlace = TelaPrincipalController.controller.enviarPacoteEnlace
                                        (origem, v, destino, numeroSequencia, idade);
      pacoteEnlace.definirPosicao(destino);
      pacoteEnlace.setCustoVizinhos(custoVizinhos);
      pacoteEnlace.start();
      dormir(200);
		}

		Platform.runLater(() -> TelaPrincipalController.controller.removerPacoteEnlace(this));
	}

	public void adicionarCustoVizinho(Roteador r, long valor) {
		if (!custoVizinhos.containsKey(r)) custoVizinhos.put(r, valor);
	}

	public void decrementarIdade() {
		idade--;
	}

	private void dormir(long valor) {
		try {
			Thread.sleep(valor);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public void setLink(ImageView link) {
		this.link = link;
	}

	public ImageView getLink() {
		return link;
	}

	public void setNumeroSequencia(int seq) {
		this.numeroSequencia = seq;
	}

	public int getNumeroSequencia() {
		return numeroSequencia;
	}

	public void setIdade(int idade) {
		this.idade = idade;
	}

	public int getIdade() {
		return idade;
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

	public void setLinhaChegada(Roteador linhaChegada) {
		this.linhaChegada = linhaChegada;
	}

	public Roteador getLinhaChegada() {
		return linhaChegada;
	}

  public void setCustoVizinhos(HashMap<Roteador, Long> c)  {
    this.custoVizinhos = c;
  } 

  public HashMap<Roteador, Long> getCustoVizinhos() {
    return custoVizinhos;
  }

	public void setPosX(double x) {
		this.posX = x;
	}

	public double getPosX() {
		return posX;
	}

	public void setPosY(double y) {
		this.posY = y;
	}

	public double getPosY() {
		return posY;
	}
}
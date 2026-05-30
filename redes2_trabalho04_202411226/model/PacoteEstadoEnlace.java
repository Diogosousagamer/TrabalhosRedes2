/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/05/2026
* Ultima alteracao.: 30/05/2026
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
	private HashMap<Roteador, Long> custoVizinhos;
	private Roteador origem;
  private Roteador linhaChegada;
	private Roteador destino;
	private double posX;
	private double posY;

  /*
   * ***************************************************************
   * Metodo: PacoteEstadoEnlace
   * Funcao: inicializa uma nova instancia da classe PacoteEstadoEnlace
   * Parametros: ImageView link - imagem do pacote
                 int numeroSequencia - numero de sequencia do pacote
                 Roteador origem - roteador que criou o pacote
                 Roteador destino - roteador de destino do percurso
                 Roteador linhaChegada - linha de saida usada para chegar
                                         ao destino final
   * Retorno: nenhum
   ****************************************************************/

	public PacoteEstadoEnlace(ImageView link, int numeroSequencia, Roteador origem, Roteador destino, Roteador linhaChegada) {
		this.link = link;
		this.numeroSequencia = numeroSequencia;
		this.origem = origem;
		this.destino = destino;
    this.linhaChegada = linhaChegada;
		custoVizinhos = new HashMap<>();
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
		// Enquanto a Thread e a simulacao nao forem interrompidas
		while (!Thread.currentThread().isInterrupted() && TelaPrincipalController.controller.simulacaoAtiva) {
			// Movimenta o pacote ate o destino final
			movimentar(this.destino);

			// Processa o pacote de estado de enlace
    	processar();

    	// Interrompe o laco
    	break;
		} // Fim do bloco while
	}

  /*
   * ***************************************************************
   * Metodo: definirPosicao
   * Funcao: define a posicao inicial do pacote de estado de enlace
   * Parametros: Roteador r - roteador onde o pacote sera posicionado
   * Retorno: void
   ****************************************************************/

	public void definirPosicao(Roteador r) {
		// Carrega os contadores com a posicao do roteador
		posX = r.getPosX();
		posY = r.getPosY();

    // Inicio do bloco Platform.runLater
		Platform.runLater(() -> {
			// Posiciona a imagem em cima do valor inicial
			// dos contadores
			link.setLayoutX(posX);
			link.setLayoutY(posY);
		});	// Fim do bloco Platform.runLater
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

  /*
   * ***************************************************************
   * Metodo: processar
   * Funcao: processa as informacoes do pacote de estado de enlace
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	private void processar() {
		// Acessa o buffer do destino e a entrada correspondente a origem do pacote
		BufferEnlace bufferDestino = destino.getBufferEnlace();
		EntradaBuffer entradaOrigem = bufferDestino.obterEntrada(origem);
		
		// Obtem o pacote mantido pelo buffer e o seu numero de sequencia
		PacoteEstadoEnlace pacoteEntradaOrigem = entradaOrigem.getPacoteAtual();
		int seqAtual = (pacoteEntradaOrigem != null) ? pacoteEntradaOrigem.getNumeroSequencia() : -1;

    // Inicio do bloco if
    // Se o buffer nao estiver mantendo nenhum pacote ou o numero de sequencia deste pacote
    // for maior que o numero de sequencia do pacote atual
		if (pacoteEntradaOrigem == null || this.numeroSequencia > seqAtual) {
			// O buffer passa a manter este pacote, alterando a entrada da origem
			entradaOrigem.setPacoteAtual(this);
			bufferDestino.alterarEntrada(entradaOrigem);

			// Obtem os vizinhos do destino
			CopyOnWriteArrayList<Roteador> vizinhosDestino = destino.getVizinhos();

      // Inicio do bloco for
			for (Roteador v : vizinhosDestino) {
				// Inicio do bloco if/else
				// Se o vizinho corresponder ao roteador pelo qual o pacote chegou
				if (v.getNome().equals(linhaChegada.getNome())) {
					// Desmarca a flag de transmissao e marca a flag de confirmacao do vizinho
					// na entrada da origem
					bufferDestino.alterarFlagTransmissao(origem, v, false);
					bufferDestino.alterarFlagConfirmacao(origem, v, true);
				}
				else {
					// Marca a flag de transmissao e desmarca a flag de confirmacao do vizinho
					// na entrada da origem
					bufferDestino.alterarFlagTransmissao(origem, v, true);
					bufferDestino.alterarFlagConfirmacao(origem, v, false);
				} // Fim do bloco if/else
			} // Fim do bloco for

      // Encaminha o pacote para os seus vizinhos
			encaminharParaOsVizinhos();
		}
		else { // Caso contrario
			// Obtem o buffer da linha de saida, alterando a flag de confirmacao do destino dentro
			// da entrada da origem
			BufferEnlace bufferChegada = linhaChegada.getBufferEnlace();
			if (bufferChegada != null) bufferChegada.alterarFlagConfirmacao(origem, destino, true);

			// Descarta o pacote de estado de enlace da sub rede
			Platform.runLater(() -> TelaPrincipalController.controller.removerPacoteEnlace(this));
		} // Fim do bloco if/else
	}

  /*
   * ***************************************************************
   * Metodo: encaminharParaOsVizinhos
   * Funcao: encaminha o pacote de estado de enlace para os vizinhos
             do destino
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	private void encaminharParaOsVizinhos() {
		// Obtem a lista de vizinhos do destino
		CopyOnWriteArrayList<Roteador> vizinhosDestino = destino.getVizinhos();

    // Inicio do bloco for
		for (Roteador v : vizinhosDestino) {
			// Envia um novo pacote de estado de enlace para cada vizinho, exceto
			// para o vizinho que enviou o pacote inicial
			if (v.getNome().equals(linhaChegada.getNome())) continue;
      PacoteEstadoEnlace pacoteEnlace = TelaPrincipalController.controller.enviarPacoteEnlace(origem, v, destino, numeroSequencia);
      pacoteEnlace.definirPosicao(destino);
      pacoteEnlace.setCustoVizinhos(custoVizinhos);
      pacoteEnlace.start();
      dormir(200);
		} // Fim do bloco for

    // Remove o pacote de estado de enlace da sub rede
		Platform.runLater(() -> TelaPrincipalController.controller.removerPacoteEnlace(this));
	}

  /*
   * ***************************************************************
   * Metodo: dormir
   * Funcao: coloca a Thread para dormir por alguns ms
   * Parametros: long valor - tempo de repouso
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
   * Metodo: setLink
   * Funcao: define a imagem do pacote de estado de enlace
   * Parametros: ImageView link - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setLink(ImageView link) {
		this.link = link;
	}

  /*
   * ***************************************************************
   * Metodo: getLink
   * Funcao: retorna a imagem do pacote de estado de enlace
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: ImageView
   ****************************************************************/

	public ImageView getLink() {
		return link;
	}

	/*
   * ***************************************************************
   * Metodo: setOrigem
   * Funcao: define o roteador de origem do percurso do pacote de estado de enlace
   * Parametros: Roteador origem - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setOrigem(Roteador origem) {
		this.origem = origem;
	}

  /*
   * ***************************************************************
   * Metodo: getOrigem
   * Funcao: retorna o roteador de origem do percurso do pacote de estado de enlace
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getOrigem() {
		return origem;
	}

  /*
   * ***************************************************************
   * Metodo: setDestino
   * Funcao: define o roteador de destino do percurso do pacote de estado de enlace
   * Parametros: Roteador destino - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setDestino(Roteador destino) {
		this.destino = destino;
	}
 
  /*
   * ***************************************************************
   * Metodo: getDestino
   * Funcao: retorna o roteador de destino do percurso do pacote de estado de enlace
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getDestino() {
		return destino;
	}

  /*
   * ***************************************************************
   * Metodo: setDestino
   * Funcao: define o roteador de destino do percurso do pacote de estado de enlace
   * Parametros: Roteador destino - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setLinhaChegada(Roteador linhaChegada) {
		this.linhaChegada = linhaChegada;
	}

  /*
   * ***************************************************************
   * Metodo: getLinhaChegada
   * Funcao: retorna a linha de chegada do percurso do pacote de estado de enlace
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getLinhaChegada() {
		return linhaChegada;
	}

  /*
   * ***************************************************************
   * Metodo: setNumeroSequencia
   * Funcao: define o numero de sequencia do pacote de estado de enlace
   * Parametros: int seq - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setNumeroSequencia(int seq) {
		this.numeroSequencia = seq;
	}

  /*
   * ***************************************************************
   * Metodo: getNumeroSequencia
   * Funcao: retorna o numero de sequencia do pacote de estado de enlace
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: int
   ****************************************************************/

	public int getNumeroSequencia() {
		return numeroSequencia;
	}

  /*
   * ***************************************************************
   * Metodo: setCustoVizinhos
   * Funcao: define o custo dos vizinhos da origem transportados
             pelo pacote de estado de enlace
   * Parametros: HashMap<Roteador, Long> c - lista a ser definida
   * Retorno: void
   ****************************************************************/

  public void setCustoVizinhos(HashMap<Roteador, Long> c) {
    this.custoVizinhos = c;
  } 

  /*
   * ***************************************************************
   * Metodo: getCustoVizinhos
   * Funcao: retorna o custo dos vizinhos da origem transportados
             pelo pacote de estado de enlace
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: HashMap<Roteador, Long>
   ****************************************************************/

  public HashMap<Roteador, Long> getCustoVizinhos() {
    return custoVizinhos;
  }

	/*
   * ***************************************************************
   * Metodo: setPosX
   * Funcao: define a posicao do pacote de estado de enlace no eixo X
   * Parametros: double posX - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setPosX(double posX) {
    this.posX = posX;
  }

  /*
   * ***************************************************************
   * Metodo: getPosX
   * Funcao: retorna a posicao do pacote de estado de enlace no eixo X
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: double
   ****************************************************************/

  public double getPosX() {
    return posX;
  }

  /*
   * ***************************************************************
   * Metodo: setPosY
   * Funcao: define a posicao do pacote de estado de enlace no eixo Y
   * Parametros: double posY - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setPosY(double posY) {
    this.posY = posY;
  }

  /*
   * ***************************************************************
   * Metodo: getPosY
   * Funcao: retorna a posicao do pacote de estado de enlace no eixo Y
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: double
   ****************************************************************/

  public double getPosY() {
    return posY;
  }
}
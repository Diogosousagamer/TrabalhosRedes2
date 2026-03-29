/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 16/03/2026
* Ultima alteracao.: 26/03/2026
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
	// Variaveis e instancias
	private double posX;
	private double posY;
	private ImageView envelope;
	private int tempoDeVida;
	private int versao;
	private Roteador origem;
	private Roteador destino;
	private Roteador vindoDe;
	private ArrayList<Roteador> roteadoresVisitados = new ArrayList<>();

  /*
   * ***************************************************************
   * Metodo: Pacote
   * Funcao: inicializa uma nova instancia da classe Pacote (V1 do algoritmo de inundacao)
   * Parametros: ImageView envelope - imagem do pacote
                 int versao - versao do algoritmo de inundacao
                 Roteador origem - roteador do qual se originou
                 Roteador destino - roteador para o qual o pacote sera encaminhado
   * Retorno: nenhum
   ****************************************************************/

	public Pacote(ImageView envelope, int versao, Roteador origem, Roteador destino) {
		this.envelope = envelope;
		this.versao = versao;
		this.origem = origem;
		this.destino = destino;
	}

  /*
   * ***************************************************************
   * Metodo: Pacote
   * Funcao: inicializa uma nova instancia da classe Pacote (V2 do algoritmo de inundacao)
   * Parametros: ImageView envelope - imagem do pacote
                 int versao - versao do algoritmo de inundacao
                 Roteador origem - roteador do qual se originou
                 Roteador destino - roteador para o qual o pacote sera encaminhado
                 Roteador vindoDe - linha de saida do qual ele chegou
   * Retorno: nenhum
   ****************************************************************/

	public Pacote(ImageView envelope, int versao, Roteador origem, Roteador destino, Roteador vindoDe) {
		this.envelope = envelope;
		this.versao = versao;
		this.origem = origem;
		this.destino = destino;
		this.vindoDe = vindoDe;
	}

  /*
   * ***************************************************************
   * Metodo: Pacote
   * Funcao: inicializa uma nova instancia da classe Pacote (V3 do algoritmo de inundacao)
   * Parametros: ImageView envelope - imagem do pacote
                 int versao - versao do algoritmo de inundacao
                 Roteador origem - roteador do qual se originou
                 Roteador destino - roteador para o qual o pacote sera encaminhado
                 Roteador vindoDe - linha de saida do qual ele chegou
                 int tempoDeVida - tempo de vida do pacote na sub rede
   * Retorno: nenhum
   ****************************************************************/

	public Pacote(ImageView envelope, int versao, Roteador origem, Roteador destino, Roteador vindoDe, int tempoDeVida) {
		this.envelope = envelope;
		this.versao = versao;
		this.origem = origem;
		this.destino = destino;
		this.vindoDe = vindoDe;
		this.tempoDeVida = tempoDeVida;
	}

  /*
   * ***************************************************************
   * Metodo: Pacote
   * Funcao: inicializa uma nova instancia da classe Pacote (V4 do algoritmo de inundacao)
   * Parametros: ImageView envelope - imagem do pacote
                 int versao - versao do algoritmo de inundacao
                 Roteador origem - roteador do qual se originou
                 Roteador destino - roteador para o qual o pacote sera encaminhado
                 Roteador vindoDe - linha de saida do qual ele chegou
                 int tempoDeVida - tempo de vida do pacote na sub rede
                 ArrayList<Roteador> roteadoresVisitados - lista de roteadores visitados
                 anteriormente pelo pacote
   * Retorno: nenhum
   ****************************************************************/

	public Pacote(ImageView envelope, int versao, Roteador origem, Roteador destino, Roteador vindoDe, int tempoDeVida, ArrayList<Roteador> roteadoresVisitados) {
		this.envelope = envelope;
		this.versao = versao;
		this.origem = origem;
		this.destino = destino;
		this.vindoDe = vindoDe;
		this.tempoDeVida = tempoDeVida;
		this.roteadoresVisitados = roteadoresVisitados;
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
		// Inicio do bloco if
		if (vindoDe == null && destino.isOrigem()) {
			// Comeca a encaminhar o pacote (e os pacotes gerados) para os vizinhos
			// se o destino corresponder ao pacote de origem da rota
			encaminharPacotesVizinhos();
			return;
		}

    // Para os demais casos, o pacote se movimenta ate atingir o destino
		movimentar(destino);
	}

  /*
   * ***************************************************************
   * Metodo: definirPosicao
   * Funcao: define o valor inicial dos contadores de posicao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: nenhum
   ****************************************************************/

	public void definirPosicao() {
		// Armazena as posicoes iniciais da imagem nos eixos X e Y
		// nos contadores de posicao
		posX = envelope.getLayoutX();
		posY = envelope.getLayoutY();
	}

  /*
   * ***************************************************************
   * Metodo: movimentar
   * Funcao: movimenta um pacote para um roteador
   * Parametros: Roteador roteador - roteador para o qual o pacote 
                 sera encaminhado
   * Retorno: void
   ****************************************************************/

	private void movimentar(Roteador roteador) {
		// Obtem-se a posicao (X e Y) do roteador de destino
		double destinoX = roteador.getPosX();
		double destinoY = roteador.getPosY();

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

    // Apos esses passos, sera realizada a geracao de pacotes
    // e o seu devido encaminhamento para os roteadores vizinhos
		encaminharPacotesVizinhos();
	}

  /*
   * ***************************************************************
   * Metodo: encaminharPacotesVizinhos
   * Funcao: gera outros pacotes a serem encaminhados para os vizinhos
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	private void encaminharPacotesVizinhos() {
		// Inicio do bloco if
		if (destino.isDestino()) {
			// Interrompe a simulacao (e o metodo) se o destino for o destino final da rota
			TelaPrincipalController.controller.interromper();
			return;
		} // Fim do bloco if

    // Interrompe o metodo se a Thread for interrompida
		if (Thread.currentThread().isInterrupted()) return;

    // Inicio do bloco if
    // Se a versao do algoritmo for da 3.0 em diante
		if (versao > 1) {
			// Decrementa o tempo de vida
			decrementarTempoDeVida();
		
		  // Inicio do bloco if
			if (this.tempoDeVida <= 0) {
				TelaPrincipalController.controller.removerPacote(this);
				return;
			} // Fim do bloco if
		} // Fim do bloco if

    // Marca o destino como um roteador ja visitado se a versao 4.0
    // do algoritmo de inundacao estiver sendo executada
		if (versao == 3) adicionarRoteadorVisitado(destino);

    // Instancia que representara o proximo destino do pacote
    Roteador proximoDestino = null;

    // Obtem os vizinhos do destino para realizar o encaminhamento
		ArrayList<Roteador> vizinhos = destino.getVizinhos();

    // Inicio do bloco for
    // Percorre-se a lista de vizinhos para fazer a geracao dos pacotes
    // e o devido encaminhamento deles
		for (Roteador v : vizinhos) {
			// O loop e retomado se o vizinho buscado tiver sido a linha de saida 
			// pela qual ele chegou
			if (versao > 0 && (vindoDe != null && v.equals(vindoDe))) continue;

			// O loop e retomado se o vizinho buscado ja estiver sido visitado anteriormente
			// desde que a versao 4.0 do algoritmo de inundacao esteja sendo executada
			if (versao == 3 && visitado(v)) continue;

      // Inicio do bloco if/else
      // Se o proximo destino ainda nao tiver sido selecionado
			if (proximoDestino == null) {
				// Selecionamos o primeiro vizinho buscado como o proximo destino
				// para o qual o pacote sera encaminhado
				proximoDestino = v;
			}
			else {
				// Caso contrario, um novo pacote eh gerado e encaminhado para o proximo vizinho
				TelaPrincipalController.controller.gerarMaisPacotes(destino, v, destino);
			} // Fim do bloco if/else
		} // Fim do bloco for

    // Inicio do bloco if/else  
    // Se o proximo destino tiver sido definido anteriormente
		if (proximoDestino != null) {
			// Alteramos os parametros (destino e linha de saida de origem) e encaminhamos
			// o pacote para o proximo destino
			this.vindoDe = destino;
			this.destino = proximoDestino;
			movimentar(destino);
		}
		else {
			// Caso contrario, o pacote eh "morto" na interface
			TelaPrincipalController.controller.removerPacote(this);
		} // Fim do bloco if/else
	}

  /*
   * ***************************************************************
   * Metodo: decrementarTempoDeVida
   * Funcao: decrementa o tempo de vida do pacote na sub rede (exclusivo para a V3
             do algoritmo de inundacao)
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	private void decrementarTempoDeVida() {
		tempoDeVida--;
	}

  /*
   * ***************************************************************
   * Metodo: adicionarRoteadorVisitado
   * Funcao: adiciona um roteador dentro da lista de roteadores ja visitados
   * Parametros: Roteador r - roteador a ser adicionado
   * Retorno: void
   ****************************************************************/

	private void adicionarRoteadorVisitado(Roteador r) {
		roteadoresVisitados.add(r);
	}

  /*
   * ***************************************************************
   * Metodo: visitado
   * Funcao: verifica se um roteador ja foi visitado anteriormente
   * Parametros: Roteador r - roteador a ser verificado
   * Retorno: boolean
   ****************************************************************/

	private boolean visitado(Roteador r) {
		// Inicio do bloco for
		// Percorre-se a lista de roteadores visitados para verificar se o roteador
		// passado como parametro se encontra na lista
		for (Roteador v : roteadoresVisitados) {
			// Inicio do bloco if
			if (v.getNome().equals(r.getNome())) {
				// Retorna verdadeiro caso o roteador se encontrar na lista
				// conforme o seu rotulo
				return true;
			} // Fim do bloco if
		} // Fim do bloco for

    // Retorna falso caso o roteador nao tiver sido encontrado na lista
		return false;
	}

  /*
   * ***************************************************************
   * Metodo: setEnvelope
   * Funcao: define um novo valor para a imagem do pacote
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
}
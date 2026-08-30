/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 30/08/2026
* Ultima alteracao.: 30/08/2026
* Nome.............: Grafo
* Funcao...........: Classe que controla as operacoes do grafo da sub-rede.
                     
*************************************************************** */

package model;

import controller.TelaPrincipalController;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;

public class Grafo {
	private int quantidadeNos;
	private int quantidadePacotes;
	private AnchorPane painel;
	private Label lblPacotes;
	private ArrayList<Roteador> roteadores;
	private ArrayList<String> arestas;
	private HashMap<String, Label> labels;
	private CopyOnWriteArrayList<Pacote> pacotes;
	private TelaPrincipalController controller;

	public Grafo(AnchorPane painel, Label lblPacotes, TelaPrincipalController controller) {
		this.painel = painel;
		this.lblPacotes = lblPacotes;
		this.controller = controller;

		roteadores = new ArrayList<>();
		arestas = new ArrayList<>();
		labels = new HashMap<>();
		pacotes = new CopyOnWriteArrayList<>();

		quantidadeNos = 0;
		quantidadePacotes = 0;

		this.montarGrafo();
	}

	private void adicionarElemento() {

	}

	private void removerElemento() {

	}

	public void adicionarPacote(Pacote p) {
		ImageView envelope = p.getEnvelope();
		painel.getChildren().add(envelope);

		pacotes.add(p);
		definirValorPacotes(quantidadePacotes++);
		jogarRoteadoresParaFrente();

		// Garante que a Thread seja interrompida caso a janela for fechada
    p.setDaemon(true);

    // Inicia a Thread
    p.start();
	}

	public void removerPacote(Pacote p) {
		// Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      // Obtem e remove a imagem do pacote da interface
      ImageView envelope = p.getEnvelope();
      painel.getChildren().remove(envelope);

      // Remove o pacote da lista de pacotes e decrementa 
      // a quantidade de pacotes existentes na rede
      pacotes.remove(p);
      definirValorPacotes(quantidadePacotes--);
    });
	}

	private void definirValorPacotes(int valor) {
		lblPacotes.setText(Integer.toString(valor));
	}

	public void removerPacotes() {
    // Inicio do bloco for
    for (Pacote p : pacotes) {
      // Interrompe a Thread de cada pacote ainda presente na rede
      p.interrupt();
    } // Fim do bloco for

    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
      for (Pacote p : pacotes) {
      	ImageView envelope = p.getEnvelope();
      	painel.getChildren().remove(envelope);
      }

      pacotes.clear();
    });
	}

	public void reiniciarGrafo() {
		quantidadePacotes = 0;
		definirValorPacotes(quantidadePacotes);
		
		bloquearRoteadores(false);
		redefinirRoteadores();
	}

	private void montarGrafo() {
		try (BufferedReader br = new BufferedReader(new FileReader("backbone.txt"))) {
			String linha = br.readLine();
			if (linha == null) return;
			this.quantidadeNos = Integer.parseInt(linha.trim());

			criarRoteadores(quantidadeNos);

			while ((linha = br.readLine()) != null) {
				String[] partes = linha.split(",");
				if (partes.length != 3) continue; 

				String nome1 = partes[0];
				String nome2 = partes[1];

				Roteador r1 = obterRoteador(nome1);
				Roteador r2 = obterRoteador(nome2);

				if (r1 != null && r2 != null) criarAresta(r1, r2);
			}

			jogarRoteadoresParaFrente();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void criarRoteadores(int nos) {
		for (int i = 0; i < nos; i++) {
			String nome = gerarNome(i);
			Circle no = criarNo(nome, i);
			Roteador r = new Roteador(no, nome);

			double[] posicoes = calcularPosicaoRoteador(r);
			r.definirPosicao(posicoes[0], posicoes[1]);

			roteadores.add(r);
			criarLabel(nome, no);
		}
	}

	private void criarAresta(Roteador r1, Roteador r2) {
		String id = (r1.getNome().compareTo(r2.getNome()) < 0) ? r1.getNome() + r2.getNome() : r2.getNome() + r1.getNome();

		if (!arestas.contains(id)) {
			// A linha e desenhada entre os nos de cada roteador
      Line linha = new Line(r1.getNo().getCenterX(), r1.getNo().getCenterY(), r2.getNo().getCenterX(), r2.getNo().getCenterY());
      linha.setStroke(Color.WHITE);
      linha.setStrokeWidth(1.0);

      // Adiciona a id da aresta
      arestas.add(id);

      // Adiciona a linha na tela da sub rede
      painel.getChildren().add(linha);

      // Os roteadores sao marcados como vizinhos um do outro
      r1.adicionarVizinho(r2);
      r2.adicionarVizinho(r1);
		}
	}

	private void criarLabel(String nome, Circle no) {
		Label l = new Label(nome);
		l.setTextFill(Color.web("#2d4180"));
    l.setFont(Font.font("VCR OSD Mono", 15));

    double[] posicoes = calcularPosicaoLabel(l.getBoundsInLocal().getWidth(), l.getBoundsInLocal().getHeight(), 
    																				no.getCenterX(), no.getCenterY());

    l.setLayoutX(posicoes[0]);
    l.setLayoutY(posicoes[1]);

    painel.getChildren().add(l);
    labels.putIfAbsent(nome, l);
	}

	private String gerarNome(int i) {
		return String.valueOf((char) ('A' + i));
	}

	private Circle criarNo(String nome, int i) {
		double[] posicoes = calcularPosicaoNo(i); 

		double x = posicoes[0];
		double y = posicoes[1];

		Circle c = new Circle(x, y, 15, Color.WHITE);
		c.setStroke(Color.BLACK);
		c.setStrokeWidth(2);
		c.setStrokeType(StrokeType.OUTSIDE);
		c.setCursor(Cursor.HAND);

		c.setOnMouseClicked(event -> {
			controller.definirOrigemDestino(event, obterRoteador(nome));
		});

		painel.getChildren().add(c);

		return c;
	}

	private double[] calcularPosicaoNo(int i) {
		double centroX = painel.getPrefWidth() / 2;
		double centroY = painel.getPrefHeight() / 2;
		double raio = Math.min(centroX, centroY) - 60;

		double angulo = (2 * Math.PI * i) / quantidadeNos;
		double x = centroX + raio * Math.cos(angulo);
		double y = centroY + raio * Math.sin(angulo);

		return new double[]{x, y};
	}

	private double[] calcularPosicaoRoteador(Roteador r) {
		double larguraPacote = 41.0;
		double alturaPacote = 98.0;

		Circle c = r.getNo();
		if (c == null) return null;

		double x = c.getCenterX() - (larguraPacote / 2) + 5.0;
		double y = c.getCenterY() - (alturaPacote / 2) + 30.0;

		return new double[]{x, y};
	}

	private double[] calcularPosicaoLabel(double largura, double altura, double centroX, double centroY) {
		double x = centroX - (largura / 2.0) - 3.0;
    double y = centroY - (altura / 2.0) - 5.0;

		return new double[]{x, y};
	}

	private Roteador obterRoteador(String nome) {
		for (Roteador r : roteadores) {
			if (r.getNome().equals(nome)) {
				return r;
			}
		}

		return null;
	}

	private void jogarRoteadoresParaFrente() {
		for (Roteador r : roteadores) {
			Circle c = r.getNo();
			String nome = r.getNome();
			Label l = labels.get(nome);

			c.toFront();
			l.toFront();
		}
	}

	/*
   * ***************************************************************
   * Metodo: existeOrigem
   * Funcao: verifica se um roteador ja foi marcado como origem no percurso
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  public boolean existeOrigem() {
    // Inicio do bloco for
    // Realiza-se uma busca dentro da lista de roteadores
    // existentes na topologia
    for (Roteador r : roteadores) {
      // Inicio do bloco if
      if (r.isOrigem()) {
        // Retorna verdadeiro se o roteador tiver sido
        // definido como a origem (ponto inicial) da rota
        return true;
      } // Fim do bloco if
    } // Fim do bloco for

    // Retorna falso caso nenhum ponto de origem
    // tiver sido definido para a rota
    return false;
  }

  /*
   * ***************************************************************
   * Metodo: existeDestino
   * Funcao: verifica se um roteador ja foi marcado como destino no percurso
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

  public boolean existeDestino() {
    // Inicio do bloco for
    // Realiza-se uma busca dentro da lista de roteadores
    // existentes na topologia
    for (Roteador r : roteadores) {
      // Inicio do bloco if
      if (r.isDestino()) {
        // Retorna verdadeiro se algum roteador ja tiver sido
        // definido como a origem
        return true;
      } // Fim do bloco if
    } // Fim do bloco for

    // Retorna falso caso o destino nao tiver sido
    // definido anteriormente
    return false;
  }

  public void bloquearRoteadores(boolean bloqueio) {
  	for (Roteador r : roteadores) {
  		r.bloquearRoteador(bloqueio);
  	}
  }

  public void redefinirRoteadores() {
  	for (Roteador r : roteadores) {
  		r.redefinirCorRoteador();
  		if (r.isOrigem()) r.setOrigem(false);
  		if (r.isDestino()) r.setDestino(false);
  	}
  }
}
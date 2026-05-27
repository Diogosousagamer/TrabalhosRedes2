/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 02/05/2026
* Ultima alteracao.: 27/05/2026
* Nome.............: TabelaRoteamento
* Funcao...........: Classe que gerencia as operacoes de cada tabela de roteamento.
                     
*************************************************************** */

package model;

import controller.TelaPrincipalController;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class TabelaRoteamento {
	// Variaveis e instancias
	private Roteador r;
	private TableView<EntradaTabela> tabela;
	private ArrayList<EntradaTabela> entradas;
  private final long INFINITO = 30;

  /*
   * ***************************************************************
   * Metodo: TabelaRoteamento
   * Funcao: inicializa uma nova instancia da classe TabelaRoteamento
   * Parametros: Roteador r - roteador da tabela
                 TableView<EntradaTabela> tabela - tabela na interface
   * Retorno: nenhum
   ****************************************************************/

	public TabelaRoteamento(Roteador r, TableView<EntradaTabela> tabela) {
		this.r = r;
		this.tabela = tabela;
		entradas = new ArrayList<>();
	}

  /*
   * ***************************************************************
   * Metodo: definirEntradasIniciais
   * Funcao: carrega as entradas iniciais dentro da tabela
   * Parametros: CopyOnWriteArrayList<Roteador> roteadores - lista de roteadores
   * Retorno: void
   ****************************************************************/

	public void definirEntradasIniciais(CopyOnWriteArrayList<Roteador> roteadores) {
		// Inicio do bloco for
		for (Roteador rot : roteadores) {
			// Insere a entrada correspondente ao roteador
      inserirEntrada(new EntradaTabela(rot, rot.getNome(), "-", "-"));
    } // Fim do bloco for

    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
    	// Atualiza o roteador na topologia e nos seus vizinhos
    	TelaPrincipalController.controller.atualizarRoteador(r);
    	TelaPrincipalController.controller.alterarRoteadorNosVizinhos(r);
    }); // Fim do bloco Platform.runLater
	}

  /*
   * ***************************************************************
   * Metodo: preencherTabela
   * Funcao: preenche a tabela com base nos calculos de Dijkstra
   * Parametros: long[] distancia - conjunto de distancias possiveis para cada destino
                 int[] predecessor - conjunto de predecessores para cada destino
                 int raiz - indice correspondente ao roteador da tabela
                 CopyOnWriteArrayList<Roteador> listaRoteadores - lista de roteadores
                 presentes na sub rede
   * Retorno: void
   ****************************************************************/

  public void preencherTabela(long[] distancia, int[] predecessor, int raiz, CopyOnWriteArrayList<Roteador> listaRoteadores) {
    // Inicio do bloco try/catch
    try {
      // O tamanho da distancia corresponde a quantidade de roteadores presentes na sub rede
      int n = distancia.length;

      // Inicio do bloco for
      // Percorre todas as linhas de destino possiveis para o roteador
      for (int idxDestino = 0; idxDestino < n; idxDestino++) {
        // Pula se o destino corresponder ao indice do roteador da tabela
        if (idxDestino == raiz) continue;

        // Pula se a distancia marcada tiver um custo infinito
        if (distancia[idxDestino] == 100000) continue;

        // O destino passa a ser o indice atual e o proximo hop
        int atual = idxDestino;
        int proximoSalto = idxDestino;

        // Inicio do bloco while
        // Enquanto o predecessor for diferente da raiz (roteador da tabela) e tiver sido definido (diferente de -1)
        while (predecessor[atual] != raiz && predecessor[atual] != -1) {
          // Atualiza o proximo hop para o predecessor do roteador atual
          proximoSalto = predecessor[atual];

          // O predecessor obtido passa a ser o roteador atual
          atual = predecessor[atual];
        } // Fim do bloco while

        // Obtem o nome do roteador da linha de destino e da linha de saida obtida 
        String nomeDestino = gerarNome(idxDestino);
        String nomeHop = gerarNome(proximoSalto);

        // Obtem o custo estimado ate o destino atual e a instancia do roteador de destino
        long custo = distancia[idxDestino];
        Roteador rotDestino = obterRoteadorDestino(nomeDestino, listaRoteadores);

        // Cria uma nova entrada com os dados obtidos
        final EntradaTabela entradaFinal = new EntradaTabela(rotDestino, nomeDestino, nomeHop, Long.toString(custo));

        // Altera a entrada correspondente na tabela e aguarda meio segundo antes de fazer uma nova insercao
        Platform.runLater(() -> alterarEntrada(entradaFinal));
        dormir(500);
      } // Fim do bloco for
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } // Fim do bloco try/catch
  }

  /*
   * ***************************************************************
   * Metodo: inserirEntrada
   * Funcao: insere uma nova entrada dentro da tabela
   * Parametros: EntradaTabela e - entrada a ser inserida
   * Retorno: void
   ****************************************************************/

	private void inserirEntrada(EntradaTabela e) {
		// Insere a entrada na lista de entrada e atualiza a tabela
		entradas.add(e);
		atualizarTabela();
	}

  /*
   * ***************************************************************
   * Metodo: atualizarTabela
   * Funcao: atualiza a tabela com os dados modificados
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void atualizarTabela() {
		// Inicio do bloco Platform.runLater
		Platform.runLater(() -> {
			// Converte a lista de entradas em uma lista observavel para que ela possa ser inserida na tabela
			ObservableList<EntradaTabela> dados = FXCollections.observableArrayList(entradas);
			tabela.setItems(dados);
			tabela.refresh();
		}); // Fim do bloco Platform.runLater
	}

	/*
   * ***************************************************************
   * Metodo: alterarEntrada
   * Funcao: altera uma determinada entrada na tabela de roteamento
   * Parametros: EntradaTabela modificada - entrada com os dados modificados
   * Retorno: void
   ****************************************************************/

	public void alterarEntrada(EntradaTabela modificada) {
		// Inicio do bloco for
		for (int i = 0; i < entradas.size(); i++) {
      // Obtem a entrada atual
			EntradaTabela e = entradas.get(i);

      // Inicio do bloco if
			if (modificada.getDestino().equals(e.getDestino())) {
        // Realiza a substituicao na lista e interrompe o laco caso a linha de destino
        // da entrada a ser alterada corresponde a da entrada atual
				entradas.set(i, modificada);
				break;
			} // Fim do bloco if
		} // Fim do bloco for

    // Atualiza a tabela
		atualizarTabela();
	}

  /*
   * ***************************************************************
   * Metodo: obterEntrada
   * Funcao: obtem uma determinada entrada da tabela de roteamento
   * Parametros: String destino - linha de destino correspondente a entrada a ser obtida
   * Retorno: void
   ****************************************************************/

	public EntradaTabela obterEntrada(String destino) {
		// Inicio do bloco for
		for (EntradaTabela e : entradas) {
      // Obtem o destino da entrada atual
			String destinoAtual = e.getDestino().trim();

			// Inicio do bloco if
			if (destinoAtual.equals(destino)) {
				// Retorna a entrada caso ela possuir a linha de destino procurada
				return e;
			} // Fim do bloco if
		} // Fim do bloco for

    // Retorna nulo caso a entrada buscada nao for encontrada
		return null;
	}

  /*
   * ***************************************************************
   * Metodo: redefinirEntradas
   * Funcao: reinicia as entradas da tabela de roteamento
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void redefinirEntradas() {
		// Esvazia a lista e atualiza a tabela
		entradas.clear();
		atualizarTabela();
	}

	/*
   * ***************************************************************
   * Metodo: dormir
   * Funcao: coloca o processo para dormir por alguns milissegundos
   * Parametros: long valor - tempo de sono do processo em milissegundos
   * Retorno: void
   ****************************************************************/

  private void dormir(long valor) throws InterruptedException {
    // A tabela eh posta para dormir por alguns ms
    Thread.sleep(valor);
  }

  /*
   * ***************************************************************
   * Metodo: gerarNome
   * Funcao: obtem o nome do roteador a partir do indice fornecido
   * Parametros: int i - indice do roteador
   * Retorno: String
   ****************************************************************/

  private String gerarNome(int i) {
    return String.valueOf((char) ('A' + i));
  }

  /*
   * ***************************************************************
   * Metodo: obterRoteadorDestino
   * Funcao: obtem o roteador da linha de destino fornecida
   * Parametros: String nome - nome do roteador a ser obtido
                 CopyOnWriteArrayList<Roteador> listaRoteadores - lista de roteadores
                 presentes na sub rede
   * Retorno: Roteador
   ****************************************************************/

  private Roteador obterRoteadorDestino(String nome, CopyOnWriteArrayList<Roteador> listaRoteadores) {
    // Inicio do bloco for
    for (Roteador r : listaRoteadores) {
      // Retorna o roteador atual se o nome dele corresponder ao nome buscado
      if (r.getNome().equals(nome)) return r;
    } // Fim do bloco for

    // Retorna nulo caso nenhuma correspondencia for encontrada
    return null;
  }

  /*
   * ***************************************************************
   * Metodo: setRoteador
   * Funcao: define o roteador da tabela de roteamento
   * Parametros: Roteador r - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setRoteador(Roteador r) {
		this.r = r;
	}

  /*
   * ***************************************************************
   * Metodo: getRoteador
   * Funcao: retorna o roteador da tabela de roteamento
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Roteador
   ****************************************************************/

	public Roteador getRoteador() {
		return r;
	}

  /*
   * ***************************************************************
   * Metodo: setTabela
   * Funcao: define a tabela na interface
   * Parametros: TableView<EntradaTabela> tabela - tabela a ser definida
   * Retorno: void
   ****************************************************************/

	public void setTabela(TableView<EntradaTabela> tabela) {
		this.tabela = tabela;
	}

  /*
   * ***************************************************************
   * Metodo: getTabela
   * Funcao: retorna a tabela na interface
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: TableView<EntradaTabela>
   ****************************************************************/

	public TableView<EntradaTabela> getTabela() {
		return tabela;
	}

  /*
   * ***************************************************************
   * Metodo: setEntradas
   * Funcao: define o conjunto de entradas da tabela de roteamento
   * Parametros: ArrayList<EntradaTabela> entradas - entradas a serem definidas
   * Retorno: void
   ****************************************************************/

	public void setEntradas(ArrayList<EntradaTabela> entradas) {
		this.entradas = entradas;
	}

  /*
   * ***************************************************************
   * Metodo: getEntradas
   * Funcao: retorna o conjunto de entradas da tabela de roteamento
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: ArrayList<EntradaTabela>
   ****************************************************************/

	public ArrayList<EntradaTabela> getEntradas() {
		return entradas;
	}
}
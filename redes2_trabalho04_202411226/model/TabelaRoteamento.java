/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 02/05/2026
* Ultima alteracao.: 26/05/2026
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

  public void preencherTabela(long[] distancia, int[] predecessor, int raiz, CopyOnWriteArrayList<Roteador> listaRoteadores) {
    int n = distancia.length;

    for (int idxDestino = 0; idxDestino < n; idxDestino++) {
      if (idxDestino == raiz) continue;
      if (distancia[idxDestino] == 100000) continue;

      int atual = idxDestino;
      int proximoSalto = idxDestino;

      while (predecessor[atual] != raiz && predecessor[atual] != -1) {
        proximoSalto = predecessor[atual];
        atual = predecessor[atual];
      }

      String nomeDestino = gerarNome(idxDestino);
      String nomeHop = gerarNome(proximoSalto);
      long custo = distancia[idxDestino];
      Roteador rotDestino = obterRoteadorDestino(nomeDestino, listaRoteadores);

      alterarEntrada(new EntradaTabela(rotDestino, nomeDestino, nomeHop, Long.toString(custo)));
      dormir(500);
    }
  }

  private String gerarNome(int i) {
    return String.valueOf((char) ('A' + i));
  }

  private Roteador obterRoteadorDestino(String nome, CopyOnWriteArrayList<Roteador> listaRoteadores) {
    for (Roteador r : listaRoteadores) {
      if (r.getNome().equals(nome)) return r;
    }

    return null;
  }

  /*
   * ***************************************************************
   * Metodo: ps1
   * Funcao: retorna o retardo de um caminho entre o host e o destino almejado
   * Parametros: Roteador destino - roteador de destino
   * Retorno: long
   ****************************************************************/

  public long ps1(Roteador destino) {
    // Distancia a ser obtida
    long distancia = 0;

    // Inicio do bloco try/catch
    // O roteador lera o arquivo de backbone para encontrar o retardo
    try (BufferedReader br = new BufferedReader(new FileReader("backbone.txt"))) {
      // String que sera responsavel por ler cada linha do arquivo
      String linha = "";

      // Inicio do bloco while
      // Enquanto ainda tiver texto presente no arquivo
      while ((linha = br.readLine()) != null) {
        // Divide a linha em partes e as armazena em um vetor
        String[] partes = linha.split(",");
 
        // Pula para ler outra linha caso a quantidade de partes
        // nao for a almejada
        if (partes.length < 3) continue;

        // Obtem os nomes dos roteadores
        String nome1 = partes[0];
        String nome2 = partes[1];

        // Verifica
        boolean verificacao = nome1.equals(r.getNome()) && nome2.equals(destino.getNome()) 
                              || nome1.equals(destino.getNome()) && nome2.equals(r.getNome());

        // Inicio do bloco if/else if
        if (verificacao) {
          // Retorna a latencia e interrompe o laco
          distancia = Long.parseLong(partes[2]);
          break;
        }
      } // Fim do bloco while
    }
    catch (IOException e) {
      // Em caso de excecao, ela sera exibida no terminal
      e.printStackTrace();
    } // Fim do bloco try/catch

    // Retorna a distancia
    return distancia;
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

  private void dormir(long valor) {
    // Inicio do bloco try/catch
    try {
      // A tabela eh posta para dormir por alguns ms
      Thread.sleep(valor);
    }
    catch (InterruptedException e) {
      // Em caso de excecao, a Thread e interrompida
      Thread.currentThread().interrupt();
    } // Fim do bloco try/catch
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
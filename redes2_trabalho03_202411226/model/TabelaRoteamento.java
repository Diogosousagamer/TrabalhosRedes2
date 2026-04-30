/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 18/04/2026
* Ultima alteracao.: 30/04/2026
* Nome.............: TabelaRoteamento
* Funcao...........: Classe que gerencia as operacoes de cada tabela de roteamento.
                     
*************************************************************** */

package model;

import controller.TelaPrincipalController;
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
    }

    // Obtem a lista de vizinhos do roteador da tabela
    CopyOnWriteArrayList<Roteador> vizinhos = new CopyOnWriteArrayList<>(r.getVizinhos());

    // Inicio do bloco if
    if (!vizinhos.isEmpty()) {
    	// Inicio do bloco for
      for (Roteador v : vizinhos) {
        final long distancia = r.ping(v);
        final String vizinho = v.getNome();
      	Platform.runLater(() -> r.modificarEntrada(v, vizinho, vizinho, distancia));
      } // Fim do bloco for
    } // Fim do bloco if

    // Inicio do bloco Platform.runLater
    Platform.runLater(() -> {
    	// Atualiza o roteador na topologia e nos seus vizinhos
    	TelaPrincipalController.controller.atualizarRoteador(r);
    	TelaPrincipalController.controller.alterarRoteadorNosVizinhos(r);
    }); // Fim do bloco Platform.runLater
	}

  /*
   * ***************************************************************
   * Metodo: processarVetor
   * Funcao: modifica a tabela com base na tabela do vizinho
   * Parametros: Roteador emissor - vizinho que forneceu as entradas da tabela
                 ArrayList<EntradaTabela> entradasEmissor - entradas da tabela do emissor
   * Retorno: void
   ****************************************************************/

	public void processarVetor(Roteador emissor, ArrayList<EntradaTabela> entradasEmissor) {
		long custoParaVizinho = r.ping(emissor);

		for (EntradaTabela e : entradasEmissor) {
			String destino = e.getDestino().trim();

			if (destino.equals(this.r.getNome())) continue;

      String retardoEmissor = e.getRetardo().trim();
      if (retardoEmissor.equals("-")) continue;

      long custoEntrada = Long.parseLong(retardoEmissor);
			long custoViaVizinho = custoParaVizinho + custoEntrada;

			EntradaTabela entradaLocal = this.obterEntrada(destino);

			if (entradaLocal != null) {
				String retardoLocal = entradaLocal.getRetardo().trim();

				long distanciaLocal = (retardoLocal.equals("-")) ? Long.MAX_VALUE : Long.parseLong(retardoLocal);
				boolean viaMesmoVizinho = entradaLocal.getLinhaSaida().equals(emissor.getNome());
				Roteador entrada = entradaLocal.getRoteadorDestino();

				if ((entrada != null) && (custoViaVizinho < distanciaLocal || viaMesmoVizinho)) {
					if (distanciaLocal != custoViaVizinho || !viaMesmoVizinho) {
						entradaLocal.setRetardo(Long.toString(custoViaVizinho));
						entradaLocal.setLinhaSaida(emissor.getNome());
						alterarEntrada(entradaLocal);

						Platform.runLater(() -> {
							TelaPrincipalController.controller.atualizarRoteador(entrada);
							TelaPrincipalController.controller.alterarRoteadorNosVizinhos(entrada);
							this.atualizarTabela();
						});
					}
				}
			}
		}
	}

  /*
   * ***************************************************************
   * Metodo: inserirEntrada
   * Funcao: insere uma nova entrada dentro da tabela
   * Parametros: EntradaTabela e - entrada a ser inserida
   * Retorno: void
   ****************************************************************/

	public void inserirEntrada(EntradaTabela e) {
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
			EntradaTabela e = entradas.get(i);

			if (modificada.getDestino().equals(e.getDestino())) {
				entradas.set(i, modificada);
				break;
			}
		}

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
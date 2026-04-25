/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 18/04/2026
* Ultima alteracao.: 24/04/2026
* Nome.............: TabelaRoteamento
* Funcao...........: Classe que gerencia as operacoes de cada tabela de roteamento.
                     
*************************************************************** */

package model;

import controller.TelaPrincipalController;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class TabelaRoteamento {
	// Variaveis e instancias
	private String nome;
	private TableView<EntradaTabela> tabela;
	private ArrayList<EntradaTabela> entradas;

  /*
   * ***************************************************************
   * Metodo: TabelaRoteamento
   * Funcao: inicializa uma nova instancia da classe TabelaRoteamento
   * Parametros: String nome - rotulo do roteador da tabela
                 TableView<EntradaTabela> tabela - tabela na interface
                 ArrayList<EntradaTabela> entradas - conjunto de entradas da tabela
   * Retorno: nenhum
   ****************************************************************/

	public TabelaRoteamento(String nome, TableView<EntradaTabela> tabela) {
		this.nome = nome;
		this.tabela = tabela;
		entradas = new ArrayList<>();
	}

	public boolean processarVetor(Roteador receptor, Roteador emissor, ArrayList<EntradaTabela> entradasEmissor) {
		boolean mudou = false;
		long custoParaVizinho = receptor.ping(emissor);

		for (EntradaTabela e : entradasEmissor) {
			String destino = e.getDestino();

			if (destino.equals(this.getNome())) continue;

      String retardoEmissor = e.getRetardo().trim();

      long custoEntrada = (retardoEmissor.equals("-")) ? 0 : Long.parseLong(retardoEmissor);
			long custoViaVizinho = custoParaVizinho + custoEntrada;

			EntradaTabela entradaLocal = this.obterEntrada(destino);

			if (entradaLocal != null) {
				String retardoLocal = entradaLocal.getRetardo().trim();

				long distanciaLocal = (retardoLocal.equals("-")) ? Integer.MAX_VALUE : Long.parseLong(retardoLocal);
				boolean viaMesmoVizinho = entradaLocal.getLinhaSaida().equals(emissor.getNome());

				if (custoViaVizinho < distanciaLocal || viaMesmoVizinho) {
					if (distanciaLocal != custoViaVizinho || !viaMesmoVizinho) {
						entradaLocal.setRetardo(Long.toString(custoViaVizinho));
						entradaLocal.setLinhaSaida(emissor.getNome());
						alterarEntrada(entradaLocal);

						Roteador entrada = entradaLocal.getRoteadorDestino();
						Aresta a = TelaPrincipalController.controller.obterAresta(emissor, entrada);

            if (entrada != null && a != null) {
            	entrada.setDistancia(custoViaVizinho);
							entrada.setAntecessor(emissor);

							Platform.runLater(() -> {
								TelaPrincipalController.controller.alterarDistancia(entrada);
								TelaPrincipalController.controller.atualizarRoteador(entrada);
								TelaPrincipalController.controller.alterarRoteadorNosVizinhos(entrada);
							});
            }

						mudou = true;
					}
				}
			}
		}

		return mudou;
	}

	public void inserirEntrada(EntradaTabela e) {
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
		Platform.runLater(() -> {
			// Converte a lista de entradas em uma lista observavel para que ela possa ser inserida na tabela
			ObservableList<EntradaTabela> dados = FXCollections.observableArrayList(entradas);
			tabela.setItems(dados);
		});
	}

	/*
   * ***************************************************************
   * Metodo: alterarEntrada
   * Funcao: altera uma determinada entrada na tabela de roteamento
   * Parametros: EntradaTabela modificada - entrada com os dados modificados
   * Retorno: void
   ****************************************************************/

	public void alterarEntrada(EntradaTabela modificada) {
		for (int i = 0; i < entradas.size(); i++) {
			EntradaTabela e = entradas.get(i);

			if (modificada.getDestino().equals(e.getDestino())) {
				entradas.set(i, modificada);
				break;
			}
		}

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
			if (e.getDestino().equals(destino)) {
				return e;
			}
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
		entradas.clear();
		atualizarTabela();
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setTabela(TableView<EntradaTabela> tabela) {
		this.tabela = tabela;
	}

	public TableView<EntradaTabela> getTabela() {
		return tabela;
	}

	public void setEntradas(ArrayList<EntradaTabela> entradas) {
		this.entradas = entradas;
	}

	public ArrayList<EntradaTabela> getEntradas() {
		return entradas;
	}
}
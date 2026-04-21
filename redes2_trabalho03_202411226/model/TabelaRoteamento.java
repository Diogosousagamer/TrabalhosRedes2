/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 18/04/2026
* Ultima alteracao.: 21/04/2026
* Nome.............: TabelaRoteamento
* Funcao...........: Classe que gerencia as operacoes de cada tabela de roteamento.
                     
*************************************************************** */

package model;

import java.util.ArrayList;
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

	public TabelaRoteamento(String nome, TableView<EntradaTabela> tabela, ArrayList<EntradaTabela> entradas) {
		this.nome = nome;
		this.tabela = tabela;
		this.entradas = entradas;
	}

  /*
   * ***************************************************************
   * Metodo: atualizarTabela
   * Funcao: atualiza a tabela com os dados modificados
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void atualizarTabela() {
		// Converte a lista de entradas em uma lista observavel para que ela possa ser inserida na tabela
		ObservableList<EntradaTabela> dados = FXCollections.observableArrayList(entradas);
		tabela.setItems(dados);
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
		for (EntradaTabela e : entradas) {
			e.setLinhaSaida("-");
			e.setRetardo("-");
		}

		atualizarTabela();
	}
}
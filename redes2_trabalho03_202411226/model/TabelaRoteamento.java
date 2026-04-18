/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 18/04/2026
* Ultima alteracao.: 18/04/2026
* Nome.............: TabelaRoteamento
* Funcao...........: Classe que gerencia as operacoes de cada tabela de roteamento.
                     
*************************************************************** */

package model;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class TabelaRoteamento {
	private String nome;
	private TableView<EntradaTabela> tabela;
	private ArrayList<EntradaTabela> entradas;

	public TabelaRoteamento(String nome, TableView<EntradaTabela> tabela, ArrayList<EntradaTabela> entradas) {
		this.nome = nome;
		this.tabela = tabela;
		this.entradas = entradas;
	}

	public void atualizarTabela() {
		ObservableList<EntradaTabela> dados = FXCollections.observableArrayList(entradas);
		tabela.setItems(dados);
	}

	private void alterarEntrada(EntradaTabela modificada) {
		for (int i = 0; i < entradas.size(); i++) {
			EntradaTabela e = entradas.get(i);

			if (modificada.getDestino().equals(e.getDestino())) {
				entradas.set(i, modificada);
				break;
			}
		}

		atualizarTabela();
	}

	public void redefinirEntradas() {
		for (EntradaTabela e : entradas) {
			e.setLinhaSaida("-");
			e.setRetardo(0);
		}

		atualizarTabela();
	}
}
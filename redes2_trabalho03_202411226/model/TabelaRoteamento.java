/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 18/04/2026
* Ultima alteracao.: 19/04/2026
* Nome.............: TabelaRoteamento
* Funcao...........: Classe que gerencia as operacoes de cada tabela de roteamento.
                     
*************************************************************** */

package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

	public void ping(Roteador r1, Roteador r2) {
		long distancia;

		try (BufferedReader br = new BufferedReader(new FileReader("backbone.txt"))) {
			String linha = "";

			while ((linha = br.readLine()) != null) {
				String[] partes = linha.split(",");

				if (partes.length < 4) continue;

				String nome1 = partes[0];
				String nome2 = partes[1];
				long tempoIda = Long.parseLong(partes[2]);
				long tempoVolta = Long.parseLong(partes[3]);

				if (nome1.equals(r1.getNome()) && nome2.equals(r2.getNome())) {
					distancia = tempoIda;
				}
				else if (nome1.equals(r2.getNome()) && nome2.equals(r1.getNome())) {
					distancia = tempoVolta;
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

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

	public void redefinirEntradas() {
		for (EntradaTabela e : entradas) {
			e.setLinhaSaida("-");
			e.setRetardo(0);
		}

		atualizarTabela();
	}
}
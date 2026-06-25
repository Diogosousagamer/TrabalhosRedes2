/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 24/06/2026
* Nome.............: Grupo
* Funcao...........: Classe que controla as operacoes dos grupos do aplicativo.
                     
*************************************************************** */

package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.scene.image.Image;

public class Grupo {
	// Variaveis e instancias
	private Image perfilGrupo;
	private String nome;
	private Mensagem ultimaMensagem;
	private ArrayList<Mensagem> mensagens;
	private boolean selected;

	public Grupo(Image perfilGrupo, String nome) {
		this.perfilGrupo = perfilGrupo;
		this.nome = nome;
		mensagens = new ArrayList<>();
		selected = false;
	}

	public synchronized Mensagem obterUltimaMensagem() { 
		if (mensagens == null || mensagens.isEmpty()) return null;
		atualizarUltimaMensagem();
		return ultimaMensagem;
	}

	public synchronized void atualizarUltimaMensagem() {
		if (mensagens == null || mensagens.isEmpty()) return;

		for (Mensagem m : mensagens) {
			if (ultimaMensagem == null || m.getTempoEnvio().isAfter(ultimaMensagem.getTempoEnvio())) {
				ultimaMensagem = m;
			}
		}
	}

	public synchronized void adicionarMensagem(Mensagem m) {
		mensagens.add(m);
		mensagens.sort(Comparator.comparing(Mensagem::getTempoEnvio));
	}

	public void setPerfilGrupo(Image perfilGrupo) {
		this.perfilGrupo = perfilGrupo;
	}

	public Image getPerfilGrupo() {
		return perfilGrupo;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

  public ArrayList<Mensagem> getMensagens() {
    return mensagens;
  }

	public void setSelected(boolean s) {
		this.selected = s;
	}

	public boolean isSelected() {
		return selected;
	}
}
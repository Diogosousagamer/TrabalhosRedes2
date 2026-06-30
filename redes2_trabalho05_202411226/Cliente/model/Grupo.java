/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 30/06/2026
* Nome.............: Grupo
* Funcao...........: Classe que controla as operacoes dos grupos do aplicativo.
                     
*************************************************************** */

package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import javafx.scene.image.Image;

public class Grupo {
	// Variaveis e instancias
	private String nome;
	private Mensagem ultimaMensagem;
	private ArrayList<Mensagem> mensagens;
	private boolean selected;
  private static final Image DELIVERED = new Image(Grupo.class.getResource("/img/Delivered.png").toExternalForm());
  private static final Image READ = new Image(Grupo.class.getResource("/img/Read.png").toExternalForm());

  /*
   * ***************************************************************
   * Metodo: Grupo
   * Funcao: inicializa uma nova instancia da classe Grupo
   * Parametros: String nome - nome do grupo
   * Retorno: nenhum
   ****************************************************************/

	public Grupo(String nome) {
		this.nome = nome;
		mensagens = new ArrayList<>();
		selected = false;
	}

  /*
   * ***************************************************************
   * Metodo: obterUltimaMensagem
   * Funcao: retorna a ultima mensagem enviada no grupo
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Mensagem
   ****************************************************************/

	public synchronized Mensagem obterUltimaMensagem() { 
		// Retorna nulo caso a lista de mensagens nao tiver sido inicializada
		if (mensagens == null || mensagens.isEmpty()) return null;

		// Atualiza a lista para obter a ultima mensagem
		atualizarUltimaMensagem();

		// Retorna a ultima mensagem
		return ultimaMensagem;
	}

  /*
   * ***************************************************************
   * Metodo: atualizarUltimaMensagem
   * Funcao: obtem a ultima mensagem apos a insercao de uma nova mensagem
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public synchronized void atualizarUltimaMensagem() {
		// Interrompe o metodo caso a lista de mensagens nao tiver sido inicializada
		if (mensagens == null || mensagens.isEmpty()) return;

    // Inicio do bloco for
		for (Mensagem m : mensagens) {
			// Inicio do bloco if
			// Se a ultima mensagem for nula ou a mensagem atual for mais recente que a ultima mensagem definida anteriormente
			if (ultimaMensagem == null || m.getTempoEnvio().isAfter(ultimaMensagem.getTempoEnvio())) {
				// A mensagem atual passa a ser a ultima mensagem		
				ultimaMensagem = m;
			} // Fim do bloco if
		} // Fim do bloco for
	}

  /*
   * ***************************************************************
   * Metodo: adicionarMensagem
   * Funcao: adiciona uma nova mensagem enviada no grupo
   * Parametros: Mensagem m - mensagem a ser adicionada
   * Retorno: void
   ****************************************************************/

	public synchronized void adicionarMensagem(Mensagem m) {
		mensagens.add(m);
	}

  /*
   * ***************************************************************
   * Metodo: atualizarStatusMensagem
   * Funcao: modifica o status de alguma mensagem do grupo
   * Parametros: String usuario - autor da mensagem
                 String tempoEnvio - tempo de envio da mensagem
                 String status - status a ser inserido
   * Retorno: void
   ****************************************************************/

	public synchronized void atualizarStatusMensagem(String usuario, String tempoEnvio, String status) {
    if (usuario == null || tempoEnvio == null || status == null) return;

		// Inicio do bloco for
		for (Mensagem m : mensagens) {
			// Obtem o autor e o tempo de envio da mensagem atual
			String autor = m.getAutor();
			String tempoMsgAtual = m.formatarTempoEnvio();

      // Inicio do bloco if
      // Se o autor e o tempo de envio corresponderem aos da mensagem atual e a mensagem nao tiver sido lida anteriormente
			if (autor.equals(usuario) && tempoMsgAtual.equals(tempoEnvio) && !m.isRead()) {
        // Inicio do bloco switch/case
				switch (status) {
          case "DELIVERED":
            m.setStatus(DELIVERED);
            break;

          case "READ":
            m.setStatus(READ);
            m.setRead(true);
            break;

          default:
            break;
        } // Fim do bloco switch/case

        // Interrompe o laco
        break; 
			} // Fim do bloco if
		} // Fim do bloco for
	}

  /*
   * ***************************************************************
   * Metodo: setNome
   * Funcao: define o nome do grupo
   * Parametros: String nome - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setNome(String nome) {
		this.nome = nome;
	}

  /*
   * ***************************************************************
   * Metodo: getNome
   * Funcao: retorna o nome do grupo
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

	public String getNome() {
		return nome;
	}

  /*
   * ***************************************************************
   * Metodo: getMensagens
   * Funcao: retorna a lista de mensagens enviadas no grupo
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: ArrayList<Mensagem>
   ****************************************************************/

  public ArrayList<Mensagem> getMensagens() {
    return mensagens;
  }

  /*
   * ***************************************************************
   * Metodo: setSelected
   * Funcao: define se o grupo foi selecionado ou nao
   * Parametros: boolean s - valor a ser definido
   * Retorno: void
   ****************************************************************/

	public void setSelected(boolean s) {
		this.selected = s;
	}

  /*
   * ***************************************************************
   * Metodo: isSelected
   * Funcao: retorna se o grupo foi selecionado ou nao
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: boolean
   ****************************************************************/

	public boolean isSelected() {
		return selected;
	}
}
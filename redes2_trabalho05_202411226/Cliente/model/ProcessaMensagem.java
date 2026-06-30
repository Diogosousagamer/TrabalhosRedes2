/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 24/06/2026
* Ultima alteracao.: 28/06/2026
* Nome.............: ProcessaMensagem
* Funcao...........: Thread que processa as APDUs enviadas para o cliente
                     no protocolo UDP.
                     
*************************************************************** */

package model;

import controller.TelaGrupoController;
import controller.TelaPrincipalController;
import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.time.LocalDateTime;
import javafx.application.Platform;

public class ProcessaMensagem extends Thread {
	// Variaveis e instancias
	private clienteUDP udp;
	private String mensagem;

  /*
   * ***************************************************************
   * Metodo: ProcessaMensagem
   * Funcao: inicializa uma nova instancia da Thread ProcessaMensagem
   * Parametros: clienteUDP udp - thread do protocolo UDP
                 String mensagem - mensagem a ser processada
   * Retorno: nenhum
   ****************************************************************/

	public ProcessaMensagem(clienteUDP udp, String mensagem) {
		this.udp = udp;
		this.mensagem = mensagem;
	}
	
	/*
   * ***************************************************************
   * Metodo: run
   * Funcao: executa as operacoes da Thread
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	@Override
	public void run() {
		// Inicio do bloco try/catch
		try {
			// Decodifica a mensagem recebida e a empacota em uma APDU
			APDU apdu = APDU.decodificarMensagem(mensagem);

			// Obtem o autor e o tempo de envio da mensagem
			String usuario = apdu.getUsuario();
			LocalDateTime tempoEnvio = apdu.getTempoEnvio();

      // Inicio do bloco if/else if
      // Se for uma APDU SEND
			if ("SEND".equals(apdu.getTipo().trim())) {
				// Carrega a mensagem encaminhada pela APDU
				Mensagem m = new Mensagem(apdu.getMensagem(), usuario, tempoEnvio);

        // Carrega ela para o grupo correspondente
				for (Grupo g : Usuario.getUsuario().getGrupos()) {
					if (g.getNome().equals(apdu.getGrupo())) {
						g.adicionarMensagem(m);
						break;
					}
				}

				// Envia uma APDU informando que ela foi entregue
				udp.enviarAPDU(new APDU("CONFIRM", usuario, apdu.getGrupo(), apdu.getMensagem(), tempoEnvio, "DELIVERED"));
			}
			else if (usuario.equals(Usuario.getUsuario().getNome()) && "CONFIRM".equals(apdu.getTipo().trim())) { // Porem se for uma APDU CONFIRM
				                                                                                                    // e o autor da mensagem for correspondente
				                                                                                                    // ao usuario
				// Busca o grupo informado na APDU
				Grupo g = Usuario.getUsuario().buscarGrupo(apdu.getGrupo());

				// Atualiza o status da mensagem correspondente se o grupo informado na APDU for encontrado
				if (g != null) g.atualizarStatusMensagem(usuario, apdu.formatarTempoEnvio(), apdu.getStatus().trim());

				System.out.println("Mensagem: " + apdu.getMensagem() + " Grupo: " + apdu.getGrupo() + " Envio: " + apdu.formatarTempoEnvio() + " Status: " + apdu.getStatus());
			} // Fim do bloco if/else if

      // Inicio do bloco if/else
      // Se a tela de grupos estiver aberta
			if (TelaGrupoController.grupos != null) {
				// Recarrega as mensagen
				Platform.runLater(() -> TelaGrupoController.grupos.carregarMensagens());
			}
			else {
				// Senao, recarrega apenas os grupos
				Platform.runLater(() -> TelaPrincipalController.principal.carregarGrupos());
			} // Fim do bloco if/else
		}
		catch (Exception e) {
			// Em caso de excecao, emite a pilha de execucao
			// para rastrear a sua origem
			e.printStackTrace();
		} // Fim do bloco try/catch
	}
}
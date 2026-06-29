/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 17/06/2026
* Ultima alteracao.: 27/06/2026
* Nome.............: ProcessaMensagem
* Funcao...........: Thread que processa as APDUs enviadas para o servidor 
                     no protocolo UDP.
                     
*************************************************************** */

package model;

import java.io.*;
import java.net.*;
import java.lang.Thread;
import controller.*;

public class ProcessaMensagem extends Thread {
	private String mensagem;
	private final int PORTA = 6790;

  /*
   * ***************************************************************
   * Metodo: ProcessaMensagem
   * Funcao: inicializa uma nova instancia da Thread ProcessaMensagem
   * Parametros: String mensagem - mensagem a ser processada
   * Retorno: nenhum
   ****************************************************************/

	public ProcessaMensagem(String mensagem) {
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
			// Decodifica a mensagem recebida e a converte em uma APDU
			APDU apdu = APDU.decodificarMensagem(mensagem);

			// Acessa o banco de grupos
			BancoGrupos bancoGrupos = BancoGrupos.getBancoGrupos();

      // Inicio do bloco if/else if
      // Se for uma APDU SEND
			if (apdu.getTipo().equals("SEND")) {
				// Imprime os dados globais (tipo, usuario e grupo)
				TelaPrincipalController.controller.logUDP("Tipo: " + apdu.getTipo());
				TelaPrincipalController.controller.logUDP("Usuario: " + apdu.getUsuario());
				TelaPrincipalController.controller.logUDP("Grupo: " + apdu.getGrupo());

        // Imprime o conteudo da mensagem (se ela nao for nula/vazia)
				if (apdu.getMensagem() != null && !apdu.getMensagem().isEmpty()) {
					TelaPrincipalController.controller.logUDP("Mensagem: " + apdu.getMensagem());
				}

        // Obtem o grupo com o qual o usuario pertence
				String grupo = apdu.getGrupo();
				int numMembrosGrupo = bancoGrupos.obterNumUsuariosGrupo(grupo);

				if (numMembrosGrupo == 1) {
					String ipUsuario = bancoGrupos.obterIpUsuario(grupo);
					enviarMensagem(ipUsuario, new APDU("CONFIRM", apdu.getUsuario(), apdu.getGrupo(), apdu.getMensagem(), apdu.getTempoEnvio(), "READ"));
					return;
				}

        // Inicio do bloco for
				for (String usuario : bancoGrupos.obterUsuariosGrupo(grupo)) {
					// Encaminha a mensagem para que todos os usuarios consigam
					// atualizar o grupo (com excecao do autor original)
					if (usuario.equals(apdu.getUsuario())) continue;
					String ipUsuario = bancoGrupos.obterIpUsuario(usuario);
					enviarMensagem(ipUsuario, apdu);
				} // Fim do bloco for
			}
			else if (apdu.getTipo().equals("CONFIRM") && apdu.getStatus() != null) { // Porem se for uma APDU CONFIRM e o status
				                                                                       // nao for nulo
				// Imprime os dados globais (tipo, usuario e grupo)
				TelaPrincipalController.controller.logUDP("Tipo: " + apdu.getTipo());
				TelaPrincipalController.controller.logUDP("Usuario: " + apdu.getUsuario());
				TelaPrincipalController.controller.logUDP("Grupo: " + apdu.getGrupo());
 
        // Imprime o conteudo da mensagem (se ela nao for nula/vazia)
				if (apdu.getMensagem() != null && !apdu.getMensagem().isEmpty()) {
					TelaPrincipalController.controller.logUDP("Mensagem: " + apdu.getMensagem());
				}

				// Inicio do bloco if
				// Se o status nao tiver vazio
				if (!apdu.getStatus().isEmpty()) {
					// Imprime o status da mensagem
					String status = apdu.getStatus();
					TelaPrincipalController.controller.logUDP("Status: " + status);

          // Obtem o endereco IP do autor
					String ipAutor = bancoGrupos.obterIpUsuario(apdu.getUsuario());

          // Inicio do bloco switch/case
					switch (status) {
						case "DELIVERED": // Caso a mensagem tiver sido entregue a algum membro do grupo
							// Envia a APDU para o autor original da mensagem
							enviarMensagem(ipAutor, apdu);
							break;

						case "READ": // Caso a mensagem tiver sido lida
							// Registra a leitura da mensagem
							bancoGrupos.registrarLeiturasMensagem(apdu.getGrupo(), apdu.getUsuario(), apdu.getMensagem(), apdu.formatarTempoEnvio());

              // Obtem a quantidade de leituras feitas e a quantidade de leituras necessarias
							String msgFormatada = apdu.getGrupo() + " " + apdu.getUsuario() + " " + apdu.getMensagem() + " " + apdu.formatarTempoEnvio();
							int leiturasFeitas = bancoGrupos.obterNumRegistrosMensagem(apdu.getGrupo(), msgFormatada);
							int leiturasNecessarias = bancoGrupos.obterNumUsuariosGrupo(apdu.getGrupo()) - 1;

              // Inicio do bloco if
              // Se todos os usuarios do grupo tiverem lido a mensagem
							if (leiturasFeitas >= leiturasNecessarias) {
								// Envia a APDU para o autor original da mensagem
								enviarMensagem(ipAutor, apdu);
							} // Fim do bloco if

							break; 
					} // Fim do bloco switch/case
				} // Fim do bloco if
			} // Fim do bloco if/else if
		}
		catch (Exception e) {
			// Em caso de excecao, 
			e.printStackTrace();
		} // Fim do bloco try/catch
	}

	private void enviarMensagem(String ipUsuario, APDU apdu) {
		// Inicio do bloco try/catch
		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress enderecoDestino = InetAddress.getByName(ipUsuario);
			byte[] dadosSaida = new byte[1024];

			String mensagem = apdu.enviarMensagem();
			dadosSaida = mensagem.getBytes("UTF-8");

			DatagramPacket pacote = new DatagramPacket(dadosSaida, dadosSaida.length, enderecoDestino, PORTA);
			socket.send(pacote);
		}
		catch (IOException e) {
			e.printStackTrace();
		} // Fim do bloco try/catch
	}
}
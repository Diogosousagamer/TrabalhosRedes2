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

	public ProcessaMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
	@Override
	public void run() {
		try {
			APDU apdu = APDU.decodificarMensagem(mensagem);
			BancoGrupos bancoGrupos = BancoGrupos.getBancoGrupos();

			if (apdu.getTipo().equals("SEND")) {
				TelaPrincipalController.controller.logUDP("Tipo: " + apdu.getTipo());
				TelaPrincipalController.controller.logUDP("Usuario: " + apdu.getUsuario());
				TelaPrincipalController.controller.logUDP("Grupo: " + apdu.getGrupo());

				if (apdu.getMensagem() != null && !apdu.getMensagem().isEmpty()) {
					TelaPrincipalController.controller.logUDP("Mensagem: " + apdu.getMensagem());
				}

				String grupo = apdu.getGrupo();

				for (String usuario : bancoGrupos.obterUsuariosGrupo(grupo)) {
					if (usuario.equals(apdu.getUsuario())) continue;
					String ipUsuario = bancoGrupos.obterIpUsuario(usuario);
					enviarMensagem(ipUsuario, apdu);
				}
			}
			else if (apdu.getTipo().equals("CONFIRM") && apdu.getStatus() != null) {
				TelaPrincipalController.controller.logUDP("Tipo: " + apdu.getTipo());
				TelaPrincipalController.controller.logUDP("Usuario: " + apdu.getUsuario());
				TelaPrincipalController.controller.logUDP("Grupo: " + apdu.getGrupo());

				if (apdu.getMensagem() != null && !apdu.getMensagem().isEmpty()) {
					TelaPrincipalController.controller.logUDP("Mensagem: " + apdu.getMensagem());
				}

				if (!apdu.getStatus().isEmpty()) {
					String status = apdu.getStatus();
					TelaPrincipalController.controller.logUDP("Status: " + status);

					String ipAutor = bancoGrupos.obterIpUsuario(apdu.getUsuario());

					switch (status) {
						case "DELIVERED":
							enviarMensagem(ipAutor, apdu);
							break;

						case "READ":
							bancoGrupos.registrarLeiturasMensagem(apdu.getGrupo(), apdu.getUsuario(), apdu.getMensagem(), apdu.formatarTempoEnvio());

							String msgFormatada = apdu.getGrupo() + " " + apdu.getUsuario() + " " + apdu.getMensagem() + " " + apdu.formatarTempoEnvio();
							int leiturasFeitas = bancoGrupos.obterNumRegistrosMensagem(apdu.getGrupo(), msgFormatada);
							int leiturasNecessarias = bancoGrupos.obterNumUsuariosGrupo(apdu.getGrupo()) - 1;

							if (leiturasFeitas >= leiturasNecessarias) {
								enviarMensagem(ipAutor, apdu);
							}

							break; 
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void enviarMensagem(String ipUsuario, APDU apdu) {
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
		}
	}
}
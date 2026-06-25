/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 17/06/2026
* Ultima alteracao.: 24/06/2026
* Nome.............: ProcessaMensagem
* Funcao...........: Thread que processa as APDUs enviadas para o servidor.
                     
*************************************************************** */

package model;

import java.io.*;
import java.net.*;
import java.lang.Thread;
import controller.*;

public class ProcessaMensagem extends Thread {
	private String mensagem;
	private final int PORTA = 6789;

	public ProcessaMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
	@Override
	public void run() {
		try {
			APDU apdu = APDU.decodificarMensagem(mensagem);

			if (apdu.getTipo().equals("SEND")) {
				TelaPrincipalController.controller.logUDP("Tipo: " + apdu.getTipo());
				TelaPrincipalController.controller.logUDP("Usuario: " + apdu.getUsuario());
				TelaPrincipalController.controller.logUDP("Grupo: " + apdu.getGrupo());

				if (apdu.getMensagem() != null && !apdu.getMensagem().isEmpty()) {
					TelaPrincipalController.controller.logUDP("Mensagem: " + apdu.getMensagem());
				}

				String grupo = apdu.getGrupo();
				BancoGrupos bancoGrupos = BancoGrupos.getBancoGrupos();

				for (String usuario : bancoGrupos.obterUsuariosGrupo(grupo)) {
					if (usuario.equals(apdu.getUsuario())) continue;
					String ipUsuario = bancoGrupos.obterIpUsuario(usuario);
					enviarMensagem(ipUsuario, apdu);
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
			dadosSaida = mensagem.getBytes();

			DatagramPacket pacote = new DatagramPacket(dadosSaida, dadosSaida.length, enderecoDestino, PORTA);
			socket.send(pacote);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 18/06/2026
* Nome.............: clienteUDP
* Funcao...........: Interface do cliente no protocolo UDP.
                     
*************************************************************** */

package model;

import java.io.*;
import java.net.*;
import java.lang.Thread;

public class clienteUDP extends Thread {
	// Variaveis e instancias
	private APDU apdu;
	private byte[] dadosSaida = new byte[1024];
	private ObjectOutputStream saida;
	final int PORTA = 6789;

	@Override
	public void run() {
		try {
			DatagramSocket conexaoCliente = new DatagramSocket();
			InetAddress enderecoIPServidor = InetAddress.getByName(Usuario.ipServidor);
			byte[] dadosSaida = new byte[1024];

			String mensagemEnviada = obterMensagem();
			dadosSaida = mensagemEnviada.getBytes();
			DatagramPacket pacoteEnviado = new DatagramPacket(dadosSaida, dadosSaida.length, enderecoIPServidor, PORTA);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String obterMensagem() {
		if (apdu != null) {
			switch (apdu.getTipo()) {
				case "SEND":
					return apdu.enviarSend();

				case "JOIN":
					return apdu.enviarJoin();

				case "LEAVE": 
					return apdu.enviarLeave();

				default:
					break;
			}
		}

		return "";
	}

	public void setAPDU(APDU apdu) {
		this.apdu = apdu;
	}
}
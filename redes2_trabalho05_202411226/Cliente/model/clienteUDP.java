/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 24/06/2026
* Nome.............: clienteUDP
* Funcao...........: Interface do cliente no protocolo UDP.
                     
*************************************************************** */

package model;

import java.io.*;
import java.net.*;
import java.lang.Thread;

public class clienteUDP extends Thread {
	// Variaveis e instancias
	private byte[] dadosSaida = new byte[1024];
	private ObjectOutputStream saida;
	final int PORTA = 6789;
	private DatagramSocket conexaoCliente;
	private InetAddress ipServidor;

	public void enviarAPDU(APDU apdu) {
		try {
			conexaoCliente = new DatagramSocket();
			ipServidor = InetAddress.getByName(Usuario.getUsuario().getIpServidor());
			byte[] dadosSaida = new byte[1024];

			String mensagemEnviada = (apdu != null) ? apdu.enviarMensagem() : "";
			dadosSaida = mensagemEnviada.getBytes();
			DatagramPacket pacoteEnviado = new DatagramPacket(dadosSaida, dadosSaida.length, ipServidor, PORTA);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void escutarServidor() {
		try {		
			DatagramSocket socket = new DatagramSocket();
			byte[] entrada = new byte[1024];

			while (true) {
				DatagramPacket datagramaRecebido = new DatagramPacket(entrada, entrada.length);
				socket.receive(datagramaRecebido);

				String mensagemRecebida  = new String(datagramaRecebido.getData());
				ProcessaMensagem p = new ProcessaMensagem(mensagemRecebida);
				p.start();
			}
		}
		catch (IOException e) {
			System.err.println("Escuta UDP encerrada abruptamente.");
			e.printStackTrace();
		}
	}
}
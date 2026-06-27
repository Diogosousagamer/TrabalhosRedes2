/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 27/06/2026
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

	@Override
	public void run() {
		escutarServidor();
	}

	public void enviarAPDU(APDU apdu) {
		try {
			conexaoCliente = new DatagramSocket();
			ipServidor = InetAddress.getByName(Usuario.getUsuario().getIpServidor());
			byte[] dadosSaida = new byte[1024];

			String mensagemEnviada = (apdu != null) ? apdu.enviarMensagem() : "";
			dadosSaida = mensagemEnviada.getBytes("UTF-8");
			DatagramPacket pacoteEnviado = new DatagramPacket(dadosSaida, dadosSaida.length, ipServidor, PORTA);

			conexaoCliente.send(pacoteEnviado);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void escutarServidor() {
		try {		
			DatagramSocket socket = new DatagramSocket(6790);
			System.out.println("Cliente UDP escutando na porta 6790.");

			while (true) {
				byte[] entrada = new byte[1024];
				DatagramPacket datagramaRecebido = new DatagramPacket(entrada, entrada.length);
				socket.receive(datagramaRecebido);

				String mensagemRecebida = new String(datagramaRecebido.getData(), 0, datagramaRecebido.getLength(), "UTF-8");
				ProcessaMensagem p = new ProcessaMensagem(this, mensagemRecebida);
				p.start();
			}
		}
		catch (IOException e) {
			System.err.println("Escuta UDP encerrada abruptamente.");
			e.printStackTrace();
		}
	}
}
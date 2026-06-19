/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 19/06/2026
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
	private DatagramSocket conexaoCliente;
	private InetAddress enderecoIPServidor;

	@Override
	public void run() {
		try {
			conexaoCliente = new DatagramSocket();
			enderecoIPServidor = InetAddress.getByName(Usuario.ipServidor);
			byte[] dadosSaida = new byte[1024];

			String mensagemEnviada = (apdu != null) ? apdu.enviarMensagem() : "";
			dadosSaida = mensagemEnviada.getBytes();
			DatagramPacket pacoteEnviado = new DatagramPacket(dadosSaida, dadosSaida.length, enderecoIPServidor, PORTA);

			escutarServidor();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void escutarServidor() {
		try {		
			byte[] bytesRecebidos = new byte[1024];

			while (!conexaoCliente.isClosed()) {
				DatagramPacket pacoteRecebido = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);
				
				// A Thread fica PARADA aqui ate chegar um pacote UDP do servidor
				conexaoCliente.receive(pacoteRecebido);

				// Converte os bytes recebidos para String de forma limpa
				String mensagemDoServidor = new String(pacoteRecebido.getData(), 0, pacoteRecebido.getLength());
				System.out.println("UDP Recebido do Servidor: " + mensagemDoServidor);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setAPDU(APDU apdu) {
		this.apdu = apdu;
	}
}
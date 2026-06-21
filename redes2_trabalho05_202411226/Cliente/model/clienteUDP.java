/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 20/06/2026
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
			while (!conexaoCliente.isClosed()) {
				byte[] bytesRecebidos = new byte[1024];
				DatagramPacket pacoteRecebido = new DatagramPacket(bytesRecebidos, bytesRecebidos.length);
				
				// A Thread permanece parada ate chegar um pacote UDP do servidor
				conexaoCliente.receive(pacoteRecebido);

				// Converte os bytes recebidos para String de forma limpa
				String msg = new String(pacoteRecebido.getData(), 0, pacoteRecebido.getLength());
				APDU apduRecebida = APDU.decodificarMensagem(msg);

				if (apduRecebida != null) {
					System.out.println("Tipo: " + apduRecebida.getTipo());
					System.out.println("Usuario: " + apduRecebida.getUsuario());
					System.out.println("Grupo: " + apduRecebida.getGrupo());

					if (apduRecebida.getMensagem() != null && !apduRecebida.getMensagem().isEmpty()) {
						System.out.println("Mensagem: " + apduRecebida.getMensagem());
					}
				}
			}
		}
		catch (IOException e) {
			System.out.println("Escuta UDP encerrada abruptamente.");
			e.printStackTrace();
		}
	}

	public void setAPDU(APDU apdu) {
		this.apdu = apdu;
	}
}
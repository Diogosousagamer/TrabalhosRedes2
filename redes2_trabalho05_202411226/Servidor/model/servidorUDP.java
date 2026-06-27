/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 27/06/2026
* Nome.............: servidorUDP
* Funcao...........: Interface do servidor no protocolo UDP.
                     
*************************************************************** */

package model;

import java.io.*;
import java.lang.Thread;
import java.net.*;
import controller.*;

public class servidorUDP extends Thread {
	private final int PORTA = 6789;

	@Override
	public void run() {
		try {
			DatagramSocket servidor = new DatagramSocket(PORTA);
			byte[] dadosEntrada = new byte[1024];
			TelaPrincipalController.controller.logUDP("Servidor UDP na porta " + PORTA);

			while (true) {
				DatagramPacket datagramaRecebido = new DatagramPacket(dadosEntrada, dadosEntrada.length);
				servidor.receive(datagramaRecebido);

				String mensagemRecebida  = new String(datagramaRecebido.getData(), 0, datagramaRecebido.getLength(), "UTF-8");
				ProcessaMensagem p = new ProcessaMensagem(mensagemRecebida);
				p.start();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
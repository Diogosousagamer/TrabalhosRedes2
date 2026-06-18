/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 17/06/2026
* Nome.............: servidorUDP
* Funcao...........: Interface do servidor no protocolo UDP.
                     
*************************************************************** */

import java.io.*;
import java.lang.Thread;
import java.net.*;

public class servidorUDP extends Thread {
	@Override
	public void run() {
		try {
			final int portaLocal = 6789;
			DatagramSocket servidor = new DatagramSocket(portaLocal);
			byte[] dadosEntrada = new byte[1024];
			System.out.println("Servidor UDP na porta " + portaLocal);

			while (true) {
				DatagramPacket datagramaRecebido = new DatagramPacket(dadosEntrada, dadosEntrada.length);
				servidor.receive(datagramaRecebido);

				String mensagemRecebida  = new String(datagramaRecebido.getData());
				ProcessaMensagem p = new ProcessaMensagem(mensagemRecebida);
				p.start();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
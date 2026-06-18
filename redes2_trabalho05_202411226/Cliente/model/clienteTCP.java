/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 18/06/2026
* Nome.............: clienteTCP
* Funcao...........: Interface do cliente no protocolo TCP.
                     
*************************************************************** */

package model;

import java.io.*;
import java.net.*;
import java.lang.Thread;

public class clienteTCP extends Thread {
	// Variaveis e instancias
	private APDU apdu;
	final int PORTA = 6789;
	private ObjectOutputStream saida;
	private Socket s;
	private String host = "";

	@Override
	public void run() {
		try {
			host = Usuario.ipServidor;
			s = new Socket(host, PORTA);
			saida = new ObjectOutputStream(s.getOutputStream());

			String msg = obterMensagem();
			saida.writeObject(msg);
			saida.flush();
		}
		catch (IOException e) {
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
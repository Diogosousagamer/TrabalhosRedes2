/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 23/06/2026
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

	public clienteTCP() {
		try {
			host = Usuario.getUsuario().getIpServidor();
			s = new Socket(host, PORTA);
			saida = new ObjectOutputStream(s.getOutputStream());
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public void enviarAPDU(APDU apdu) {
		this.apdu = apdu;

		try {
			String msg = (apdu != null) ? apdu.enviarMensagem() : null;

			if (msg != null) {
				saida.writeObject(msg);
				saida.flush();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
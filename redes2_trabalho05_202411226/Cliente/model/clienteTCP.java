/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 17/06/2026
* Nome.............: clienteTCP
* Funcao...........: Interface do cliente no protocolo TCP.
                     
*************************************************************** */

package model;

import java.io.*;
import java.net.*;
import java.lang.Thread;

public class clienteTCP extends Thread {
	final int PORTA = 6789;

	@Override
	public void run() {
		try {
			host = Usuario.ipServidor;
			Socket s = new Socket(host, porta);
			ObjectOutputStream saida = new ObjectOutputStream(s.getOutputStream());

			String msg = "";
			saida.writeObject(msg);
			saida.flush();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
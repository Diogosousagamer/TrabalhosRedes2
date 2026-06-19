/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 19/06/2026
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

			String msg = (apdu != null) ? apdu.enviarMensagem() : null;
			saida.writeObject(msg);
			saida.flush();

			escutarServidor();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void escutarServidor() {
		try {		
			ObjectInputStream entrada = new ObjectInputStream(s.getInputStream());

			while (!s.isClosed()) {
				Object obj = entrada.readObject();

				if (obj instanceof String) {
					String msg = (String) obj;
					System.out.println("Mensagem do servidor: " + msg);
				}
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
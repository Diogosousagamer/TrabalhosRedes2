/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 21/06/2026
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
			host = Usuario.getUsuario().getIpServidor();
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

			while (true) {
				Object obj = entrada.readObject();
				if (obj == null) continue;

				if (obj instanceof String) {
					String msg = (String) obj;
					APDU apdu = APDU.decodificarMensagem(msg);

					if (apdu != null) {
						System.out.println(apdu.getTipo());
						System.out.println("Usuario: " + apdu.getUsuario());
						System.out.println("Grupo: " + apdu.getGrupo());

						if (apdu.getMensagem() != null && !apdu.getMensagem().isEmpty()) {
							System.out.println("Mensagem: " + apdu.getMensagem());
						}
					}
				}
			}
		}
		catch (IOException e) {
			System.out.println("Escuta TCP encerrada abruptamente.");
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			System.out.println("Escuta TCP encerrada abruptamente.");
			e.printStackTrace();
		}
	}

	public void setAPDU(APDU apdu) {
		this.apdu = apdu;
	}
}
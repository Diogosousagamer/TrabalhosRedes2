/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 02/07/2026
* Nome.............: clienteTCP
* Funcao...........: Interface do cliente no protocolo TCP.
                     
*************************************************************** */

package model;

import java.io.*;
import java.lang.Thread;
import java.net.*;

public class clienteTCP extends Thread {
	// Variaveis e instancias
	final int PORTA = 6789;
	private ObjectOutputStream saida;
	private ObjectInputStream entrada;
	private Socket s;
	private String host = "";

  /*
   * ***************************************************************
   * Metodo: clienteTCP
   * Funcao: inicializa uma nova instancia da Thread clienteTCP
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: nenhum
   ****************************************************************/

	public clienteTCP() {
		// Inicio do bloco try/catch
		try {
			// Abre uma nova socket para se comunicar com o servidor
			host = Usuario.getUsuario().getIpServidor();
			s = new Socket(host, PORTA);

			// Carrega um novo fluxo de entrada/saida para o socket
			saida = new ObjectOutputStream(s.getOutputStream());
			saida.flush();

			entrada = new ObjectInputStream(s.getInputStream());
		}
		catch (IOException e) {
			// Em caso de excecao, sua origem eh rastreada no console
			e.printStackTrace();
		}  // Fim do bloco try/catch
	}

  /*
   * ***************************************************************
   * Metodo: enviarAPDU
   * Funcao: envia uma nova APDU para o servidor
   * Parametros: APDU apdu - apdu a ser enviada
   * Retorno: void
   ****************************************************************/

	public void enviarAPDU(APDU apdu) {
    // Inicio do bloco try/catch
		try {
			// Obtem a mensagem da APDU se ela nao for nula
			String msg = (apdu != null) ? apdu.enviarMensagem() : null;

      // Inicio do bloco if
			if (msg != null) {
				// Escreve e envia a mensagem para ser capturada
				// pelo servidor
				saida.writeObject(msg);
				saida.flush();
			} // Fim do bloco if
		}
		catch (IOException e) {
			// Em caso de excecao, sua origem eh rastreada no console
			e.printStackTrace();
		} // Fim do bloco try/catch
	}

	public boolean conectar(APDU apdu) {
		try {
			if (entrada == null || saida == null) {
				System.err.println("Erro de conexao.");
				fecharConexao();
				return false;
			}

			if (apdu != null && apdu.getTipo().equals("REGISTER")) {
				String msg = (apdu != null) ? apdu.enviarMensagem() : null;

				if (msg != null) {
					saida.writeObject(msg);
					saida.flush();

					return entrada.readBoolean();
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	public void fecharConexao() {
		try {
			this.interrupt();

			if (entrada != null) {
				entrada.close();
				entrada = null;
			}

			if (saida != null) {
				saida.close();
				saida = null;
			}

			if (s != null && !s.isClosed()) {
				s.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
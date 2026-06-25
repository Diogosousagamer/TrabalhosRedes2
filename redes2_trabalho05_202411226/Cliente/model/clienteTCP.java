/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 25/06/2026
* Nome.............: clienteTCP
* Funcao...........: Interface do cliente no protocolo TCP.
                     
*************************************************************** */

package model;

import java.io.*;
import java.lang.Thread;
import java.net.*;
import java.util.Base64;
import javafx.scene.image.Image;

public class clienteTCP extends Thread {
	// Variaveis e instancias
	final int PORTA = 6789;
	private ObjectOutputStream saida;
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

			// Carrega um novo fluxo de saida para o socket
			saida = new ObjectOutputStream(s.getOutputStream());
		}
		catch (IOException e) {
			// Em caso de excecao, sua origem eh rastreada no console
			e.printStackTrace();
		}  // Fim do bloco try/catch
	}

	@Override
	public void run() {
		receber();
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

	public void receber() {
		try {
			ObjectInputStream entrada = new ObjectInputStream(s.getInputStream());

			while (true) {
				String msg = (String) entrada.readObject();

				if (msg != null && !msg.isEmpty()) {
					APDU apdu = APDU.decodificarMensagem(msg);
					String tipo = apdu.getTipo();
					String usuario = apdu.getUsuario();
					Image perfilUsuario = new Image(new ByteArrayInputStream(apdu.getPerfilUsuario()));
					String ipServidor = apdu.getIpServidor();
					Usuario u = new Usuario(perfilUsuario, usuario, ipServidor, Base64.getEncoder().encodeToString(apdu.getPerfilUsuario()));

					switch (tipo) {
						case "JOIN":
							Usuario.registrarUsuarioNaRede(u);
							System.out.println("Usuario " + u.getNome() + " entrou no grupo.");
							break;

						case "LEAVE":
							Usuario.removerUsuarioDaRede(u);
							System.out.println("Usuario " + u.getNome() + " saiu do grupo.");
							break;

						default:
							break;
					}
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
}
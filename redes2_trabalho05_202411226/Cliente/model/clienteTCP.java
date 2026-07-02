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

  /*
   * ***************************************************************
   * Metodo: conectar
   * Funcao: solicita conexao com o servidor TCP
   * Parametros: APDU apdu - apdu de solicitacao (REGISTER)
   * Retorno: boolean
   ****************************************************************/

	public boolean conectar(APDU apdu) {
		// Inicio do bloco try/catch
		try {
			// Inicio do bloco if
			if (entrada == null || saida == null) {
				// Encerra a conexao se o fluxo de entrada/saida nao for inicializado
				// e retorna falso
				System.err.println("Erro de conexao.");
				fecharConexao();
				return false;
			} // Fim do bloco if

			// Inicio do bloco if
			// Se a APDU nao for nula e de fato, for uma solicitacao de login
			if (apdu != null && apdu.getTipo().equals("REGISTER")) {
				// Extrai a mensagem da APDU
				String msg = (apdu != null) ? apdu.enviarMensagem() : null;

        // Inicio do bloco if
        // Se a mensagem nao for nula
				if (msg != null) {
					// Escreve a mensagem no fluxo de saida
					saida.writeObject(msg);
					saida.flush();

          // Retorna o resultado emitido pelo servidor TCP (consulte
				  // a Thread BancoClientes na pasta do Servidor)
					return entrada.readBoolean();
				} // Fim do bloco if
			} // Fim do bloco if
		}
		catch (IOException e) {
			// Em caso de excecao, emite a pilha de execucao para rastrear a sua origem
			e.printStackTrace();

			// Retorna falso
			return false;
		}

		// Retorna falso por padrao
		return false;
	}

  /*
   * ***************************************************************
   * Metodo: fecharConexao
   * Funcao: fecha a conexao com o servidor TCP
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void fecharConexao() {
		// Inicio do bloco try/catch
		try {
			// Interrompe a Thread
			this.interrupt();

			// Inicio do bloco if
			if (entrada != null) {
				// Fecha o fluxo de entrada se ele nao for nulo
				entrada.close();
				entrada = null;
			} // Fim do bloco if

			// Inicio do bloco if
			if (saida != null) {
				// Fecha o fluxo de saida se ele nao for nulo
				saida.close();
				saida = null;
			} // Fim do bloco if

			// Interrompe a conexao se ela nao tiver sido fechada
			if (s != null && !s.isClosed()) s.close();
		}
		catch (Exception e) {
			// Em caso de excecao, emite a pilha de execucao para rastrear a sua origem
			e.printStackTrace();
		} // Fim do bloco try/catch
	}
}
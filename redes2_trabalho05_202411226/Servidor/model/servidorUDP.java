/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 28/06/2026
* Nome.............: servidorUDP
* Funcao...........: Interface do servidor no protocolo UDP.
                     
*************************************************************** */

package model;

import java.io.*;
import java.lang.Thread;
import java.net.*;
import controller.*;

public class servidorUDP extends Thread {
	// Variaveis e instancias
	private final int PORTA = 6789;

  /*
   * ***************************************************************
   * Metodo: run
   * Funcao: executa as operacoes da Thread
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	@Override
	public void run() {
		// Inicio do bloco try/catch
		try {
			// Abre uma nova Socket para que o servidor se comunique com os clientes
			DatagramSocket servidor = new DatagramSocket(PORTA);

			// Cria um vetor de bytes para ler o conteudo do pacote
			byte[] dadosEntrada = new byte[1024];

			// Sinaliza que o servidor UDP esta ativo
			TelaPrincipalController.controller.logUDP("Servidor UDP na porta " + PORTA);

      // Inicio do bloco while
      // Enquanto a Thread estiver ativa
			while (true) {
				// O servidor recebe um novo datagrama encaminhado na Socket
				DatagramPacket datagramaRecebido = new DatagramPacket(dadosEntrada, dadosEntrada.length);
				servidor.receive(datagramaRecebido);

        // Extrai e processa a mensagem do datagrama
				String mensagemRecebida  = new String(datagramaRecebido.getData(), 0, datagramaRecebido.getLength(), "UTF-8");
				ProcessaMensagem p = new ProcessaMensagem(mensagemRecebida);
				p.start();
			} // Fim do bloco while
		}
		catch (Exception e) {
			// Em caso de excecao, emite a pilha de execucao
			// para rastrear a sua origem
			e.printStackTrace();
		} // Fim do bloco try/catch
	} 
}
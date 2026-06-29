/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 28/06/2026
* Nome.............: servidorTCP
* Funcao...........: Interface do servidor no protocolo TCP.
                     
*************************************************************** */

package model;

import java.io.*;
import java.lang.Thread;
import java.net.*;
import controller.*;

public class servidorTCP extends Thread {
	// Variaveis e instancias
	final int PORTA = 6789;

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
			// Abre uma Socket orientada a conexoes para se comunicar com os clientes
			ServerSocket servidor = new ServerSocket(PORTA);
			TelaPrincipalController.controller.logTCP("Servidor TCP na porta " + PORTA);

      // Inicio do bloco while
			while (true) {
				// Procura uma nova conexao para aceitar
				Socket conexao = servidor.accept();

				// Obtem o endereco IP do cliente
				String ipCliente = conexao.getInetAddress().getHostAddress();
				
				// Notifica o sucesso da conexao
				TelaPrincipalController.controller.logTCP("Novo cliente conectado com sucesso: " + ipCliente);

				// Executa o banco de clientes para processar as APDUs encaminhadas via TCP (JOIN e LEAVE)
				BancoClientes bancoClientes = new BancoClientes(conexao, ipCliente);
				bancoClientes.start();
			}	// Fim do bloco while
		}
		catch (Exception e) {
			// Em caso de excecao, emite a pilha de execucao
			// para rastrear a sua origem
			e.printStackTrace();
		} // Fim do bloco try/catch
	}
}
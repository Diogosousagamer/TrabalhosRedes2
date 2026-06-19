/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 19/06/2026
* Nome.............: servidorTCP
* Funcao...........: Interface do servidor no protocolo TCP.
                     
*************************************************************** */

import java.io.*;
import java.lang.Thread;
import java.net.*;

public class servidorTCP extends Thread {
	final int PORTA = 6789;

	@Override
	public void run() {
		try {
			ServerSocket servidor = new ServerSocket(PORTA);
			System.out.println("Servidor TCP na porta " + PORTA);

			while (true) {
				Socket conexao = servidor.accept();
				String ipCliente = conexao.getInetAddress().getHostAddress();
				System.out.println("Novo cliente conectado com sucesso: " + ipCliente);

				BancoClientes bancoClientes = new BancoClientes(conexao, ipCliente);
				bancoClientes.start();
			}	
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
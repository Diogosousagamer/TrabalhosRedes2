/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 18/06/2026
* Nome.............: BancoClientes
* Funcao...........: Thread que gerencia a conexao dos clientes.
                     
*************************************************************** */

import java.io.*;
import java.lang.Thread;
import java.net.*;

public class BancoClientes extends Thread {
	private Socket conexao;
	private String ipUsuario;

	public BancoClientes(Socket conexao, String ipUsuario) {
		this.conexao = conexao;
		this.ipUsuario = ipUsuario;
	}

	@Override
	public void run() {
		try {
			ObjectInputStream entrada = new ObjectInputStream(conexao.getInputStream());
			System.out.println((String) entrada.readObject());

			while (true) {

			}
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
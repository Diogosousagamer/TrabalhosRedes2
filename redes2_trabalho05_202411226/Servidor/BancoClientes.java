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
	private String ipCliente;

	public BancoClientes(Socket conexao, String ipCliente) {
		this.conexao = conexao;
		this.ipCliente = ipCliente;
	}

	@Override
	public void run() {
		try {
			ObjectInputStream entrada = new ObjectInputStream(conexao.getInputStream());

			while (!conexao.isClosed()) {
				String mensagem = (String) entrada.readObject();
				APDU apdu = APDU.decodificarMensagem(mensagem);
				processarMensagem(apdu);
			}
		}
		catch (EOFException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			encerrarConexao();
		}
	}

	private void processarMensagem(APDU apdu) {
		System.out.println("Tipo: " + apdu.getTipo());
		System.out.println("Usuario: " + apdu.getUsuario());
		System.out.println("Grupo: " + apdu.getGrupo());
		if (!apdu.getMensagem().isEmpty()) System.out.println("Mensagem: " + apdu.getMensagem());
		
		String tipo = apdu.getTipo();

		switch (tipo) {
			case "JOIN":
				break;

			case "LEAVE":
				break;

			default:
				break;
		} 
	}

	private void encerrarConexao() {
		try {
			if (!conexao.isClosed()) conexao.close();
			System.out.println("Conexao encerrada com o cliente: " + ipCliente);
		}
		catch (Exception e) {
			System.out.println("Erro ao encerrar a conexao.");
			e.printStackTrace();
		}
	}
}
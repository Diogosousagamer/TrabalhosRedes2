/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 23/06/2026
* Nome.............: BancoClientes
* Funcao...........: Thread que gerencia a conexao dos clientes.
                     
*************************************************************** */

import java.io.*;
import java.lang.Thread;
import java.net.*;
import java.util.HashMap;

public class BancoClientes extends Thread {
	private Socket conexao;
	private String ipCliente;
	private APDU apduRecebida;
	private BancoGrupos bancoGrupos;

	public BancoClientes(Socket conexao, String ipCliente) {
		this.conexao = conexao;
		this.ipCliente = ipCliente;
		bancoGrupos = BancoGrupos.getBancoGrupos();
	}

	@Override
	public void run() {
		try {
			ObjectInputStream entrada = new ObjectInputStream(conexao.getInputStream());

			while (!conexao.isClosed()) {
				String mensagem = (String) entrada.readObject();
				if (mensagem != null && !mensagem.isEmpty()) apduRecebida = APDU.decodificarMensagem(mensagem);
				if (apduRecebida != null) processarMensagem(apduRecebida);
			}
		}
		catch (IOException e) {
			System.err.println("Erro inesperado de entrada/saida: " + e.getMessage());
		}
		catch (ClassNotFoundException e) {
			System.err.println("Classe nao encontrada: " + e.getMessage());
		}
		finally {
			encerrarConexao();
		}
	}

	private void processarMensagem(APDU apdu) {
		if (apdu != null) {
			System.out.println("Tipo: " + apdu.getTipo());
			System.out.println("Usuario: " + apdu.getUsuario());
			System.out.println("Grupo: " + apdu.getGrupo());

			String usuario = apdu.getUsuario();

      HashMap<String, String> listaIpUsuario = bancoGrupos.getListaIpUsuario();
			HashMap<String, String> listaUsuarioIp = bancoGrupos.getListaUsuarioIp();

			listaIpUsuario.putIfAbsent(usuario, ipCliente);
			listaUsuarioIp.putIfAbsent(ipCliente, usuario);

			String tipo = apdu.getTipo();

			switch (tipo) {
				case "JOIN":
					bancoGrupos.adicionarUsuarioGrupo(apdu.getUsuario(), apdu.getGrupo());
					break;

				case "LEAVE":
					bancoGrupos.removerUsuarioGrupo(apdu.getUsuario(), apdu.getGrupo());
					break;

				default:
					break;
			} 
		}
	}

	private void encerrarConexao() {
		try {
			if (!conexao.isClosed()) conexao.close();

			String usuarioDesconectado = bancoGrupos.getListaUsuarioIp().get(ipCliente);

			if (usuarioDesconectado != null) {
				bancoGrupos.limparGruposUsuario(usuarioDesconectado);
				bancoGrupos.removerUsuarioIp(usuarioDesconectado);
				bancoGrupos.removerIpUsuario(ipCliente);
			}

			System.out.println("Conexao encerrada pelo cliente: " + ipCliente);
		}
		catch (Exception e) {
			System.err.println("Erro ao encerrar a conexao.");
		}
	}
}
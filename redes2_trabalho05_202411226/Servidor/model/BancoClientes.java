/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 26/06/2026
* Nome.............: BancoClientes
* Funcao...........: Thread que gerencia a conexao dos clientes.
                     
*************************************************************** */

package model;

import java.io.*;
import java.lang.Thread;
import java.net.*;
import java.util.HashMap;
import controller.*;

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
			TelaPrincipalController.controller.logTCP("Erro inesperado de entrada/saida: " + e.getMessage());
		}
		catch (ClassNotFoundException e) {
			TelaPrincipalController.controller.logTCP("Classe nao encontrada: " + e.getMessage());
		}
		finally {
			encerrarConexao();
		}
	}

	private void processarMensagem(APDU apdu) {
		try {
			if (apdu != null && (apdu.getTipo().equals("JOIN") || apdu.getTipo().equals("LEAVE"))) {
				TelaPrincipalController.controller.logTCP("Tipo: " + apdu.getTipo());
				TelaPrincipalController.controller.logTCP("Usuario: " + apdu.getUsuario());
				TelaPrincipalController.controller.logTCP("Grupo: " + apdu.getGrupo());

				String usuario = apdu.getUsuario();

    		HashMap<String, String> listaIpUsuario = bancoGrupos.getListaIpUsuario();
				HashMap<String, String> listaUsuarioIp = bancoGrupos.getListaUsuarioIp();

				listaIpUsuario.putIfAbsent(usuario, ipCliente);
				listaUsuarioIp.putIfAbsent(ipCliente, usuario);

				String tipo = apdu.getTipo();

				if (tipo.equals("JOIN") || tipo.equals("LEAVE")) {
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

					ObjectOutputStream saida = new ObjectOutputStream(conexao.getOutputStream());
					String grupo = apdu.getGrupo();

					for (String u : bancoGrupos.obterUsuariosGrupo(grupo)) {
						if (u.equals(usuario)) continue;
						String ipUsuario = bancoGrupos.obterIpUsuario(u);
						enviarMensagem(ipUsuario, apdu);
					}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void enviarMensagem(String ipUsuario, APDU apdu) {
		try {
			ObjectOutputStream saida = new ObjectOutputStream(conexao.getOutputStream());
			String msg = (apdu != null) ? apdu.enviarMensagem() : null;

			if (msg != null) {
				saida.writeObject(msg);
				saida.flush();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
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

			TelaPrincipalController.controller.logTCP("Conexao encerrada pelo cliente: " + ipCliente);
		}
		catch (Exception e) {
			TelaPrincipalController.controller.logTCP("Erro ao encerrar a conexao.");
		}
	}
}
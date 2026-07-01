/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 01/07/2026
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
	// Variaveis e instancias
	private Socket conexao;
	private String ipCliente;
	private APDU apduRecebida;
	private BancoGrupos bancoGrupos;

  /*
   * ***************************************************************
   * Metodo: BancoClientes
   * Funcao: Inicializa uma nova instancia da Thread BancoClientes
   * Parametros: Socket socket - conexao entre o cliente e o servidor
                 String ipCliente - endereco IP do cliente
   * Retorno: nenhum
   ****************************************************************/

	public BancoClientes(Socket conexao, String ipCliente) {
		this.conexao = conexao;
		this.ipCliente = ipCliente;
		bancoGrupos = BancoGrupos.getBancoGrupos();
	}

	/*
   * ***************************************************************
   * Metodo: run
   * Funcao: executa as operacoes da Thread
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	@Override
	public void run() {
		try {
			ObjectInputStream entrada = new ObjectInputStream(conexao.getInputStream());
			ObjectOutputStream saida = new ObjectOutputStream(conexao.getOutputStream());

			String msgInicial = (String) entrada.readObject();
			APDU apduRegistro = (msgInicial != null) ? APDU.decodificarMensagem(msgInicial) : null;

			if (apduRegistro != null && "REGISTER".equals(apduRegistro.getTipo().trim())) {
				String usuario = apduRegistro.getUsuario();

				if (!bancoGrupos.usuarioExiste(usuario)) {
					saida.writeBoolean(true);
					saida.flush();

					HashMap<String, String> listaIpUsuario = bancoGrupos.getListaIpUsuario();
					HashMap<String, String> listaUsuarioIp = bancoGrupos.getListaUsuarioIp();

					listaIpUsuario.putIfAbsent(usuario, ipCliente);
					listaUsuarioIp.putIfAbsent(ipCliente, usuario);

					TelaPrincipalController.controller.logTCP("Novo cliente conectado com sucesso: " + ipCliente);
				}
				else {
					saida.writeBoolean(false);
					saida.flush();

					TelaPrincipalController.controller.logTCP("Conexao rejeitada! O usuario " + usuario + " ja existe.");
					conexao.close();
					return;
				}
			} 

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

  /*
   * ***************************************************************
   * Metodo: processarMensagem
   * Funcao: processa as mensagens enviadas para o TCP
   * Parametros: APDU apdu - mensagem a ser processada
   * Retorno: void
   ****************************************************************/

	private void processarMensagem(APDU apdu) {
		if (apdu != null && (apdu.getTipo().equals("JOIN") || apdu.getTipo().equals("LEAVE"))) {
			TelaPrincipalController.controller.logTCP("Tipo: " + apdu.getTipo());
			TelaPrincipalController.controller.logTCP("Usuario: " + apdu.getUsuario());
			TelaPrincipalController.controller.logTCP("Grupo: " + apdu.getGrupo());

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
			}
		}
	}

  /*
   * ***************************************************************
   * Metodo: encerrarConexao
   * Funcao: encerra a conexao do usuario
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

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
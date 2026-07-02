/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 02/07/2026
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
		// Inicio do bloco try/catch/finally
		try {
			// Abre o fluxo de entrada e saida
			ObjectInputStream entrada = new ObjectInputStream(conexao.getInputStream());
			ObjectOutputStream saida = new ObjectOutputStream(conexao.getOutputStream());

			// Tenta receber o estabelecimento de uma nova conexao
			String msgInicial = (String) entrada.readObject();
			APDU apduRegistro = (msgInicial != null) ? APDU.decodificarMensagem(msgInicial) : null;

      // Inicio do bloco if/else
      // Se a APDU inicial se tratar, de fato, de uma solicitacao de conexao
			if (apduRegistro != null && "REGISTER".equals(apduRegistro.getTipo().trim())) {
				// Obtem o nome de usuario
				String usuario = apduRegistro.getUsuario();

				// Inicio do bloco if
				// Se o nome de usuario e o IP nao estao sendo usados por nenhum outro cliente
				if (!bancoGrupos.usuarioExiste(usuario) && !bancoGrupos.ipConectado(ipCliente)) {
					// Retorna verdadeiro para o cliente prosseguir com a conexao
					saida.writeBoolean(true);
					saida.flush();

					// Insere as informacoes do usuario no banco de grupos
					HashMap<String, String> listaIpUsuario = bancoGrupos.getListaIpUsuario();
					HashMap<String, String> listaUsuarioIp = bancoGrupos.getListaUsuarioIp();
					listaIpUsuario.putIfAbsent(usuario, ipCliente);
					listaUsuarioIp.putIfAbsent(ipCliente, usuario);

					// Informa que o cliente foi conectado com sucesso
					TelaPrincipalController.controller.logTCP("Novo cliente conectado com sucesso: " + ipCliente);
				}
				else {
					// Senao, retorna falso, rejeitando a tentativa de conexao
					saida.writeBoolean(false);
					saida.flush();

					// Informa o fracasso da conexao e a encerra, interrompendo a Thread
					TelaPrincipalController.controller.logTCP("Conexao rejeitada! O usuario " + usuario + " ja existe.");
					conexao.close();
					return;
				} // Fim do bloco if/else
			} // Fim do bloco if

			// Inicio do bloco while
			// Enquanto a conexao nao for encerrada
			while (!conexao.isClosed()) {
				// Obtem uma nova mensagem veiculada para a entrada
				String mensagem = (String) entrada.readObject();

				// Decodifica a mensagem e a converte em uma APDU caso ela nao for nula e nao estiver vazia
				if (mensagem != null && !mensagem.isEmpty()) apduRecebida = APDU.decodificarMensagem(mensagem);

				// Processa a APDU recebida se ela nao for nula
				if (apduRecebida != null) processarMensagem(apduRecebida);
			} // Fim do bloco while
		}
		catch (IOException e) {
			// Emite um erro de entrada/saida no log
			TelaPrincipalController.controller.logTCP("Erro inesperado de entrada/saida: " + e.getMessage());
		}
		catch (ClassNotFoundException e) {
			// Emite um erro de classe nao encontrada no log
			TelaPrincipalController.controller.logTCP("Classe nao encontrada: " + e.getMessage());
		}
		finally {
			// Encerra a conexao
			encerrarConexao();
		} // Fim do bloco try/catch/finally
	}

  /*
   * ***************************************************************
   * Metodo: processarMensagem
   * Funcao: processa as mensagens enviadas para o TCP
   * Parametros: APDU apdu - mensagem a ser processada
   * Retorno: void
   ****************************************************************/

	private void processarMensagem(APDU apdu) {
		// Inicio do bloco if
		// Se a APDU nao for nula e corresponder a um JOIN ou um LEAVE
		if (apdu != null && (apdu.getTipo().equals("JOIN") || apdu.getTipo().equals("LEAVE"))) {
			// Registra as informacoes da APDU (tipo, usuario e grupo)
			TelaPrincipalController.controller.logTCP("Tipo: " + apdu.getTipo());
			TelaPrincipalController.controller.logTCP("Usuario: " + apdu.getUsuario());
			TelaPrincipalController.controller.logTCP("Grupo: " + apdu.getGrupo());

			// Obtem o tipo da APDU
			String tipo = apdu.getTipo();

			// Inicio do bloco if
			// Se o tipo da APDU for um JOIN ou um LEAVE
			if (tipo.equals("JOIN") || tipo.equals("LEAVE")) {
				// Inicio do bloco switch/case
				switch (tipo) {
					case "JOIN":
						// Adiciona o usuario ao grupo
						bancoGrupos.adicionarUsuarioGrupo(apdu.getUsuario(), apdu.getGrupo());					
						break;

					case "LEAVE":
						// Remove o usuario do grupo
						bancoGrupos.removerUsuarioGrupo(apdu.getUsuario(), apdu.getGrupo());
						break;

					default:
						break;
				} // Fim do bloco switch/case
			} // Fim do bloco if
		} // Fim do bloco if
	}

  /*
   * ***************************************************************
   * Metodo: encerrarConexao
   * Funcao: encerra a conexao do usuario
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	private void encerrarConexao() {
		// Inicio do bloco try/catch
		try {
			// Fecha a conexao
			if (!conexao.isClosed()) conexao.close();

			// Obtem o nome do usuario desconectado
			String usuarioDesconectado = bancoGrupos.getListaUsuarioIp().get(ipCliente);

			// Inicio do bloco if
			if (usuarioDesconectado != null) {
				// Limpa todos os registros do usuario desconectado caso ele for encontrado
				bancoGrupos.limparGruposUsuario(usuarioDesconectado);
				bancoGrupos.removerUsuarioIp(usuarioDesconectado);
				bancoGrupos.removerIpUsuario(ipCliente);
			} // Fim do bloco if

			// Registra o encerramento da conexao no log
			TelaPrincipalController.controller.logTCP("Conexao encerrada pelo cliente: " + ipCliente);
		}
		catch (Exception e) {
			// Em caso de excecao, registra o erro no log
			TelaPrincipalController.controller.logTCP("Erro ao encerrar a conexao.");
		} // Fim do bloco try/catch
	}
}
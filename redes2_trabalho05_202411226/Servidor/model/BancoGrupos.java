/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 02/07/2026
* Nome.............: BancoGrupos
* Funcao...........: Classe que gerencia a criacao e manutencao dos grupos.
					 
*************************************************************** */

package model;

import java.util.ArrayList;
import java.util.HashMap;
import controller.*;

public class BancoGrupos {
	// Variaveis e instancias
	private static HashMap<String, ArrayList<String>> gruposUsuarios;
	private static HashMap<String, HashMap<String, Integer>> numLeituras;
	private static HashMap<String, HashMap<String, Integer>> numEntregas;
	private static HashMap<String, String> listaIpUsuario;
	private static HashMap<String, String> listaUsuarioIp;
	private static BancoGrupos bancoGrupos;

	/*
   * ***************************************************************
   * Metodo: BancoClientes
   * Funcao: Inicializa uma nova instancia da classe BancoGrupos
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: nenhum
   ****************************************************************/

	public BancoGrupos() {
		gruposUsuarios = new HashMap<>();
		listaIpUsuario = new HashMap<>();
		listaUsuarioIp = new HashMap<>();
		numLeituras = new HashMap<>();
		numEntregas = new HashMap<>();
	}

  /*
   * ***************************************************************
   * Metodo: criarGrupo
   * Funcao: Cria o registro de um novo grupo no servidor
   * Parametros: String grupo - grupo a ser criado
   * Retorno: void
   ****************************************************************/

	public void criarGrupo(String grupo) {
		// Inicio do bloco if/else
		// Se o grupo nao se encontra registrado
		if (!gruposUsuarios.containsKey(grupo)) {
			// Adiciona o grupo e inicializa a lista de usuarios
			gruposUsuarios.put(grupo, new ArrayList<>());

			// Cria um registro da quantidade de entregas e leituras para cada mensagem no grupo
			// para que assim as APDUs CONFIRM possam ser processadas
			if (!numLeituras.containsKey(grupo)) numLeituras.put(grupo, new HashMap<>());
			if (!numEntregas.containsKey(grupo)) numEntregas.put(grupo, new HashMap<>());
		}	
		else {
			// Senao, comunica que o grupo ja se encontra registrado no servidor
			TelaPrincipalController.controller.logTCP("Grupo ja existente.");
		} // Fim do bloco if/else
	}

	/*
   * ***************************************************************
   * Metodo: adicionarUsuarioGrupo
   * Funcao: adiciona o usuario em um determinado grupo
   * Parametros: String usuario - usuario a ser adicionado no grupo
                 String grupo - grupo onde o usuario sera adicionado
   * Retorno: void
   ****************************************************************/

	public synchronized void adicionarUsuarioGrupo(String usuario, String grupo) {
		// Cria o registro do grupo caso ele nao existir
		if (gruposUsuarios.get(grupo) == null) criarGrupo(grupo);

		// Inicio do bloco if/else
		// Se o usuario nao se encontrar no grupo
		if (!gruposUsuarios.get(grupo).contains(usuario)) {
			// Adiciona o usuario no grupo
			gruposUsuarios.get(grupo).add(usuario);
		}
		else {
			// Senao, comunica no log que o usuario ja se encontra no grupo
			TelaPrincipalController.controller.logTCP("Usuario ja se encontra no grupo.");
		} // Fim do bloco if/else
	}

  /*
   * ***************************************************************
   * Metodo: removerUsuarioGrupo
   * Funcao: remove o usuario de um determinado grupo
   * Parametros: String usuario - usuario a ser removido do grupo
                 String grupo - grupo do qual o usuario sera removido
   * Retorno: void
   ****************************************************************/

	public synchronized void removerUsuarioGrupo(String usuario, String grupo) {
		// Inicio do bloco if
		// Se o grupo estiver registrado no servidor e possuir pelo menos um usuario
		if (gruposUsuarios.get(grupo) != null && !gruposUsuarios.get(grupo).isEmpty()) {
			// Inicio do bloco if/else
			// Se o usuario se encontrar no grupo
			if (gruposUsuarios.get(grupo).contains(usuario)) {
				// Remove o usuario do grupo
				gruposUsuarios.get(grupo).remove(usuario);
				TelaPrincipalController.controller.logTCP("Usuario removido do grupo " + grupo + " com sucesso.");

				// Inicio do bloco if
				if (gruposUsuarios.get(grupo).isEmpty()) {
					// Apaga os registros do grupo caso estiver sem usuarios
					gruposUsuarios.remove(grupo);
					numLeituras.remove(grupo);
					numEntregas.remove(grupo);
					TelaPrincipalController.controller.logTCP("Grupo " + grupo + " excluido do servidor.");
				} // Fim do bloco if
			}
			else {
				// Senao, registra no log que o usuario nao se encontra no grupo especificado
				TelaPrincipalController.controller.logTCP("Este usuario nao se encontra no grupo.");
			} // Fim do bloco if/else
		} // Fim do bloco if
	}

  /*
   * ***************************************************************
   * Metodo: limparGruposUsuario
   * Funcao: remove o usuario de todos os grupos
   * Parametros: String usuario - usuario a ser removido de todos os grupos
   * Retorno: void
   ****************************************************************/

	public synchronized void limparGruposUsuario(String usuario) {
		// Remove o usuario de cada grupo que ele se encontra
		for (ArrayList<String> listaUsuarios : gruposUsuarios.values()) {
			listaUsuarios.remove(usuario);
		}

		// Exclui o registro dos grupos que estiverem sem usuarios 
		gruposUsuarios.entrySet().removeIf(entry -> {
			boolean vazio = entry.getValue().isEmpty();

			if (vazio) {
				TelaPrincipalController.controller.logTCP("Grupo " + entry.getKey() + " excluido do servidor.");
				numLeituras.remove(entry.getKey());
			}

			return vazio;
		});
	}

  /*
   * ***************************************************************
   * Metodo: registrarEntregasMensagem
   * Funcao: registra a quantidade de usuarios que receberam a mensagem
             enviada por um dado usuario em certo grupo
   * Parametros: String grupo - grupo onde a mensagem foi enviada
                 String autor - autor da mensagem
                 String mensagem - conteudo da mensagem
                 String tempoEnvio - tempo de envio da mensagem
   * Retorno: void
   ****************************************************************/

	public synchronized void registrarEntregasMensagem(String grupo, String autor, String mensagem, String tempoEnvio) {
		// Inclui o grupo no HashMap de registros de entregas caso ele nao estiver presente
		if (!numEntregas.containsKey(grupo)) numEntregas.put(grupo, new HashMap<>());

		// Obtem a lista de registros de entregas para cada mensagem enviada no grupo
		HashMap<String, Integer> registros = numEntregas.get(grupo);

		// Inclui a mensagem dentro da lista de registros de entregas caso ela nao tiver presente
		String msg = grupo + " " + autor + " " + mensagem + " " + tempoEnvio;
		if (!registros.containsKey(msg)) registros.put(msg, 0);

		// Calcula e atualiza o numero de entregas registradas para aquela mensagem
		int numRegistrosAtual = registros.get(msg) + 1;
		registros.put(msg, numRegistrosAtual);
	}

	/*
   * ***************************************************************
   * Metodo: obterNumEntregasMensagem
   * Funcao: retorna a quantidade de usuarios que receberam a mensagem
             enviada por um dado usuario em certo grupo
   * Parametros: String grupo - grupo onde a mensagem foi enviada
                 String msg - mensagem formatada
   * Retorno: int
   ****************************************************************/

	public synchronized int obterNumEntregasMensagem(String grupo, String msg) {
		// Obtem a lista de entregas registradas para o grupo especificado
		HashMap<String, Integer> registros = numEntregas.get(grupo);

		// Retorna zero se a lista for nula ou nao possuir a mensagem
		if (registros == null || !registros.containsKey(msg)) {
			return 0;
		}

		// Obtem a quantidade de registros atual e a quantidade de registros maxima
		// (o ultimo, nesse caso, corresponde a todos os usuarios exceto o autor da mensagem)
		int numRegistros = registros.get(msg);
		int registrosMax = obterNumUsuariosGrupo(grupo) - 1;

		// Remove a mensagem caso tiver sido atingido o limite de registros
		if (numRegistros >= registrosMax) {
			registros.remove(msg);
		}

		// Retorna a quantidade de registros
		return numRegistros;
	}

	/*
   * ***************************************************************
   * Metodo: registrarLeiturasMensagem
   * Funcao: registra a quantidade de usuarios que leram a mensagem
             enviada por um dado usuario em certo grupo
   * Parametros: String grupo - grupo onde a mensagem foi enviada
                 String autor - autor da mensagem
                 String mensagem - conteudo da mensagem
                 String tempoEnvio - tempo de envio da mensagem
   * Retorno: void
   ****************************************************************/

	public synchronized void registrarLeiturasMensagem(String grupo, String autor, String mensagem, String tempoEnvio) {
		// Inclui o grupo no HashMap de registros de leituras caso ele nao estiver presente
		if (!numLeituras.containsKey(grupo)) numLeituras.put(grupo, new HashMap<>());

		// Obtem a lista de registros de leituras para cada mensagem enviada no grupo
		HashMap<String, Integer> registros = numLeituras.get(grupo);

		// Inclui a mensagem dentro da lista de registros de leituras caso ela nao tiver presente
		String msg = grupo + " " + autor + " " + mensagem + " " + tempoEnvio;
		if (!registros.containsKey(msg)) registros.put(msg, 0);

		// Calcula e atualiza o numero de leituras registradas para aquela mensagem
		int numRegistrosAtual = registros.get(msg) + 1;
		registros.put(msg, numRegistrosAtual);
	}

	/*
   * ***************************************************************
   * Metodo: obterNumRegistrosMensagem
   * Funcao: retorna a quantidade de usuarios que leram a mensagem
             enviada por um dado usuario em certo grupo
   * Parametros: String grupo - grupo onde a mensagem foi enviada
                 String msg - mensagem formatada
   * Retorno: int
   ****************************************************************/

	public synchronized int obterNumRegistrosMensagem(String grupo, String msg) {
		// Obtem a lista de leituras registradas para o grupo especificado
		HashMap<String, Integer> registros = numLeituras.get(grupo);

		// Retorna zero se a lista for nula ou nao possuir a mensagem
		if (registros == null || !registros.containsKey(msg)) {
			return 0;
		}

		// Obtem a quantidade de registros atual e a quantidade de registros maxima
		// (o ultimo, nesse caso, corresponde a todos os usuarios exceto o autor da mensagem)
		int numRegistros = registros.get(msg);
		int registrosMax = obterNumUsuariosGrupo(grupo) - 1;

		// Remove a mensagem caso tiver sido atingido o limite de registros
		if (numRegistros >= registrosMax) {
			registros.remove(msg);
		}

		// Retorna a quantidade de registros
		return numRegistros;
	}

	/*
   * ***************************************************************
   * Metodo: obterIpUsuario
   * Funcao: obtem o endereco ip de certo usuario
   * Parametros: String usuario - usuario cujo endereco IP sera obtido
   * Retorno: String
   ****************************************************************/

	public synchronized String obterIpUsuario(String usuario) {
		return listaIpUsuario.get(usuario);
	}

	/*
   * ***************************************************************
   * Metodo: removerIpUsuario
   * Funcao: remove o endereco ip de certo usuario
   * Parametros: String usuario - usuario cujo endereco IP sera removido
   * Retorno: void
   ****************************************************************/

	public synchronized void removerIpUsuario(String usuario) {
		listaIpUsuario.remove(usuario);
	}

  /*
   * ***************************************************************
   * Metodo: removerUsuarioIp
   * Funcao: remove o ususario do servidor
   * Parametros: String ip - endereco IP do usuario a ser removido
   * Retorno: void
   ****************************************************************/

	public synchronized void removerUsuarioIp(String ip) {
		listaUsuarioIp.remove(ip);
	}

  /*
   * ***************************************************************
   * Metodo: obterUsuariosGrupo
   * Funcao: obtem a lista de usuarios de um determinado grupo
   * Parametros: String grupo - grupo a ser obtido
   * Retorno: ArrayList<String>
   ****************************************************************/

	public ArrayList<String> obterUsuariosGrupo(String grupo) {
		return gruposUsuarios.get(grupo);
	}

	/*
   * ***************************************************************
   * Metodo: obterNumUsuariosGrupo
   * Funcao: obtem a quantidade de usuarios presentes em um determinado grupo
   * Parametros: String grupo - grupo a ser obtido
   * Retorno: int
   ****************************************************************/

	public int obterNumUsuariosGrupo(String grupo) {
		return gruposUsuarios.get(grupo).size();
	}

	/*
   * ***************************************************************
   * Metodo: usuarioExiste
   * Funcao: verifica se o nome de usuario ja esta sendo usado por algum cliente
   * Parametros: String usuario - usuario a ser verificado
   * Retorno: boolean
   ****************************************************************/

	public boolean usuarioExiste(String usuario) {
		return listaIpUsuario.containsKey(usuario);
	}

	/*
   * ***************************************************************
   * Metodo: ipConectado
   * Funcao: verifica se o endereco IP ja esta sendo usado por algum cliente
   * Parametros: String ip - endereco IP a ser verificado
   * Retorno: boolean
   ****************************************************************/

	public boolean ipConectado(String ip) {
		return listaUsuarioIp.containsKey(ip);
	}

  /*
   * ***************************************************************
   * Metodo: getBancoGrupos
   * Funcao: retorna a instancia estatica da classe para garantir consistencia
             nos dados armazenados
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: BancoGrupos
   ****************************************************************/

	public static BancoGrupos getBancoGrupos() {
		// Inicializa a instancia caso ela estiver nula
		if (bancoGrupos == null) bancoGrupos = new BancoGrupos();

		// Retorna a instancia
		return bancoGrupos;
	}

	/*
   * ***************************************************************
   * Metodo: getListaIpUsuario
   * Funcao: retorna a lista de enderecos IP distribuidos por usuario
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: HashMap<String, String>
   ****************************************************************/

	public HashMap<String, String> getListaIpUsuario() {
		return listaIpUsuario;
	}

	/*
   * ***************************************************************
   * Metodo: getListaUsuarioIp
   * Funcao: retorna a lista de usuarios distribuidos por endereco IP
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: HashMap<String, String>
   ****************************************************************/

	public HashMap<String, String> getListaUsuarioIp() {
		return listaUsuarioIp;
	}
}
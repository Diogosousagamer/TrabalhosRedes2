/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 26/06/2026
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

	public BancoGrupos() {
		gruposUsuarios = new HashMap<>();
		listaIpUsuario = new HashMap<>();
		listaUsuarioIp = new HashMap<>();
		numLeituras = new HashMap<>();
		numEntregas = new HashMap<>();
	}

	public void criarGrupo(String grupo) {
		if (!gruposUsuarios.containsKey(grupo)) {
			gruposUsuarios.put(grupo, new ArrayList<>());
			if (!numLeituras.containsKey(grupo)) numLeituras.put(grupo, new HashMap<>());
		}	
		else {
			TelaPrincipalController.controller.logTCP("Grupo ja existente.");
		}
	}

	public synchronized void adicionarUsuarioGrupo(String usuario, String grupo) {
		if (gruposUsuarios.get(grupo) == null) {
			criarGrupo(grupo);
		}

		if (!gruposUsuarios.get(grupo).contains(usuario)) {
			gruposUsuarios.get(grupo).add(usuario);
		}
		else {
			TelaPrincipalController.controller.logTCP("Usuario ja se encontra no grupo.");
		}
	}

	public synchronized void removerUsuarioGrupo(String usuario, String grupo) {
		if (gruposUsuarios.get(grupo) != null && !gruposUsuarios.get(grupo).isEmpty()) {
			if (gruposUsuarios.get(grupo).contains(usuario)) {
				gruposUsuarios.get(grupo).remove(usuario);
				TelaPrincipalController.controller.logTCP("Usuario removido do grupo " + grupo + " com sucesso.");

				if (gruposUsuarios.get(grupo).isEmpty()) {
					gruposUsuarios.remove(grupo);
					numLeituras.remove(grupo);
					TelaPrincipalController.controller.logTCP("Grupo " + grupo + " excluido do servidor.");
				}
			}
			else {
				TelaPrincipalController.controller.logTCP("Este usuario nao se encontra no grupo.");
			}
		}
	}

	public synchronized void limparGruposUsuario(String usuario) {
		for (ArrayList<String> listaUsuarios : gruposUsuarios.values()) {
			listaUsuarios.remove(usuario);
		}

		gruposUsuarios.entrySet().removeIf(entry -> {
			boolean vazio = entry.getValue().isEmpty();

			if (vazio) {
				TelaPrincipalController.controller.logTCP("Grupo " + entry.getKey() + " excluido do servidor.");
				numLeituras.remove(entry.getKey());
			}

			return vazio;
		});
	}

	public synchronized void registrarEntregasMensagem(String grupo, String autor, String mensagem, String tempoEnvio) {
		if (!numEntregas.containsKey(grupo)) numEntregas.put(grupo, new HashMap<>());
		HashMap<String, Integer> registros = numEntregas.get(grupo);

		String msg = grupo + " " + autor + " " + mensagem + " " + tempoEnvio;
		if (!registros.containsKey(msg)) registros.put(msg, 0);

		int numRegistrosAtual = registros.get(msg) + 1;
		registros.put(msg, numRegistrosAtual);
	}

	public synchronized int obterNumEntregasMensagem(String grupo, String msg) {
		HashMap<String, Integer> registros = numEntregas.get(grupo);

		if (registros == null || !registros.containsKey(msg)) {
			return 0;
		}

		int numRegistros = registros.get(msg);
		int registrosMax = obterNumUsuariosGrupo(grupo) - 1;

		if (numRegistros >= registrosMax) {
			registros.remove(msg);
		}

		return numRegistros;
	}

	public synchronized void registrarLeiturasMensagem(String grupo, String autor, String mensagem, String tempoEnvio) {
		if (!numLeituras.containsKey(grupo)) numLeituras.put(grupo, new HashMap<>());
		HashMap<String, Integer> registros = numLeituras.get(grupo);

		String msg = grupo + " " + autor + " " + mensagem + " " + tempoEnvio;
		if (!registros.containsKey(msg)) registros.put(msg, 0);

		int numRegistrosAtual = registros.get(msg) + 1;
		registros.put(msg, numRegistrosAtual);
	}

	public synchronized int obterNumRegistrosMensagem(String grupo, String msg) {
		HashMap<String, Integer> registros = numLeituras.get(grupo);

		if (registros == null || !registros.containsKey(msg)) {
			return 0;
		}

		int numRegistros = registros.get(msg);
		int registrosMax = obterNumUsuariosGrupo(grupo) - 1;

		if (numRegistros >= registrosMax) {
			registros.remove(msg);
		}

		return numRegistros;
	}

	public synchronized String obterIpUsuario(String usuario) {
		return listaIpUsuario.get(usuario);
	}

	public synchronized void removerIpUsuario(String usuario) {
		listaIpUsuario.remove(usuario);
	}

	public synchronized void removerUsuarioIp(String ip) {
		listaUsuarioIp.remove(ip);
	}

	public ArrayList<String> obterUsuariosGrupo(String grupo) {
		return gruposUsuarios.get(grupo);
	}

	public int obterNumUsuariosGrupo(String grupo) {
		return gruposUsuarios.get(grupo).size();
	}

	public static BancoGrupos getBancoGrupos() {
		if (bancoGrupos == null) bancoGrupos = new BancoGrupos();
		return bancoGrupos;
	}

	public HashMap<String, String> getListaIpUsuario() {
		return listaIpUsuario;
	}

	public HashMap<String, String> getListaUsuarioIp() {
		return listaUsuarioIp;
	}
}
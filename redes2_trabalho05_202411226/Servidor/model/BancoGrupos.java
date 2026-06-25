/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 24/06/2026
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
	private static HashMap<String, String> listaIpUsuario;
	private static HashMap<String, String> listaUsuarioIp;
	private static BancoGrupos bancoGrupos;

	public BancoGrupos() {
		gruposUsuarios = new HashMap<>();
		listaIpUsuario = new HashMap<>();
		listaUsuarioIp = new HashMap<>();
	}

	public void criarGrupo(String grupo) {
		if (!gruposUsuarios.containsKey(grupo)) {
			gruposUsuarios.put(grupo, new ArrayList<>());
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
			if (vazio) TelaPrincipalController.controller.logTCP("Grupo " + entry.getKey() + " excluido do servidor.");
			return vazio;
		});
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
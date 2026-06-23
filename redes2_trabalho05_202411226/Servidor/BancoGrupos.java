/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 23/06/2026
* Nome.............: BancoGrupos
* Funcao...........: Classe que gerencia a criacao e manutencao dos grupos.
                     
*************************************************************** */

import java.util.ArrayList;
import java.util.HashMap;

public class BancoGrupos {
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
			System.out.println("Grupo ja existente.");
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
			System.out.println("Usuario ja se encontra no grupo.");
		}
	}

	public synchronized void removerUsuarioGrupo(String usuario, String grupo) {
		if (gruposUsuarios.get(grupo) != null && !gruposUsuarios.get(grupo).isEmpty()) {
			if (gruposUsuarios.get(grupo).contains(usuario)) {
				gruposUsuarios.get(grupo).remove(usuario);
				System.out.println("Usuario removido do grupo " + grupo + " com sucesso.");

				if (gruposUsuarios.get(grupo).isEmpty()) {
					gruposUsuarios.remove(grupo);
					System.out.println("Grupo " + grupo + " excluido do servidor.");
				}
			}
			else {
				System.out.println("Este usuario nao se encontra no grupo.");
			}
		}
	}

	public synchronized void limparGruposUsuario(String usuario) {
		for (ArrayList<String> listaUsuarios : gruposUsuarios.values()) {
      		listaUsuarios.remove(usuario);
      	}

	    gruposUsuarios.entrySet().removeIf(entry -> {
	    	boolean vazio = entry.getValue().isEmpty();
	    	if (vazio) System.out.println("Grupo " + entry.getKey() + " excluido do servidor.");
	    	return vazio;
	    });
	}

	public synchronized void removerUsuarioIp(String usuario) {
		listaIpUsuario.remove(usuario);
	}

	public synchronized void removerIpUsuario(String ip) {
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
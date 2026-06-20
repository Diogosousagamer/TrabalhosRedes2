/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 20/06/2026
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

		ArrayList<String> listaUsuarios = gruposUsuarios.get(grupo);

		if (!listaUsuarios.contains(usuario)) {
			listaUsuarios.add(usuario);
		}
		else {
			System.out.println("Usuario ja se encontra no grupo.");
		}
	}

	public synchronized void removerUsuarioGrupo(String usuario, String grupo) {
		ArrayList<String> listaUsuarios = gruposUsuarios.get(grupo);

		if (listaUsuarios != null) {
			if (listaUsuarios.contains(usuario)) {
				listaUsuarios.remove(usuario);
				System.out.println("Usuario removido do grupo " + grupo + " com sucesso.");

				if (listaUsuarios.isEmpty()) {
					gruposUsuarios.remove(grupo);
					System.out.println("Grupo excluido do servidor.");
				}
			}
			else {
				System.out.println("Este usuario nao se encontra no grupo.");
			}
		}
	}

	public static BancoGrupos getBancoGrupos() {
		if (bancoGrupos == null) bancoGrupos = new BancoGrupos();
		return bancoGrupos;
	}
}
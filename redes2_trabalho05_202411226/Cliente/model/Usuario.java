/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 10/06/2026
* Nome.............: Usuario
* Funcao...........: Classe que controla as operacoes dos usuarios do aplicativo.
                     
*************************************************************** */

package model;

import java.util.ArrayList;
import javafx.scene.image.Image;

public class Usuario {
	public static Image perfil;
	public static String nome;
	public static String ipServidor;
	public static ArrayList<Grupo> grupos;
	public static clienteTCP tcp;
	public static clienteUDP udp;

	public Usuario(Image perfil, String nome, String ipServidor) {
		this.perfil = perfil;
		this.nome = nome;
		this.ipServidor = ipServidor;
		grupos = new ArrayList<>();
		tcp = new clienteTCP();
		udp = new clienteUDP();
	}
}
/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 19/06/2026
* Nome.............: Usuario
* Funcao...........: Classe que controla as operacoes dos usuarios do aplicativo.
                     
*************************************************************** */

package model;

import java.util.ArrayList;
import javafx.scene.image.Image;

public class Usuario {
	// Variaveis e instancias
	public static Image perfil;
	public static String nome;
	public static String ipServidor;
	public static ArrayList<Grupo> grupos;
	public static clienteTCP tcp;
	public static clienteUDP udp;

	public Usuario(Image perfil, String nome, String ipServidor) {
		Usuario.perfil = perfil;
		Usuario.nome = nome;
		Usuario.ipServidor = ipServidor;
		Usuario.grupos = new ArrayList<>();

    // Inicializa o TCP e o UDP para que o cliente se comunique
    // com o servidor
		tcp = new clienteTCP();
		udp = new clienteUDP();
		tcp.setDaemon(true);
		udp.setDaemon(true);
		tcp.start();
		udp.start();
	}
}
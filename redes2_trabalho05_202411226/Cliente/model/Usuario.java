/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 25/06/2026
* Nome.............: Usuario
* Funcao...........: Classe que controla as operacoes dos usuarios do aplicativo.
                     
*************************************************************** */

package model;

import java.util.ArrayList;
import javafx.scene.image.Image;

public class Usuario {
  // Variaveis e instancias
  private String nome;
  private String ipServidor;
  private ArrayList<Grupo> grupos;
  private clienteTCP tcp;
  private clienteUDP udp;
  private static Usuario usuario;

  public Usuario(String nome, String ipServidor) {
    this.nome = nome;
    this.ipServidor = ipServidor;
    this.grupos = new ArrayList<>();
  }

  public static boolean conectarUsuario(String nome, String ipServidor) {
    try {
      if (usuario == null) usuario = new Usuario(nome, ipServidor);
      usuario.iniciarProtocolos();
      return true;
    }
    catch (Exception e) {
      System.err.println("Nao foi possivel conectar ao servidor " + ipServidor + ". Tente novamente mais tarde.");
      return false;
    }
  }

  public static Usuario getUsuario() {
    return usuario;
  }

  private void iniciarProtocolos() {
    this.tcp = new clienteTCP();
    this.udp = new clienteUDP();
      
    tcp.setDaemon(true);
    udp.setDaemon(true);
      
    tcp.start();
    udp.start();  
  }

  public void setIpServidor(String ipServidor) {
  	this.ipServidor = ipServidor;
  }

  public String getIpServidor() { 
  	return ipServidor; 
 	}

 	public void setNome(String nome) {
 		this.nome = nome;
 	}

  public String getNome() { 
  	return nome; 
  }

  public ArrayList<Grupo> getGrupos() { 
  	return grupos; 
  }

  public clienteTCP getTCP() {
  	return tcp;
  }

  public clienteUDP getUDP() {
  	return udp;
  }
}
/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 21/06/2026
* Nome.............: Usuario
* Funcao...........: Classe que controla as operacoes dos usuarios do aplicativo.
                     
*************************************************************** */

package model;

import java.util.ArrayList;
import javafx.scene.image.Image;

public class Usuario {
  // Variaveis e instancias
  private Image perfil;
  private String nome;
  private String ipServidor;
  private ArrayList<Grupo> grupos;
  private clienteTCP tcp;
  private clienteUDP udp;
  private static Usuario usuario;

  private Usuario(Image perfil, String nome, String ipServidor) {
    this.perfil = perfil;
    this.nome = nome;
    this.ipServidor = ipServidor;
    this.grupos = new ArrayList<>();
  }

  public static void conectarUsuario(Image perfil, String nome, String ipServidor) {
    if (usuario == null) usuario = new Usuario(perfil, nome, ipServidor);
  }

  public static Usuario getUsuario() {
    return usuario;
  }

  public void iniciarProtocolos() {
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
  	return this.ipServidor; 
 	}

 	public void setNome(String nome) {
 		this.nome = nome;
 	}

  public String getNome() { 
  	return this.nome; 
  }

  public void setPerfil(Image perfil) {
  	this.perfil = perfil;
  }

  public Image getPerfil() { 
  	return this.perfil; 
  }

  public ArrayList<Grupo> getGrupos() { 
  	return this.grupos; 
  }

  public clienteTCP getTCP() {
  	return tcp;
  }

  public clienteUDP getUDP() {
  	return udp;
  }
}
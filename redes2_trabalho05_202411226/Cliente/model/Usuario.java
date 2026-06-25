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
  private Image perfil;
  private String nome;
  private String ipServidor;
  private String caminhoImagem;
  private ArrayList<Grupo> grupos;
  private clienteTCP tcp;
  private clienteUDP udp;
  private static Usuario usuario;
  private static ArrayList<Usuario> usuariosConectados;

  public Usuario(Image perfil, String nome, String ipServidor, String caminhoImagem) {
    this.perfil = perfil;
    this.nome = nome;
    this.ipServidor = ipServidor;
    this.caminhoImagem = caminhoImagem;
    this.grupos = new ArrayList<>();
    this.usuariosConectados = new ArrayList<>();
  }

  public static boolean conectarUsuario(Image perfil, String nome, String ipServidor, String caminhoImagem) {
    try {
      if (usuario == null) usuario = new Usuario(perfil, nome, ipServidor, caminhoImagem);
      usuario.iniciarProtocolos();
      registrarUsuarioNaRede(usuario);
      return true;
    }
    catch (Exception e) {
      System.err.println("Nao foi possivel conectar ao servidor " + ipServidor + ". Tente novamente mais tarde.");
      return false;
    }
  }

  public static void registrarUsuarioNaRede(Usuario u) {
    usuariosConectados.removeIf(usuario -> usuario.getNome().equals(u.getNome()));
    usuariosConectados.add(u);
  }

  public static Usuario buscarUsuarioPorNome(String u) {
    for (Usuario usuario : usuariosConectados) {
      if (usuario.getNome().equalsIgnoreCase(u)) {
        return usuario;
      }
    }

    return null;
  }

  public static void removerUsuarioDaRede(Usuario u) {
    usuariosConectados.removeIf(user -> user.getNome().equals(u.getNome()));
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

    udp.escutarServidor();
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

  public void setPerfil(Image perfil) {
  	this.perfil = perfil;
  }

  public Image getPerfil() { 
  	return perfil; 
  }

  public void setCaminhoImagem(String img) {
    this.caminhoImagem = img;
  }

  public String getCaminhoImagem() {
    return caminhoImagem;
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
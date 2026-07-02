/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 02/07/2026
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

  /*
   * ***************************************************************
   * Metodo: Usuario
   * Funcao: inicializa uma nova instancia da classe Usuario
   * Parametros: String nome - nome do usuario
                 String ipServidor - endereco IP do servidor
   * Retorno: nenhum
   ****************************************************************/

  public Usuario(String nome, String ipServidor) {
    this.nome = nome;
    this.ipServidor = ipServidor;
    this.grupos = new ArrayList<>();
  }

  /*
   * ***************************************************************
   * Metodo: conectarUsuario
   * Funcao: tenta conectar um novo usuario ao servidor
   * Parametros: String nome - nome do usuario
                 String ipServidor - endereco IP do servidor
   * Retorno: boolean
   ****************************************************************/

  public static boolean conectarUsuario(String nome, String ipServidor) {
    // Inicio do bloco try/catch
    try {
      // Inicializa o usuario e inicia os protocolos
      usuario = new Usuario(nome, ipServidor);
      usuario.iniciarProtocolos();

      // Envia uma APDU solicitando conexao
      boolean sucesso =  usuario.getTCP().conectar(new APDU("REGISTER", usuario.getNome()));

      // Inicio do bloco if
      if (!sucesso && usuario != null) {
        // Encerra os protocolos se a conexao nao for bem sucedida (para evitar BindException)
        // e a instancia estatica do usuario nao for nula 
        usuario.encerrarProtoclos();
      } // Fim do bloco if

      // Retorna o resultado da conexao (sucesso ou fracasso)
      return sucesso;
    }
    catch (Exception e) {
      // Em caso de excecao, notifica erro e retorna falso
      System.err.println("Nao foi possivel conectar ao servidor " + ipServidor + ". Tente novamente mais tarde.");
      return false;
    } // Fim do bloco try/catch
  }

  /*
   * ***************************************************************
   * Metodo: iniciarProtocolos
   * Funcao: inicia os protocolos para que a maquina do usuario 
             se comunique com o servidor
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

  private void iniciarProtocolos() {
    this.tcp = new clienteTCP();
    this.udp = new clienteUDP();
    tcp.setDaemon(true);
    udp.setDaemon(true); 
    tcp.start();
    udp.start();  
  }

  private void encerrarProtocolos() {
    if (tcp != null) {
      tcp.fecharConexao();
    }

    if (udp != null) {
      udp.fecharConexao();
    }
  }

  /*
   * ***************************************************************
   * Metodo: buscarGrupo
   * Funcao: busca um grupo dentro da lista de grupos na qual o usuario
             se encontra
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Grupo
   ****************************************************************/

  public synchronized Grupo buscarGrupo(String grupo) {
    // Inicio do bloco for
    for (Grupo g : grupos) {
      // Inicio do bloco if
      if (g.getNome().equals(grupo)) {
        // Retorna o grupo atual caso ele corresponder
        // ao grupo buscado
        return g;
      } // Fim do bloco if
    } // Fim do bloco for

    // Retorna nulo caso nao for encontrada nenhuma correspondencia
    // ao grupo buscado
    return null;
  }

  /*
   * ***************************************************************
   * Metodo: setNome
   * Funcao: define o nome do usuario
   * Parametros: String nome - valor a ser definido
   * Retorno: void
   ****************************************************************/

 	public void setNome(String nome) {
 		this.nome = nome;
 	}

  /*
   * ***************************************************************
   * Metodo: getNome
   * Funcao: retorna o nome do usuario
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

  public String getNome() { 
  	return nome; 
  }

  /*
   * ***************************************************************
   * Metodo: setIpServidor
   * Funcao: define o endereco IP do servidor
   * Parametros: String ipServidor - valor a ser definido
   * Retorno: void
   ****************************************************************/

  public void setIpServidor(String ipServidor) {
    this.ipServidor = ipServidor;
  }

  /*
   * ***************************************************************
   * Metodo: getIpServidor
   * Funcao: retorna o endereco IP do servidor
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: String
   ****************************************************************/

  public String getIpServidor() { 
    return ipServidor; 
  }

  /*
   * ***************************************************************
   * Metodo: getGrupos
   * Funcao: retorna a lista de grupos
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: ArrayList<Grupo>
   ****************************************************************/

  public ArrayList<Grupo> getGrupos() { 
  	return grupos; 
  }

  /*
   * ***************************************************************
   * Metodo: getTCP
   * Funcao: retorna a Thread do protocolo TCP
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: clienteTCP
   ****************************************************************/

  public clienteTCP getTCP() {
  	return tcp;
  }

  /*
   * ***************************************************************
   * Metodo: getUDP
   * Funcao: retorna a Thread do protocolo UDP
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: clienteUDP
   ****************************************************************/

  public clienteUDP getUDP() {
  	return udp;
  }

  /*
   * ***************************************************************
   * Metodo: getUsuario
   * Funcao: retorna a instancia estatica do usuario da maquina
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: Usuario
   ****************************************************************/

  public static Usuario getUsuario() {
    return usuario;
  }
}
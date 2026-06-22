/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 22/06/2026
* Nome.............: TelaGrupoController
* Funcao...........: Classe que controla os eventos da TelaGrupo.
                     
*************************************************************** */

package controller;

import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.APDU;
import model.Grupo;
import model.Mensagem;
import model.Usuario;

public class TelaGrupoController {
	// Componentes da interface
	@FXML private Button btnEnviarMensagem;
  @FXML private Button btnSairChat;
  @FXML private Button btnSairGrupo;
  @FXML private Label lblNomeGrupo;
  @FXML private TextField txtMensagem;

  // Variaveis e instancias
  private Grupo g;

  public void carregarGrupo(Grupo g) {
    this.g = g;
    lblNomeGrupo.setText(this.g.getNome());   
    carregarMensagens();
  }  

  private void carregarMensagens() {
    ArrayList<Mensagem> mensagens = g.getMensagens();
    if (mensagens == null || mensagens.isEmpty()) return;

    for (Mensagem m : mensagens) {

    }
  }
}
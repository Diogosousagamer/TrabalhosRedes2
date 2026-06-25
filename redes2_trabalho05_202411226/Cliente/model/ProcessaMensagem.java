/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 24/06/2026
* Ultima alteracao.: 24/06/2026
* Nome.............: ProcessaMensagem
* Funcao...........: Thread que processa as APDUs enviadas para o servidor.
                     
*************************************************************** */

package model;

import java.io.*;
import java.net.*;
import java.lang.Thread;
import controller.TelaGrupoController;
import javafx.application.Platform;

public class ProcessaMensagem extends Thread {
	private String mensagem;

	public ProcessaMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
	@Override
	public void run() {
		try {
			APDU apdu = APDU.decodificarMensagem(mensagem);
			Usuario u = Usuario.buscarUsuarioPorNome(apdu.getUsuario());

			if (apdu.getTipo().equals("SEND") && u != null) {
				Mensagem m = new Mensagem(apdu.getMensagem(), u, apdu.getTempoEnvio());

				for (Grupo g : Usuario.getUsuario().getGrupos()) {
					if (g.getNome().equals(apdu.getGrupo())) {
						g.adicionarMensagem(m);
						break;
					}
				}

				Platform.runLater(() -> TelaGrupoController.grupos.carregarMensagens());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
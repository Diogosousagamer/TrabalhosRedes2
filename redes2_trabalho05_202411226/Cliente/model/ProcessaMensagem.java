/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 24/06/2026
* Ultima alteracao.: 26/06/2026
* Nome.............: ProcessaMensagem
* Funcao...........: Thread que processa as APDUs enviadas para o cliente.
                     
*************************************************************** */

package model;

import controller.TelaGrupoController;
import controller.TelaPrincipalController;
import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.time.LocalDateTime;
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
			String usuario = apdu.getUsuario();
			LocalDateTime tempoEnvio = apdu.getTempoEnvio();

			if (apdu.getTipo().equals("SEND")) {
				Mensagem m = new Mensagem(apdu.getMensagem(), usuario, tempoEnvio);

				for (Grupo g : Usuario.getUsuario().getGrupos()) {
					if (g.getNome().equals(apdu.getGrupo())) {
						g.adicionarMensagem(m);
						break;
					}
				}

				if (TelaGrupoController.grupos != null) {
					Platform.runLater(() -> TelaGrupoController.grupos.carregarMensagens());
				}
				else {
					Platform.runLater(() -> TelaPrincipalController.principal.carregarGrupos());
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
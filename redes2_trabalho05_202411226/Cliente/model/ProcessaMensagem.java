/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 24/06/2026
* Ultima alteracao.: 27/06/2026
* Nome.............: ProcessaMensagem
* Funcao...........: Thread que processa as APDUs enviadas para o cliente
                     no protocolo UDP.
                     
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
	private clienteUDP udp;
	private String mensagem;

	public ProcessaMensagem(clienteUDP udp, String mensagem) {
		this.udp = udp;
		this.mensagem = mensagem;
	}
	
	@Override
	public void run() {
		try {
			APDU apdu = APDU.decodificarMensagem(mensagem);
			String usuario = apdu.getUsuario();
			LocalDateTime tempoEnvio = apdu.getTempoEnvio();

			if ("SEND".equals(apdu.getTipo().trim())) {
				Mensagem m = new Mensagem(apdu.getMensagem(), usuario, tempoEnvio);

				for (Grupo g : Usuario.getUsuario().getGrupos()) {
					if (g.getNome().equals(apdu.getGrupo())) {
						g.adicionarMensagem(m);
						break;
					}
				}

				udp.enviarAPDU(new APDU("CONFIRM", usuario, apdu.getGrupo(), apdu.getMensagem(), apdu.getTempoEnvio(), "DELIVERED"));
			}
			else if (usuario.equals(Usuario.getUsuario().getNome()) && "CONFIRM".equals(apdu.getTipo().trim())) {
				Grupo g = Usuario.getUsuario().buscarGrupo(apdu.getGrupo());

				if (g != null) {
					g.atualizarStatusMensagem(usuario, apdu.formatarTempoEnvio(), apdu.getStatus());
				}
			}

			if (TelaGrupoController.grupos != null) {
				Platform.runLater(() -> TelaGrupoController.grupos.carregarMensagens());
			}
			else {
				Platform.runLater(() -> TelaPrincipalController.principal.carregarGrupos());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
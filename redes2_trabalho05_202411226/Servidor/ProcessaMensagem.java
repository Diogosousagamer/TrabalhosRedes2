/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 17/06/2026
* Ultima alteracao.: 18/06/2026
* Nome.............: ProcessaMensagem
* Funcao...........: Thread que processa as APDUs enviadas para o servidor.
                     
*************************************************************** */

import java.lang.Thread;

public class ProcessaMensagem extends Thread {
	private String mensagem;

	public ProcessaMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
	@Override
	public void run() {
		try {
			APDU apdu = APDU.decodificarMensagem(mensagem);

			System.out.println(apdu.getTipo());
			System.out.println("Usuario: " + apdu.getUsuario());
			System.out.println("Grupo: " + apdu.getGrupo());
			if (!apdu.getMensagem().isEmpty()) System.out.println("Mensagem: " + apdu.getMensagem());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
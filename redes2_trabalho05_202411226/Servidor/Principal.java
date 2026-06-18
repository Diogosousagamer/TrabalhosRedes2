/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/06/2026
* Ultima alteracao.: 18/06/2026
* Nome.............: Principal (Servidor)
* Funcao...........: Aplicativo de Instant Messaging desenvolvido utilizando protocolos
                     da camada de transporte TCP/UDP (Servidor).
                     
*************************************************************** */

public class Principal {
	public static void main(String[] args) throws Exception {
		servidorUDP serverUDP = new servidorUDP();
		servidorTCP serverTCP = new servidorTCP();
		
		serverUDP.start();
		serverTCP.start();
	}
}
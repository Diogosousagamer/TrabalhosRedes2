/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 06/06/2026
* Ultima alteracao.: 19/06/2026
* Nome.............: Principal (Servidor)
* Funcao...........: Aplicativo de Instant Messaging desenvolvido utilizando protocolos
                     da camada de transporte TCP/UDP (Servidor).
                     
*************************************************************** */

/*
   * ***************************************************************
   * Metodo: main
   * Funcao: inicializa os servidores TCP e UDP
   * Parametros: String[] args - vetor contendo argumentos necessarios 
                                 para a inicializacao do programa
   * Retorno: void
   ****************************************************************/

public class Principal {
	public static void main(String[] args) throws Exception {
		servidorUDP serverUDP = new servidorUDP();
		servidorTCP serverTCP = new servidorTCP();
		
		serverUDP.start();
		serverTCP.start();
	}
}
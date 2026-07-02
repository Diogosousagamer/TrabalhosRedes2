/* ***************************************************************
* Autor............: Diogo Oliveira de Sousa
* Matricula........: 202411226
* Inicio...........: 10/06/2026
* Ultima alteracao.: 02/07/2026
* Nome.............: clienteUDP
* Funcao...........: Interface do cliente no protocolo UDP.
                     
*************************************************************** */

package model;

import java.io.*;
import java.net.*;
import java.lang.Thread;

public class clienteUDP extends Thread {
	// Variaveis e instancias
	private byte[] dadosSaida = new byte[1024];
	private ObjectOutputStream saida;
	final int PORTA = 6789;
	private DatagramSocket conexaoCliente;
	private DatagramSocket escuta;
	private InetAddress ipServidor;

  /*
   * ***************************************************************
   * Metodo: run
   * Funcao: executa as operacoes da Thread
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	@Override
	public void run() {
		// O cliente passa a escutar o servidor em uma porta separada
		escutarServidor();
	}

  /*
   * ***************************************************************
   * Metodo: enviarAPDU
   * Funcao: envia uma nova APDU para o servidor
   * Parametros: APDU apdu - APDU a ser enviada
   * Retorno: void
   ****************************************************************/

	public void enviarAPDU(APDU apdu) {
		// Inicio do bloco try/catch
		try {
			// Inicializa uma nova Socket de datagrama (servico sem conexoes)
			conexaoCliente = new DatagramSocket();

			// Obtem o endereco IP da maquina do servidor
			ipServidor = InetAddress.getByName(Usuario.getUsuario().getIpServidor());

			// Vetor que guardara os bytes da mensagem para que possam ser 
			// transportados pelo datagrama
			byte[] dadosSaida = new byte[1024];

      // Obtem a mensagem codificada da APDU
			String mensagemEnviada = (apdu != null) ? apdu.enviarMensagem() : "";

			// Exporta os dados da mensagem com base na codificacao UTF8 (preserva caracteres especiais
		  // para evitar corrupcao quando for transportada para o servidor) 
			dadosSaida = mensagemEnviada.getBytes("UTF-8");

			// Cria um novo pacote com as informacoes obtidas
			DatagramPacket pacoteEnviado = new DatagramPacket(dadosSaida, dadosSaida.length, ipServidor, PORTA);

      // Envia o pacote criado
			conexaoCliente.send(pacoteEnviado);
		}
		catch (Exception e) {
			// Em caso de excecao, emite a pilha de execucao
			// para rastrear a sua origem
			e.printStackTrace();
		} // Fim do bloco try/catch
	}

  /*
   * ***************************************************************
   * Metodo: escutarServidor
   * Funcao: executa as operacoes da Thread
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void escutarServidor() {
		// Inicio do bloco try/catch
		try {		
			// Inicializa uma socket de datagramas (servico sem conexoes) para se comunicar
			// com o servidor UDP
			escuta = new DatagramSocket(6790);
			System.out.println("Cliente UDP escutando na porta 6790.");

      // Inicio do bloco while
      // Enquanto a Thread nao for interrompida e a escuta nao for fechada
			while (!Thread.currentThread().isInterrupted() && !escuta.isClosed()) {
				// Cria um vetor de bytes para receber uma nova mensagem
				byte[] entrada = new byte[1024];

				// Recebe um datagrama encaminhado pela socket
				DatagramPacket datagramaRecebido = new DatagramPacket(entrada, entrada.length);
				escuta.receive(datagramaRecebido);

        // Converte o conteudo do datagrama em String
				String mensagemRecebida = new String(datagramaRecebido.getData(), 0, datagramaRecebido.getLength(), "UTF-8");

				// Processa a mensagem
				ProcessaMensagem p = new ProcessaMensagem(this, mensagemRecebida);
				p.start();
			}
		}
		catch (IOException e) {
			// Em caso de excecao, sinaliza que a escuta foi encerrada abruptamente
			System.err.println("Escuta UDP encerrada abruptamente.");
		} // Fim do bloco try/catch
	}

  /*
   * ***************************************************************
   * Metodo: fecharConexao
   * Funcao: fecha a conexao com o servidor UDP
   * Parametros: nenhum parametro foi definido para esta funcao
   * Retorno: void
   ****************************************************************/

	public void fecharConexao() {
		// Inicio do bloco try/catch
		try {
			// Interrompe a Thread
			this.interrupt();

			// Inicio do bloco if
			if (escuta != null && !escuta.isClosed()) {
				// Fecha a escuta se ela nao for nula e nao tiver sido fechada
				escuta.close();
				escuta = null;
			} // Fim do bloco if

			// Inicio do bloco if
			if (conexaoCliente != null && !conexaoCliente.isClosed()) {
				// Fecha a conexao de envio caso ela nao for nula e nao tiver sido fechada
				conexaoCliente.close();
				conexaoCliente = null;
			} // Fim do bloco if
		}
		catch (Exception e) {
			// Em caso de excecao, emite a pilha de execucao para rastrear a sua origem
			e.printStackTrace();
		} // Fim do bloco try/catch
	}
}
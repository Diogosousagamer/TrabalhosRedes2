# Sobre os Trabalhos
Estes trabalhos foram elaborados como parte da avaliação parcial da disciplina Redes de Computadores II, ministrada pelo professor Marlos Marques durante o V Semestre 2026.1 do curso de Ciências da Computação da Universidade Estadual do Sudoeste da Bahia (UESB). Cada projeto apresentado visa simular os aspectos do funcionamento de uma rede computacional, desde o roteamento até os protocolos de transporte. 

## Trabalho 1 - Roteamento Por Inundação
Esse trabalho visa simular o roteamento por inundação, onde a rede é (literalmente) inundada por pacotes até que se chegue ao destino final, porém com o custo de consumir mais largura de banda. São apresentadas três versões do algoritmo:

* **Versão 1.0**: Cada pacote é enviado para todos os vizinhos do roteador receptor.
* **Versão 2.0**: Cada pacote é enviado para todos os vizinhos do roteador receptor, exceto para o roteador que o encaminhou.
* **Versão 3.0**: Cada pacote é enviado para todos os vizinhos do roteador receptor, exceto para o roteador que o encaminhou, possuindo um tempo de vida limitado para que não vaguem infinitamente na sub rede.
* **Versão 4.0**: Cada pacote é enviado para todos os vizinhos do roteador receptor, exceto para o roteador que o encaminhou, possuindo um tempo de vida limitado para que não vaguem infinitamente na sub rede. Além disso, eles não podem ser encaminhados para roteadores que já foram visitados anteriormente.

![INUNDACAO][inundacao]

O grafo exibido é gerado a partir de um arquivo backbone.txt, que informa a quantidade de nós (roteadores) presentes no grafo e as arestas (links) entre eles. O usuário pode alterá-lo abrindo-o no Bloco de Notas, porém para adicionar mais arestas, siga a formatação predefinida no arquivo:

* A primeira linha corresponde à quantidade de nós do grafo;
* As demais linhas correspondem às arestas do grafo com os seus respectivos custos (ex: A,B,2); os componentes da aresta devem estar separados por vírgula;
* Os roteadores são especificados por caracteres.

![INUNDACAO2][inundacaobackbone]

## Trabalho 2 - Roteamento Pelo Caminho Mais Curto
Esse trabalho visa simular o roteamento pelo caminho mais curto, que se trata de uma aplicação do algoritmo de Dijkstra para determinar o caminho mínimo de uma certa origem até certo destino. O algoritmo funciona da seguinte forma:

* Cada roteador é rotulado como provisório e tendo custo infinito;
* A origem é rotulada com custo 0;
* A partir da origem, os roteadores visitam os seus vizinhos e atualizam os seus custos caso possuírem um caminho de menor custo até eles (custo do roteador + custo da aresta entre o roteador e o vizinho). Aos poucos, cada roteador é rotulado como permanente (não pode ser alterado) e o algoritmo escolhe o roteador com menor custo na lista de roteadores a serem explorados para ser visitado (no caso do trabalho, é escolhido o primeiro roteador na lista de roteadores propensos a visita);
* O caminho é montado a partir dos predecessores de cada roteador, começando pelo destino até chegar na origem, e depois executado.

![DIJKSTRA][dijkstra]

O grafo exibido é gerado a partir de um arquivo backbone.txt, que informa a quantidade de nós (roteadores) presentes no grafo e as arestas (links) entre eles. O usuário pode alterar o arquivo acessando um menu do programa para alterar a sub rede, clicando no botão com a imagem de um grafo. Ao clicar em "Aplicar", o grafo é redesenhado para se adequar às mudanças realizadas, desde que o usuário siga as instruções estipuladas no menu. 

![DIJKSTRA2][dijkstrabackbone]

## Trabalho 3 - Roteamento Por Vetor de Distância
Esse trabalho visa simular o roteamento pelo vetor de distância, onde cada roteador mantém uma tabela de roteamento contendo os melhores caminhos para todos os destinos possíveis (exceto para ele próprio), com cada linha da tabela possuindo uma linha de destino (destino possível), uma linha de saída (roteador usado, direta ou indiretamente, para alcançar o destino), e o custo total do caminho atual. Os roteadores atualizam essas tabelas a partir da troca de informações com os seus vizinhos caso algum deles fornecer um caminho de menor custo para certo destino. 

Ao contrário dos dois primeiros algoritmos, trata-se de um algoritmo dinâmico, levando em consideração as mudanças na carga e na topologia da rede; dessa forma, o usuário pode remover uma aresta entre dois roteadores para refletir essa mudança, bem como alterar o backbone da rede durante a simulação. Além disso, os roteadores funcionam a partir de Threads para promover uma execução mais paralela e realista.

![VETORDISTANCIA][vetordistancia]

O grafo exibido é gerado a partir de um arquivo backbone.txt, que informa a quantidade de nós (roteadores) presentes no grafo e as arestas (links) entre eles. O usuário pode alterar o arquivo acessando um menu do programa para alterar a sub rede, clicando no botão com a imagem de um grafo. Ao clicar em "Aplicar", o grafo é redesenhado para se adequar às mudanças realizadas, desde que o usuário siga as instruções estipuladas no menu. 

OBS: o grafo é direcionado; ou seja, o custo é diferente dependendo da direção do caminho. Isso é usado para representar o retardo de ida (do A ao B) e o retardo de volta (do B ao A). Desse modo, cada aresta possui dois custos diferentes, que são diferenciados a partir de uma função ping(), que lê o arquivo backbone para descobrir o retardo do caminho um roteador para o outro.  

![VETORDISTANCIA2][vetordistanciabackbone]

## Trabalho 4 - Roteamento Por Estado de Enlace
Esse trabalho visa simular o roteamento por estado de enlace, que substituiu o vetor de distância em decorrência de seus problemas de convergência. O algoritmo funciona seguindo estes passos:

* **Conhecer os vizinhos**: os roteadores enviam pacotes Hello para as suas extremidades (arestas) para conhecerem os seus vizinhos.

![ESTADOENLACE][estadoenlace1]

* **Medir os retardos**: Os roteadores enviam pacotes Echo para mensurar os retardos do caminho direto até os seus vizinhos. Ao contrário dos demais algoritmos, os retardos são mensurados randomicamente (através de uma função chamada ps()) e não são informados no arquivo backbone.txt.

![ESTADOENLACE2][estadoenlace2]

* **Distribuir pacotes de estado de enlace**: cada roteador envia pacotes de estado de enlace aos seus vizinhos, que os distribuem para seus respectivos vizinhos, exceto para o roteador que os encaminhou (pode ser diferente da origem) - sendo assim, uma aplicação do algoritmo de inundação. Cada pacote de estado de enlace informa os vizinhos e os seus respectivos custos a partir da origem, possuindo números de sequência para permitir que os roteadores descartem possíveis duplicatas. Os roteadores mantém os pacotes recebidos de cada origem possível (exceto ele próprio) a partir de buffers. Isso é feito para que todos os roteadores estejam cientes de possíveis mudanças na carga da rede, acelerando a convergência.

![ESTADOENLACE3][estadoenlace3]

* **Calcular a melhor rota**: cada roteador, a partir dos aprendizados adquiridos, monta uma matriz de adjacência (ou uma árvore de escoamento) e aplica Dijkstra (caminho mais curto) para calcular a melhor rota para cada destino e preencher as suas tabelas de roteamento.

![ESTADOENLACE4][estadoenlace4]

Desta vez, o usuário terá até 15 segundos para selecionar uma origem e um destino e executar o encaminhamento; caso esse tempo for expirado, a simulação será reiniciada. Além disso, ao remover uma aresta durante a distribuição de pacotes de estado de enlace, os roteadores da aresta iniciam uma nova rodada de pacotes de estado de enlace para informar as mudanças, tendo o seu número de sequência atualizado para serem distinguidos de pacotes antigos. 

O grafo exibido é gerado a partir de um arquivo backbone.txt, que informa a quantidade de nós (roteadores) presentes no grafo e as arestas (links) entre eles. O usuário pode alterar o arquivo acessando um menu do programa para alterar a sub rede, clicando no botão com a imagem de um grafo. Ao clicar em "Aplicar", o grafo é redesenhado para se adequar às mudanças realizadas, desde que o usuário siga as instruções estipuladas no menu.

OBS: o grafo é direcionado; ou seja, o custo é diferente dependendo da direção do caminho. Isso é usado para representar o retardo de ida (do A ao B) e o retardo de volta (do B ao A).

![ESTADOENLACE5][estadoenlacebackbone]

## Trabalho 5 - DsgChat (Aplicativo de Instant Messaging - Cliente/Servidor)

Este trabalho se trata de um aplicativo de mensagens instantâneas (de maneira similar ao WhatsApp, Telegram etc.) que visa demonstrar o funcionamento dos protocolos TCP e UDP da camada de transporte com base na arquitetura Cliente/Servidor - uma máquina (cliente) que solicita serviços para uma outra máquina (servidor), que se encarregará de executá-los. 

![DSGCHAT1][cliente1]

![DSGCHAT2][cliente2]

O protocolo TCP (Transmission Control Protocol) foca em estabelecer um fluxo de bytes confiável sobre uma inter-rede não confiável. Logo, se trata de um serviço orientado a conexões, usado para operações que exigem maior confiabilidade em detrimento do tempo de execução, como entrar e sair em grupos. Já o protocolo UDP, sendo um serviço sem conexões, prioriza a entrega de segmentos com baixa latência e não realiza nenhum controle de confiabilidade, essencial para o envio e recepção imediatos de mensagens. 

Para garantir o pleno funcionamento do trabalho e conforme exigências do professor, foram implementadas quatro APDUs (Application Protocol Data Unit), responsáveis por transportar dados importantes para garantir o bom funcionamento dos protocolos que sustentam a aplicação:

* **JOIN (usuario, grupo):** enviada quando o usuário deseja se juntar a um grupo.
* **LEAVE (usuario, grupo):** enviada quando o usuário deseja sair de um grupo.
* **SEND (usuario, grupo, mensagem, tempoEnvio):** enviada quando o usuário deseja enviar uma mensagem em um grupo. Ao processá-la, o servidor encaminha a mensagem para todos os outros usuários que fazem parte do grupo.
* **CONFIRM (usuario, grupo, mensagem, tempoEnvio, status):** APDU extra solicitada pelo professor, serve para confirmar ao autor da mensagem se ela foi enviada (um traço cinza), entregue a todos os membros do grupo (dois traços cinzas), e lida por todos os membros do grupo (dois traços azuis).

![DSGCHAT3][servidor]

# Glossário - Redes de Computadores 2

Uma rede de computadores refere-se a um conjunto de computadores independentes interconectados por uma tecnologia (fibra óptica, ondas de rádio, etc), possibilitando a troca de informações entre si. Ao contrário de um sistema distribuído, onde os computadores compõem um único sistema, a rede de computadores não possui essa ideia de coletividade, pois as máquinas realizam operações completamente distintas. 

Segue-se uma lista de conceitos necessários para entender os trabalhos:

* **Camada de rede:** camada responsável por gerenciar as operações da sub-rede, estabelecer as "rotas" dos pacotes de uma origem específica até o destino final, controlar o congestionamento de transmissão de pacotes simultâneos e a qualidade do serviço fornecido, e mitigar problemas de transferência de uma rede pra outra.
* **Roteamento:** parte do software da camada de rede que foca em processar e atualizar as tabelas de roteamento, possibilitando o encaminhamento do pacote para o destino final.
* **Encaminhamento:** responsável por determinar as linhas de saída a serem usadas para enviar o pacote para o destino final.
* **Pacote:** unidade de dados que carrega as informações a serem encaminhadas para o destino final.
* **Princípio da otimização:** Se o roteador J estiver entre I e K (um caminho ótimo), então o caminho de J à K também será considerado ótimo.
* **Árvore de escoamento:** árvore que mostra o conjunto de rotas consideradas ótimas a serem seguidas da origem até o destino, tendo como raíz o destino.

<!-- MARKDOWN LINKS & IMAGES -->
[inundacao]: readme-images/Inundacao1.PNG
[inundacaobackbone]: readme-images/InundacaoBackbone.PNG
[dijkstra]: readme-images/Dijkstra.PNG
[dijkstrabackbone]: readme-images/DijkstraBackbone.PNG
[vetordistancia]: readme-images/VetorDistancia.PNG
[vetordistanciabackbone]: readme-images/VetorDistanciaBackbone.PNG
[estadoenlace1]: readme-images/EstadoEnlaceHello.PNG
[estadoenlace2]: readme-images/EstadoEnlaceEcho.PNG
[estadoenlace3]: readme-images/EstadoEnlaceLink.PNG
[estadoenlace4]: readme-images/EstadoEnlaceRota.PNG
[estadoenlacebackbone]: readme-images/EstadoEnlaceBackbone.PNG
[cliente1]: readme-images/DsgChatCliente.PNG
[cliente2]: readme-images/DsgChatCliente2.png
[servidor]: readme-images/DsgChatServidor.png

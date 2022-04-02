Guia de instalação da plataforma ucDrive:
    -> Nesta pasta estão presentes 2 ficheiros .jar, sendo estes o ficheiro ucDrive.jar que funciona 
    como um servidor e o ficheiro terminal.jar que funciona como um terminal de cliente.

    -> Para correr os ficheiros .jar no terminal é necessário executar o seguinte comando:
        java -jar [filename].jar

    -> Para correr os ficheiros .java é necessario compilar todos os ficheiros através do comando:
        javac *.java
    -> De seguida executa-se os seguintes comandos:
        java Server (corre o servidor)
        java Client (corre o terminal do cliente)

    NOTA:
    -> Ambos os ficheiros .jar podem ser corridos sem nenhuma ordem específica, no entanto é recomendado
    correr primeiro o ficheiro ucDrive.jar. Caso corra o ficheiro terminal.jar em primeiro lugar o 
    terminal do cliente irá ficar em espera até ligar o servidor (ucDrive.jar). 


    AVISO:
    -> É necessário que a seguinte hierarquia de ficheiros seja seguida para um bom funcionamento 
    da plataforma:
    
    terminal.jar
    ucDrive.jar
    *.class (qualquer ficheiro .class)
    MainServer
        --- info
            --- usersData.txt
    -> Todos os ficheiros acima têm de estar na mesma diretoria.
    -> É crucial que todos os .jar estejam na mesma diretoria da pasta MainServer e todas as suas subpastas
    e que ao compilar os .java os ficheiros .class gerados estejam nessa mesma diretoria.

    -> No caso de querer adicionar ou alterar um utilizador tem de editar o ficheiro usersData.txt e 
    seguir a seguinte ordem:
        CCnumber	address	pass	department	cell	user	expDate


Comandos a usar pelo cliente:
    passwd -> alterar a password do cliente

    config -main [ip] [port] -> alterar a configuração da ligação do servidor principal
    config -backup [ip] [port] -> alterar a configuração da ligação do servidor secundário

    ls -server -> listar todos os ficheiros e pastas da diretoria do servidor
    ls -client -> listar todos os ficheiros e pastas da diretoria do cliente

    cd -server "directory" -> mudar a diretoria atual do servidor
    cd -client "directory" -> mudar a diretoria atual do cliente
    cd [-server / -client] .. -> muda para a diretoria "pai" da diretoria atual
    (colocar a diretoria dentro de aspas no caso de existirem caracteres "especiais", isto é, espaços, acentos...)

    push [filename] [destination] -> carregar um ficheiro para o servidor
    (o cliente tem de ir para a diretoria onde se encontra o ficheiro a ser carregado)
    
    pull [filename] [destination] -> descarregar um ficheiro do servidor
    (o cliente tem de indicar a diretoria onde quer descarregar o ficheiro)

    mkdir -server [folderName] -> cria uma nova diretoria no servidor

    exit -> termina a ligação do cliente

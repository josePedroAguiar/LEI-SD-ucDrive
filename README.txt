Comandos:
    passwd -> alterar a password do cliente

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

    exit -> termina a ligação do cliente

import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Server {
    // TreeSet<User> tree= new TreeSet<User>(new myNameComparator());
    private  HashSet<User> hs = new HashSet<>();
    private  File myObj;
    public boolean statusMainServer =false;

    private int numero = 0;

    public Server(int serverPortMain,int serverPortBackUp) {
        while (true){
        myObj = new File("./MainServer/info/usersData.txt");
        try (ServerSocket check = new ServerSocket(serverPortBackUp)) {
            readUsersData(); // abre o ficheiro com as infos dos users e guarda toda a info
            System.out.println("Vai fechar");
                check.close();
        
            UDPPingServer t = new UDPPingServer();
            try (ServerSocket listenSocket = new ServerSocket(serverPortMain)) {
                System.out.println("A escuta no porto " + serverPortMain);
                System.out.println("LISTEN SOCKET=" + listenSocket);
                while (true) {
                    Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                    System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
                    numero++;
                    new Connection(clientSocket, hs, numero);
                }
            } catch (IOException e) {
                System.out.println("Listen:" + e.getMessage());
            }

        } catch (IOException e1) {
        UDPPingClient t= new UDPPingClient(this);
        System.out.println("Vai fechar-1");
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
        }
    }

     private void readUsersData() {
        try {
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.length() != 0 && data.charAt(0) != '#') {
                    User user = new User(data);
                    if (user.valid) {
                        user.root = createDir("./home/" + user.username);
                        user.rootServer = createDir("./MainServer/usr/" + user.username);
                        createDir("./MainServer/usr/" + user.username);
                        if (user.currentDir == null)
                            user.currentDir = user.root;
                        if (user.currentDirServer == null)
                            user.currentDirServer = user.rootServer;
                        hs.add(user);
                    }
                }
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

     private Path createDir(String name) {
        Path path = Paths.get(name + "/");
        try {
            Files.createDirectories(path);
            return path;
        } catch (NoSuchFileException e) {
            System.out.println("Parent directory doesn't exist!");
            e.printStackTrace();
        } catch (FileAlreadyExistsException e) {
            System.out.println("Directory already exists!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO " + e.getMessage());
            e.printStackTrace();
        }
        return Paths.get("./home/");
    }

    public static void main(String[] args) {

       
        new Server(6001,6003);

    }

}
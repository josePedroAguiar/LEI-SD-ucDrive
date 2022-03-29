import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Server {
    // TreeSet<User> tree= new TreeSet<User>(new myNameComparator());
    static Path root;
    static Path currentDir;
    static HashSet<User> hs = new HashSet<>();
    static File myObj;

    static private void readUsersData() {
        try {
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.length() != 0 && data.charAt(0) != '#') {
                    User user = new User(data);
                    if (user.valid) {
                        user.root = createDir("./home/" + user.username);
                        createDir(root.toString() + "/usr/" + user.username);
                        if (user.currentDir == null)
                            user.currentDir = user.root;
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

    static private Path createDir(String name) {
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
        int numero = 0;

        int serverPort = 6001;

        root = createDir("./MainServer");

        myObj = new File(Server.root.toString() + "/info/usersData.txt");

        readUsersData(); // abre o ficheiro com as infos dos users e guarda toda a info
        UDPPingServer t = new UDPPingServer();
        try (ServerSocket listenSocket = new ServerSocket(serverPort)) {
            System.out.println("A escuta no porto " + serverPort);
            System.out.println("LISTEN SOCKET=" + listenSocket);
            while (true) {
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
                numero++;
                new Connection(clientSocket, hs, numero);

                if (currentDir == null)
                    currentDir = root;
            }
        } catch (IOException e) {
            System.out.println("Listen:" + e.getMessage());
        }
    }
}
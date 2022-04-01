import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Server {
    // TreeSet<User> tree= new TreeSet<User>(new myNameComparator());
    private HashSet<User> hs;
    private File myObj;
    public boolean statusMainServer = false;
    public boolean statusBackUpServer = true;
    private String path;
    private int numero = 0;
    private File LastDir;
    public ArrayList<String> filesToReplicate ;
    
    public HashSet<User> getHs() {
        return hs;
    }

    public File getLastDir() {
        return LastDir;
    }

    public File getMyObj() {
        return myObj;
    }

    public int getNumero() {
        return numero;
    }

    public String getPath() {
        return path;
    }

    public boolean isStatusMainServer() {
        return statusMainServer;
    }

    public void setLastDir(File lastDir) {
        LastDir = lastDir;
    }

    public void setHs(HashSet<User> hs) {
        this.hs = hs;
    }

    public void setMyObj(File myObj) {
        this.myObj = myObj;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setStatusMainServer(boolean statusMainServer) {
        this.statusMainServer = statusMainServer;
    }

    public Server(int serverPortMain, int serverPortBackUp, String path) {
        filesToReplicate=new ArrayList<>();
        synchronized(filesToReplicate){
        setPath(path);            
        while (true) {
           
            setHs(new HashSet<>());
            setMyObj(new File("./" + path + "/info/usersData.txt"));
            setLastDir(new File("./" + path + "/info/lastDirs.txt"));

            

            try (ServerSocket check = new ServerSocket(serverPortBackUp)) {
                readUsersData(); // abre o ficheiro com as infos dos users e guarda toda a info
                check.close();

                new UDPPingServer(this);
                new SendFile(this);
              

                try (ServerSocket listenSocket = new ServerSocket(serverPortMain)) {
                    System.out.println("A escuta no porto " + serverPortMain);
                    System.out.println("LISTEN SOCKET=" + listenSocket);
                    while (true) {
                        Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                        System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
                        this.numero++;
                        new Connection(clientSocket, hs, numero,this);
                    }
                } catch (IOException e) {
                    System.out.println("Listen:" + e.getMessage());
                }

            } catch (IOException e1) {
                readUsersData();
                UDPPingClient t = new UDPPingClient(this);
                ReceiveFile receivedT = new ReceiveFile();
                receivedT.start();
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                    if (user.isValid() && !CompareUsername(user.getUsername())) {
                        user.setRoot(createDir("./home/" + user.getUsername()));
                        user.setRootServer(createDir("./" + this.path + "/usr/" + user.getUsername()));
                        createDir("./" + this.path + "/usr/" + user.getUsername());
                        if (!readLastDir(user)) {
                            user.setCurrentDir(user.getRoot());
                            user.setCurrentDirServer(user.getRootServer());
                        }
                        hs.add(user);
                    } else {
                        System.out.println("Utilizador inválido!");
                    }
                }
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private boolean readLastDir(User u) {
        if (getLastDir().exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(getLastDir()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.length() != 0 && line.charAt(0) != '#') {
                        String[] info = line.split("\t");
                        if (u.getUsername().equals(info[0])) {
                            u.setCurrentDir(Paths.get(info[1]));
                            u.setCurrentDirServer(Paths.get(info[2]));
                            return true;
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // o ficheiro ainda nao existe, logo ainda nao existe diretoria anterior
        return false;
    }

    private boolean CompareUsername(String name) {
        for (User u : this.hs) {
            if (u.getUsername().equals(name))
                return true;
        }
        return false;
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
        new Server(6001, 6003, "MainServer");
    }

}
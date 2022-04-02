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

class Cmd extends Thread {
    public boolean flagHideOrShow = false;
    UDPPingClient ping;
    Scanner sc = new Scanner(System.in);

    public Cmd(UDPPingClient ping) {
        this.ping = ping;

    }

    public void run() {
        try {
            do {
                String hideOrShow;
                if ((hideOrShow = sc.nextLine()).equals("")) {
                    hideOrShow = hideOrShow.toLowerCase();
                    if (hideOrShow.equals("hide") || hideOrShow.equals("h"))
                        ping.flagHideOrShow = true;
                    else
                        ping.flagHideOrShow = false;

                }
            } while (true);
        } catch (Exception e) {
            System.out.println("Exception handled");
        }
    }
}

public class Server {
    // TreeSet<User> tree= new TreeSet<User>(new myNameComparator());
    private HashSet<User> hs;
    private File myObj;
    public boolean statusMainServer = false;
    public boolean statusBackUpServer = true;
    private String path;
    private int numero = 0;
    private File LastDir;
    public ArrayList<String> filesToReplicate;
    public String addressMain;
    public String addressBackUp;
    public int portMain;
    public int portBackUp;

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

    public Server(int serverPortMain, int serverPortBackUp, String addressMain, String addressBackUp, String path) {
        portMain = serverPortMain;
        portBackUp = serverPortBackUp;
        this.addressMain = addressMain;
        this.addressBackUp = addressBackUp;
        filesToReplicate = new ArrayList<>();
        synchronized (filesToReplicate) {
            setPath(path);
            int count = 0;
            while (true) {

                setHs(new HashSet<>());
                setMyObj(new File("./" + path + "/info/usersData.txt"));
                setLastDir(new File("./" + path + "/info/lastDirs.txt"));

                try (ServerSocket check = new ServerSocket(serverPortBackUp)) {
                    readUsersData(); // abre o ficheiro com as infos dos users e guarda toda a info
                    check.close();
                    UDPPingServer t1 = new UDPPingServer(this);
                    SendFile t2 = new SendFile(this);
                    try (ServerSocket listenSocket = new ServerSocket(serverPortMain)) {

                        count = 0;
                        System.out.println("Listening in port " + serverPortMain);
                        System.out.println("LISTEN SOCKET=" + listenSocket);
                        while (true) {
                            Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                            System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
                            this.numero++;
                            new Connection(clientSocket, hs, numero, this);
                        }
                    } catch (IOException e) {
                        t1.interrupt();
                        t2.interrupt();
                        count++;
                        System.out.println("Listen:" + e.getMessage());
                        return;
                    }

                } catch (IOException e1) {
                    readUsersData();
                    UDPPingClient t = new UDPPingClient(this);
                    ReceiveFile receivedT = new ReceiveFile();
                    receivedT.start();
                    t.start();
                    count++;
                    try {
                        t.join();
                        receivedT.ds.close();
                        receivedT.interrupt();
                    } catch (InterruptedException e) {
                        t.interrupt();

                    }
                }
                System.out.println(count);
                if (count == 2) {

                    break;
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
                        System.out.println("Invalid User!");
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

        int[] serversocket = { 0, 0 };
        String address = "";
        String address2 = "";
        char[] array = new char[100];

        try {
            // Creates a reader using the FileReader
            FileReader input = new FileReader("./config.txt");

            // Reads characters
            input.read(array);

            String[] arrofStr = String.valueOf(array).split("\\n");
            // Closes the reader
            if (arrofStr.length != 4) {
                input.close();
                System.exit(1);
            } else {
                try {
                    int portMain = Integer.parseInt(arrofStr[0]);
                    int portSecond = Integer.parseInt(arrofStr[1]);
                    serversocket[0] = portMain;
                    serversocket[1] = portSecond;
                    address = arrofStr[2];
                    address2 = arrofStr[3];
                } catch (Exception e) {
                    System.out.println("Invalid config file");
                    System.exit(1);
                }
            }
            input.close();
        }

        catch (Exception e) {
            e.getStackTrace();
        }
        new Server(serversocket[0], serversocket[1], address, address2, "MainServer");
        new Server(serversocket[1], serversocket[0], address, address2, "ServerBack");
    }

}
// TCPServer2.java: Multithreaded server

import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/*class myNameComparator implements Comparator<User>
{
	public int compare(User s1, User s2)
	{
		return s1.username.compareTo(s2.username);
	}
}*/
class RandomString {
    // https://www.geeksforgeeks.org/generate-random-string-of-given-size-in-java/
    // function to generate a random string of length n
    static String getAlphaNumericString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index = (int) (AlphaNumericString.length() * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }
}

public class Server {
    // TreeSet<User> tree= new TreeSet<User>(new myNameComparator());
    static Path ServerDir;
    static Path currentDir;

    public static void main(String[] args) {
        int numero = 0;

        int serverPort = 6001;
        try (ServerSocket listenSocket = new ServerSocket(serverPort)) {
            System.out.println("A escuta no porto " + serverPort);
            System.out.println("LISTEN SOCKET=" + listenSocket);

            while (true) {
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
                numero++;
                Connection c = new Connection(clientSocket, numero);
                ServerDir = c.createDir("MainServer");
                currentDir = ServerDir;
            }
        } catch (IOException e) {
            System.out.println("Listen:" + e.getMessage());
        }
    }

    public static void setDirectory(Path newPath) {
        Server.ServerDir = newPath;
    }
}

class User {
    Path dir;
    long ccNumber;
    boolean athetication = false;
    boolean valid;
    String address;
    String pass;
    String department;
    long cellNumber;
    String username;
    Data expDate;
    Path currentDir;

    public User(String data) {
        String[] arrOfStr = data.split("\\t");
        // System.out.println(arrOfStr.length);
        if (arrOfStr.length == 7) {

            address = arrOfStr[1];
            pass = arrOfStr[2];
            department = arrOfStr[3];
            username = arrOfStr[5];
            try {
                ccNumber = Long.parseLong(arrOfStr[0]);
                cellNumber = Long.parseLong(arrOfStr[4]);
                String[] date = arrOfStr[6].split("/");
                if (date.length == 3)
                    expDate = new Data(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
                else {
                    System.out.println("ERROR: Date is invalid (i.e.: DD/M/YY-numeric)");
                    valid = false;
                    return;
                }

            } catch (NumberFormatException e) {
                System.out.println("ERROR: Data(CC-Number,Phone-Number,Date) of " + username + " is invalid");
                valid = false;
                return;
            }
            valid = true;
        } else {
            valid = false;
        }

    }

    public void setDirectory(Path newPath) {
        this.dir = newPath;
    }

    public User() {

    }
}

// = Thread para tratar de cada canal de comunicação com um cliente
class Connection extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    int thread_number;
    HashSet<User> hs = new HashSet<>();
    File myObj = new File("usersData.txt");

    public Connection(Socket aClientSocket, int numero) {
        readUsersData(); // abre o ficheiro com as infos dos users e guarda toda a info

        thread_number = numero;
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    // =============================
    public void run() {
        try {

            User currentUser = authentication(new User()); // autentica um novo utilizador

            // an echo server
            while (true)
                Menu(currentUser); // envia o menu para os clientes

        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
            updateFile();
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
            updateFile();
        }
    }

    public Path createDir(String name) {
        Path path = Paths.get("./home/" + name + "/");
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

    private void readUsersData() {
        try {
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.length() != 0 && data.charAt(0) != '#') {
                    User user = new User(data);
                    if (user.valid) {
                        user.dir = createDir(user.username);
                        user.currentDir = user.dir;
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

    private void Menu(User currentUser) throws IOException {
        String opt = in.readUTF();
        System.out.println("Command: " + opt);
        if ("passwd".equals(opt)) {
            changePass(currentUser);
            // depois de mudar a passe fecha a ligacao e pede uma nova autenticacao

        } else if ("ls -server".equals(opt)) {
            System.out.println("List Server Directory " + Server.ServerDir.toString());
            String list = listFiles(Server.ServerDir, 0, "");
            out.writeUTF(list);
        } else if (opt.contains("cd -server")) {
            String[] command = opt.split(" ");
            Path newPath = changeCurrentDir(Server.currentDir, command[2]);
            if (newPath.compareTo(Server.currentDir) == 0) {
                out.writeUTF("Diretoria inexistente\n");
            } else {
                Server.currentDir = newPath;
                out.writeUTF("Diretoria atualizada");
            }

        } else if ("ls -client".equals(opt)) {
            System.out.println("List " + currentUser.username + " Directory " + currentUser.dir.toString());
            String list = listFiles(currentUser.dir, 0, "");
            out.writeUTF(list);
        } else if (opt.contains("cd -client")) {
            String[] command = opt.split(" ");
            Path newPath = changeCurrentDir(currentUser.currentDir, command[2]);
            if (newPath.compareTo(currentUser.currentDir) == 1) {
                out.writeUTF("Diretoria inexistente\n");
            } else {
                currentUser.currentDir = newPath;
                out.writeUTF("Diretoria atualizada\n");
            }
        } else if ("exit".equals(opt)) {
            clientSocket.close();
        }
    }

    private User authentication(User currentUser) throws IOException {
        while (!currentUser.athetication) {
            String received = in.readUTF();
            String[] data = received.split("\\t");
            if (data.length == 2) {
                for (User h : hs) {
                    currentUser = h;
                    if (currentUser.username.equals(data[0]) && currentUser.pass.equals(data[1])) {
                        currentUser.athetication = true;
                        break;
                    }
                    // System.out.println(data[1]);

                }
                if (currentUser.username.equals(data[0]) && currentUser.pass.equals(data[1])) {
                    continue;
                }
            }
            System.out.println("T[" + thread_number + "]: Utilizador nao autenticado.");
            out.writeUTF("Tenta outra vez");
        }

        out.writeUTF("Login com sucesso|" + RandomString.getAlphaNumericString(10) + currentUser.username);
        return currentUser;
    }

    private void changePass(User currentUser) throws IOException {
        try {
            out.writeUTF("Nova password: ");
            while (true) {

                String newPass = in.readUTF();
                if (!newPass.equals(currentUser.pass)) {
                    currentUser.pass = newPass;
                    updateFile();
                    out.writeUTF("Password atualizada!\n");
                    break;
                }
                out.writeUTF("Password ja utilizada!\nNova password: ");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String listFiles(Path dir, int level, String list) throws IOException {
        File f = new File(dir.toString());
        File[] files = f.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                list += ".".repeat(level * 3) + " " + file.getName() + "\n";
            } else {
                list += ".".repeat(level * 3) + " " + file.getName() + "\n";
                list = listFiles(Paths.get(file.getAbsolutePath()), level + 1, list);
            }
        }
        return list;
    }

    private Path changeCurrentDir(Path Dir, String newDir) {
        String currentDir = Dir.toString();
        Path newPath = Paths.get(currentDir + "/" + newDir + "/");
        if (Files.exists(newPath))
            return newPath;
        return Dir;
    }

    private void updateFile() {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(myObj))) {
            Iterator<User> iter = hs.iterator();
            br.write("#user settings\n#CCnumber\taddress\tpass\tdepartment\tcell\tuser\texpDate\n\n");
            while (iter.hasNext()) {
                User u = iter.next();
                String date = u.expDate.toString();
                String info = u.ccNumber + "\t" + u.address + "\t" + u.pass + "\t" + u.department + "\t" + u.cellNumber
                        + "\t" + u.username + "\t" + date + "\n";
                br.write(info);
            }
            System.out.println(myObj.getName() + " atualizado com sucesso!");
        } catch (FileNotFoundException ex) {
            // file does not exist
            System.out.println("File not found!");
        } catch (IOException ex) {
            // I/O error
            System.out.println("IO:" + ex);
        }
    }
}
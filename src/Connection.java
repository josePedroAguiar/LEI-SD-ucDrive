import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

// = Thread para tratar de cada canal de comunicação com um cliente
class Connection extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private Socket clientSocket;
    private int thread_number;
    private HashSet<User> hs;
    private File myObj;
    // ArrayList<Path> filesToReplicate;

    public Connection(Socket aClientSocket, HashSet<User> hs, int numero, String path) {
        this.myObj = new File("./" + path + "/info/usersData.txt");
        this.hs = hs;

        this.thread_number = numero;
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

    private void Menu(User currentUser) throws IOException {
        String opt = in.readUTF();
        System.out.println("Command: " + opt);
        if (currentUser.isAuthenticated()) {
            if ("passwd".equals(opt)) {
                changePass(currentUser);
                currentUser.setAuthenticated(false);
                // depois de mudar a passe fecha a ligacao e pede uma nova autenticacao

            } else if ("ls -server".equals(opt)) {
                System.out.println("List Server directory " + currentUser.getCurrentDirServer().toString());
                String list = listFiles(currentUser.getCurrentDirServer(), 0, "") + "\n";
                out.writeUTF(list);
            } else if (opt.contains("cd -server")) {
                String[] command;
                Path destination;

                if (opt.contains("\"")) { // tratamento para carateres especiais
                    command = opt.split("\"");
                    destination = changeCurrentDir(currentUser.getCurrentDirServer(), command[1],
                            currentUser.getRootServer());
                } else {
                    command = opt.split(" ");
                    destination = changeCurrentDir(currentUser.getCurrentDirServer(), command[2],
                            currentUser.getRootServer());
                }
                if (destination.compareTo(currentUser.getCurrentDirServer()) == 0) {
                    out.writeUTF("Impossivel aceder a essa diretoria\n");
                } else {
                    currentUser.setCurrentDirServer(destination);
                    out.writeUTF("Diretoria atualizada");
                    System.out.println(currentUser.getCurrentDirServer().toString());
                }

            } else if ("ls -client".equals(opt)) {
                System.out.println(
                        "List " + currentUser.getUsername() + " directory " + currentUser.getCurrentDir().toString());
                String list = listFiles(currentUser.getCurrentDir(), 0, "") + "\n";
                out.writeUTF(list);
            } else if (opt.contains("cd -client")) {
                String[] command;
                Path destination;

                if (opt.contains("\"")) { // tratamento para carateres especiais
                    command = opt.split("\"");
                    destination = changeCurrentDir(currentUser.getCurrentDir(), command[1], currentUser.getRoot());
                } else {
                    command = opt.split(" ");
                    destination = changeCurrentDir(currentUser.getCurrentDir(), command[2], currentUser.getRoot());
                }
                if (destination.compareTo(currentUser.getCurrentDir()) == 0 || !Files.isDirectory(destination)) {
                    out.writeUTF("Impossivel aceder a essa diretoria\n");
                } else {
                    currentUser.setCurrentDir(destination);
                    out.writeUTF("Diretoria atualizada\n");
                    System.out.println(currentUser.getCurrentDir().toString());
                }
            } else if (opt.contains("pull")) {
                String[] ss = opt.split(" ");
                String filename = ss[1];
                String destination = ss[2];

                File f = new File(currentUser.getCurrentDirServer().toString() + "/" + filename);
                if (f.exists()) {
                    out.writeUTF(currentUser.getCurrentDirServer().toString() + "/" + filename);
                    File fileD = new File(currentUser.getCurrentDir().toString() + "/" + destination);
                    fileD.createNewFile();
                    out.writeUTF(fileD.getAbsolutePath());
                    new Upload(currentUser.getCurrentDirServer().toString() + "/" + filename, clientSocket);

                } else {
                    out.writeUTF("O ficheiro nao existe na diretoria atual\n");
                }
            } else if (opt.contains("push")) {
                String[] ss = opt.split(" ");
                String filename = ss[1];
                String destination = ss[2];

                File f = new File(currentUser.getCurrentDir().toString() + "/" + filename);

                if (f.exists()) {
                    File fileD = new File(currentUser.getCurrentDirServer().toString() + "/" + destination);
                    out.writeUTF(currentUser.getCurrentDir().toString() + "/" + filename);
                    fileD.createNewFile();
                    int port = in.readInt();
                    new Download(currentUser.getCurrentDir().toString() + "/" + filename, fileD.getAbsolutePath(),
                            port);
                } else {
                    out.writeUTF("O ficheiro nao existe na diretoria atual\n");
                    SendFile t = new SendFile();
                    t.start();
                }

            } else if (opt.contains("mkdir -server")) {
                String[] arg = opt.split(" ");
                if (arg.length == 3) {
                    createDir(currentUser.getCurrentDirServer().toString() + "/" + arg[2], currentUser);
                    System.out.println("Directory was created");
                    out.writeUTF("Directory was created");

                } else {
                    out.writeUTF("Arguments invalid (mkdir -server <path>)");
                }
            } else if ("exit".equals(opt)) {
                clientSocket.close();
                currentUser.setAuthenticated(false);
            }

        }
    }

    private void createDir(String name, User currentUser) throws IOException {
        Path path = Paths.get(name);
        try {
            Files.createDirectories(path);
        } catch (NoSuchFileException e) {
            out.writeUTF("Parent directory doesn't exist!");
            e.printStackTrace();
        } catch (FileAlreadyExistsException e) {
            out.writeUTF("Directory already exists!");
            e.printStackTrace();
        }
    }

    private User authentication(User currentUser) throws IOException {
        while (!currentUser.isAuthenticated()) {
            String received = in.readUTF();
            String[] data = received.split("\\t");
            if (data.length == 2) {
                for (User h : hs) {
                    currentUser = h;
                    if (currentUser.getUsername().equals(data[0]) && currentUser.getPass().equals(data[1])) {
                        currentUser.setAuthenticated(true);
                        break;
                    }
                }
                if (currentUser.getUsername().equals(data[0]) && currentUser.getPass().equals(data[1])) {
                    continue;
                }
            }
            System.out.println("T[" + thread_number + "]: Utilizador nao autenticado.");
            out.writeUTF("Tenta outra vez");
        }

        out.writeUTF("Login com sucesso|" + RandomString.getAlphaNumericString(10) + currentUser.getUsername());
        return currentUser;
    }

    private void changePass(User currentUser) throws IOException {
        try {
            out.writeUTF("Nova password: ");
            while (true) {

                String newPass = in.readUTF();
                if (!newPass.equals(currentUser.getPass())) {
                    currentUser.setPass(newPass);
                    updateFile();
                    out.writeUTF("Password atualizada!\n");
                    clientSocket.close();
                    break;
                }
                out.writeUTF("Password ja utilizada!\nNova password: ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String listFiles(Path root, int level, String list) throws IOException {
        DirectoryStream<Path> stream = Files.newDirectoryStream(root);

        for (Path p : stream) {
            if (Files.isRegularFile(p)) {
                list += ".".repeat(level * 3) + ". " + p.getFileName().toString() + "\n";
            } else {
                list += ".".repeat(level * 3) + ". " + p.getFileName().toString() + "\n";
                list = listFiles(p, level + 1, list);
            }
        }
        return list;
    }

    private Path changeCurrentDir(Path dir, String newDir, Path root) {
        if (newDir.equals("..")) {
            if (dir.compareTo(root) == 0)
                return dir;

            File f = new File(dir.toString());
            dir = Paths.get(f.getParent());
            return dir;
        }

        Path destination = Paths.get(dir.toString() + "/" + newDir);
        if (Files.exists(destination) && Files.isDirectory(destination))
            return destination;
        return dir;
    }

    private void updateFile() {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(myObj))) {
            Iterator<User> iter = hs.iterator();
            br.write("#user settings\n#CCnumber\taddress\tpass\tdepartment\tcell\tuser\texpDate\n\n");
            while (iter.hasNext()) {
                User u = iter.next();
                String date = u.getExpDate().toString();
                String info = u.getCcNumber() + "\t" + u.getAddress() + "\t" + u.getPass() + "\t" + u.getDepartment()
                        + "\t" + u.getCellNumber()
                        + "\t" + u.getUsername() + "\t" + date + "\n";
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
import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

// = Thread para tratar de cada canal de comunicação com um cliente
class Connection extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    int thread_number;
    HashSet<User> hs;
    File myObj = new File(Server.root.toString() + "\\info\\usersData.txt");

    public Connection(Socket aClientSocket, HashSet<User> hs, int numero) {
        this.hs = hs;

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

    private void Menu(User currentUser) throws IOException {
        String opt = in.readUTF();
        System.out.println("Command: " + opt);

        if ("passwd".equals(opt)) {
            changePass(currentUser);
            // depois de mudar a passe fecha a ligacao e pede uma nova autenticacao

        } else if ("ls -server".equals(opt)) {
            System.out.println("List Server directory " + Server.root.toString());
            String list = listFiles(Server.root, 0, "") + "\n";
            out.writeUTF(list);
        } else if (opt.contains("cd -server")) {
            String[] command;
            Path destination;

            if (opt.contains("\"")) { // tratamento para carateres especiais
                command = opt.split("\"");
                destination = changeCurrentDir(Server.currentDir, command[1]);
            } else {
                command = opt.split(" ");
                destination = changeCurrentDir(Server.currentDir, command[2]);
            }
            if (destination.compareTo(Server.currentDir) == 0) {
                out.writeUTF("Impossivel aceder a essa diretoria\n");
            } else {
                Server.currentDir = destination;
                out.writeUTF("Diretoria atualizada");
                System.out.println(Server.currentDir.toString());
            }

        } else if ("ls -client".equals(opt)) {
            System.out.println("List " + currentUser.username + " directory " + currentUser.root.toString());
            String list = listFiles(currentUser.root, 0, "") + "\n";
            out.writeUTF(list);
        } else if (opt.contains("cd -client")) {
            String[] command;
            Path destination;

            if (opt.contains("\"")) { // tratamento para carateres especiais
                command = opt.split("\"");
                destination = changeCurrentDir(currentUser.currentDir, command[1]);
            } else {
                command = opt.split(" ");
                destination = changeCurrentDir(currentUser.currentDir, command[2]);
            }
            if (destination.compareTo(currentUser.currentDir) == 0 || !Files.isDirectory(destination)) {
                out.writeUTF("Impossivel aceder a essa diretoria\n");
            } else {
                currentUser.currentDir = destination;
                out.writeUTF("Diretoria atualizada\n");
                System.out.println(currentUser.currentDir.toString());
            }
        } else if (opt.contains("sftp-get")) {
            //String filename = opt.split(" ")[1];
            //downloadFile(filename, currentUser);

        } else if (opt.contains("sftp-put")) {
            String filename = opt.split(" ")[1];
            String destination = Server.root.toString()
            + Paths.get(filename).toString().replace(currentUser.currentDir.toString(), "\\usr\\" + currentUser.username);
            new Download(filename, currentUser, destination);

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

    public String listFiles(Path root, int level, String list) throws IOException {
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

    private Path changeCurrentDir(Path dir, String newDir) {
        String currentDir = dir.toString();
        if (newDir.equals("..")) {
            if (Server.currentDir.compareTo(Server.root) == 0)
                return dir;

            File f = new File(Server.currentDir.toString());
            Path destination = Paths.get(f.getParent());
            return destination;
        }

        Path destination = Paths.get(currentDir + "/" + newDir);
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
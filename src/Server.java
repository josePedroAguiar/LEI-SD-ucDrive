
// TCPServer2.java: Multithreaded server

import java.net.*;
import java.io.*;
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
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}

public class Server {
    // TreeSet<User> tree= new TreeSet<User>(new myNameComparator());

    public static void main(String[] args) {
        int numero = 0;

        int serverPort = 6001;
        try (ServerSocket listenSocket = new ServerSocket(serverPort)) {
            System.out.println("A escuta no porto 6000");
            System.out.println("LISTEN SOCKET=" + listenSocket);
            while (true) {
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
                numero++;
                new Connection(clientSocket, numero);
            }
        } catch (IOException e) {
            System.out.println("Listen:" + e.getMessage());
        }
    }
}

class User {
    long ccNumber;
    boolean athetication = false;
    boolean valid;
    String address;
    String pass;
    String department;
    long cellNumber;
    String username;
    Data expDate;

    public User(String data) {
        String[] arrOfStr = data.split("\\t");
        System.out.println(arrOfStr.length);
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
                    System.out.println("ERROR:Date is invalid (i.e.: DD/M/YY-numeric)");
                    valid = false;
                    return;
                }

            } catch (NumberFormatException e) {
                System.out.println("ERROR:Data(CC-Number,Phone-Number,Date) of " + username + " is invalid");
                valid = false;
                return;
            }
            valid = true;
        } else {
            valid = false;
        }

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

    public Connection(Socket aClientSocket, int numero) {
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
        HashSet<User> hs = new HashSet<>();
        File myObj = new File("usersData.txt");
        try {
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.length() != 0 && data.charAt(0) != '#') {
                    User user = new User(data);
                    if (user.valid)
                        hs.add(user);
                }
            }
            /*
             * Iterator<User> iter = hs.iterator();
             * while (iter.hasNext()) {
             *
             * // Printing all elements inside objects
             * System.out.println(iter.next().username);
             * }
             */
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            while (true) {
                User currentUser = new User();
                while (!currentUser.athetication) {
                    String received = in.readUTF();
                    String[] data = received.split("\\t");
                    if (data.length == 2) {
                        Iterator<User> iter = hs.iterator();
                        while (iter.hasNext()) {
                            currentUser = iter.next();
                            if (currentUser.username.equals(data[0]) && currentUser.pass.equals(data[1])) {
                                currentUser.athetication = true;
                                break;
                            }
                            System.out.println(data[1]);

                        }
                        if (currentUser.username.equals(data[0]) && currentUser.pass.equals(data[1])) {
                            continue;
                        }
                    }
                    System.out.println("T[" + thread_number + "] Recebeu: " + received);
                    out.writeUTF("Tenta outra vez");
                }

                out.writeUTF("Login com sucesso|" + RandomString
                        .getAlphaNumericString(10) + currentUser.username);

                // an echo server
                String menu = """
                         **********MENU**********
                         1- ALTERAR PASSWORD
                         2- CONFIG IP E PORTOS
                         3- LISTAR FICHEIROS DA DIRETORIA DO SERVIDOR
                         4- MUDAR DIRETORIA DO SERVIDOR
                         5- LISTAR FICHEIROS DA DIRETORIA DO CLIENTE
                         6- MUDAR DIRETORIA DO CLIENTE
                         7- DESCARREGAR FICHEIRO
                         8- CARREGAR FICHEIRO
                         0- SAIR
                         OPCAO:""";

                out.writeUTF(menu);

                String opt = in.readUTF();
                System.out.println("Opcao: " + opt);
                if ("1".equals(opt)) {
                    changePass(currentUser);
                } else {
                    updateFile(myObj, hs);
                    clientSocket.close();
                }
            }
        } catch (EOFException e) {
            updateFile(myObj, hs);
            System.out.println("EOF:" + e);
        } catch (IOException e) {
            updateFile(myObj, hs);
            System.out.println("IO:" + e);
        }
    }

    private void changePass(User currentUser) throws IOException {

        while (true) {
            out.writeUTF("Nova password: ");
            String newPass = in.readUTF();
            if (!newPass.equals(currentUser.pass)) {
                currentUser.pass = newPass;
                out.writeUTF("Password atualizada!\n");
                break;
            }
            out.writeUTF("Password ja utilizada!\nNova password: ");
        }

    }

    private void updateFile(File myObj, HashSet<User> hs) {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(myObj))) {
            Iterator<User> iter = hs.iterator();
            br.write("#user settings\n#CCnumber\taddress\tpass\tdepartment\tcell\tuser\texpDate\n\n");
            while (iter.hasNext()) {
                User u = iter.next();
                String date = u.expDate.toString();
                String info = u.ccNumber + "\t" + u.address + "\t" + u.pass + "\t" + u.department + "\t" + u.cellNumber + "\t" + u.username + "\t" + date + "\n";
                br.write(info);
            }
            System.out.println(myObj.getName() + " atualizado com sucesso!");
        } catch (FileNotFoundException ex) {
            // file does not exist
        } catch (IOException ex) {
            // I/O error
        }
    }
}
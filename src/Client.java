import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.FileReader;

public class Client {
    private static DataInputStream in;
    private static DataOutputStream out;
    private static Socket s;
    private static boolean repeat_login = false;
    private static Scanner sc = new Scanner(System.in);

    private static void run() throws IOException {
        repeat_login = false;
        s.setSoTimeout(1000);
        System.out.println("SOCKET=" + s);

        // 2o passo
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());

        // 3o passo
        String resposta;
        String[] respostaAndToken;
        do {
            System.out.print("Username: ");
            String texto = sc.nextLine();
            System.out.print("Password: ");
            texto += "\t" + sc.nextLine();
            out.writeUTF(texto);
            resposta = in.readUTF();
            respostaAndToken = resposta.split("\\|");
            System.out.println(respostaAndToken[0] + "\n");
        } while (!respostaAndToken[0].equals("Login with success!"));

        while (Menu())
            ;
    }

    public static void main(String[] args) {
        // 1o passo - criar socket

        int[] serversocket = { 0, 0 };
        String address = "";
        String address2 = "";
        char[] array = new char[100];

        try {
            // Creates a reader using the FileReader
            FileReader input = new FileReader("./home/config.txt");

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

        int count = 0;
        while (true) {
            do {
                try {
                    s = new Socket(address, serversocket[0]);
                    run();
                    count = 0;

                } catch (UnknownHostException e) {
                    System.out.println("Sock:" + e.getMessage());
                } catch (EOFException e) {
                    System.out.println("EOF:" + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("IO:" + e.getMessage());
                    count++;

                    try {
                        s = new Socket(address2, serversocket[1]);
                        run();
                        count = 0;
                    } catch (IOException e1) {
                        System.out.println("IO:" + e.getMessage());
                        count++;
                    }
                }
            } while (repeat_login);
            if (count > 3) {
                System.out.println("Impossible to connect to ucDrive!");
                break;
            }
        }
    }

    private static boolean Menu() throws IOException {
        String opt = sc.nextLine();

        out.writeUTF(opt);

        if ("passwd".equals(opt)) {
            String message = in.readUTF();
            System.out.print(message);
            String[] data;
            do {
                String pass = sc.next();
                out.writeUTF(pass);

                data = in.readUTF().split("\n");
                if (data.length == 2)
                    System.out.print(data[0] + "\n" + data[1]);
            } while (!data[0].equals("Password updated!"));
            System.out.println(data[0]);
            s.close();
            repeat_login = true;
            System.out.println("Session ended successfully!\n");
            return false;
            // depois de mudar a passe pede uma nova autenticacao
            // System.out.println("olaola");
        } else if (opt.contains("config")) {
            String s = in.readUTF();
            System.out.println(s);
        } else if ("ls -server".equals(opt) || "ls -client".equals(opt)) {
            String[] list = in.readUTF().split("\n");
            for (String line : list)
                System.out.println(line);
        } else if (opt.contains("cd -server") || opt.contains("cd -client")) {
            String message = in.readUTF();
            System.out.println(message);

        } else if (opt.contains("pull")) {
            String input = in.readUTF();

            if (input.equals("File doesn't exist in that directory\n") || input.equals("Invalid command\n")) {
                System.out.println(input);
            } else {
                String m = in.readUTF();
                int port = in.readInt();
                new Download(input, m, port);

            }
        } else if (opt.contains("push")) {
            String input = in.readUTF();

            if (input.equals("File doesn't exist in that directory\n") || input.equals("Invalid command\n")) {
                System.out.println(input);
            } else {
                new Upload(input, s);
            }

        } else if (opt.contains("mkdir -server")) {
            String input = in.readUTF();
            System.out.println(input);

        } else if (opt.equals("exit")) {
            System.out.println("Session ended successfully!\n");
            s.close();
            System.exit(0);
        }
        return true;
    }

}
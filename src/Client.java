import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client {
    static DataInputStream in;
    static DataOutputStream out;

    public static void main(String[] args) {
        // 1o passo - criar socket
        int serversocket = 6001;
        try (Socket s = new Socket("localhost", serversocket)) {
            System.out.println("SOCKET=" + s);

            // 2o passo
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());

            // 3o passo
            try (Scanner sc = new Scanner(System.in)) {
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
                } while (!respostaAndToken[0].equals("Login com sucesso"));

                while(true)
                    receiveMenu();
            }

        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        }
    }

    private static void receiveMenu() throws IOException {
        // Receive Menu Options
        String menu = in.readUTF(); // da erro aqui quando volta a receber o menu
        System.out.print(menu);
        Scanner sc = new Scanner(System.in);
        int opt = sc.nextInt();
        //System.out.println(opt);
        out.writeUTF(String.valueOf(opt));

        if (opt == 1) {
            String message = in.readUTF();
            System.out.print(message);
            String[] data;
            do {
                String pass = sc.next();
                out.writeUTF(pass);

                data = in.readUTF().split("\n");
                if (data.length == 2)
                    System.out.print(data[0] + "\n" + data[1]);
            } while (!data[0].equals("Password atualizada!"));
            System.out.println(data[0]);
            //depois de mudar a passe pede uma nova autenticacao
            System.out.println("olaola");
            //receiveMenu();
            //System.out.println("olaola");
        }
    }
}
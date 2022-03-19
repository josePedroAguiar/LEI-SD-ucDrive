import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client {

    public static void main(String[] args) {
        // 1o passo - criar socket
        int serversocket = 6001;
        try (Socket s = new Socket("localhost", serversocket)) {
            System.out.println("SOCKET=" + s);

            // 2o passo
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

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
                    System.out.println(respostaAndToken[0]);
                } while (!respostaAndToken[0].equals("Login com sucesso"));

                while (true) {
                    // Receive Menu Options
                    System.out.print(in.readUTF());

                    int opt = sc.nextInt();
                    System.out.println(opt);
                    out.writeUTF(String.valueOf(opt));

                    if (opt == 1) {
                        String message = in.readUTF();
                        System.out.print(message);
                        while (true) {
                            String pass = sc.next();
                            out.writeUTF(pass);

                            String[] data = in.readUTF().split("\n");
                            if (data[0].equals("Password atualizada!")) {
                                System.out.println(data[0]);
                                break;
                            }
                            System.out.print(data[0] + data[1]);
                        }
                    }
                }
            }

        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        }
    }
}
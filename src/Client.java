import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client {
    static DataInputStream in;
    static DataOutputStream out;
    static Socket s;
    static boolean repeat_login=false;

    public static void main(String[] args) {
        // 1o passo - criar socket
        int serversocket = 6001;
        do {
        try {
            repeat_login=false;
            s = new Socket("localhost", serversocket);
            System.out.println("SOCKET=" + s);

            // 2o passo
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());

            // 3o passo
            Scanner sc = new Scanner(System.in);
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

            while (Menu());
                
       
        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
        }
         }while(repeat_login);
    }

    private static boolean Menu() throws IOException {
        Scanner sc = new Scanner(System.in);
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
            } while (!data[0].equals("Password atualizada!"));
            System.out.println(data[0]);
            s.close();
            repeat_login=true;
            System.out.println("Sessão foi terminada");
            return false;
            // depois de mudar a passe pede uma nova autenticacao
            // System.out.println("olaola");
        } else if ("ls -server".equals(opt) || "ls -client".equals(opt)) {
            String[] list = in.readUTF().split("\n");
            for (String line : list)
                System.out.println(line);
        } else if (opt.contains("cd -server") || opt.contains("cd -client")) {
            String message = in.readUTF();
            System.out.println(message);
        } else if (opt.contains("pull")) {
            String[] ss = opt.split(" ");
            String filename = ss[1];
            String destination = ss[2];
            
            new Download(filename, destination, s);

        } else if (opt.contains("push")) {
            int port = in.readInt();
            new Upload(opt.split(" ")[1], port);

        } else if (opt.equals("exit")){
            s.close();
            return false;}
            return true;
    }
   
}
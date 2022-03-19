import java.net.*;
import java.util.Scanner;
import java.io.*;

public class Client {
	private static int serversocket = 6001;
	
	public static void main(String args[]) {
		// 1o passo - criar socket
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
					texto += "\t"+sc.nextLine();
					out.writeUTF(texto);
					resposta= in.readUTF();
					respostaAndToken=resposta.split("\\|");
					System.out.println(respostaAndToken[0]);
				}while ( !respostaAndToken[0].equals("Login com sucesso") );
				while (true) {

					// WRITE INTO THE SOCKET
					//out.writeUTF(texto);
					
					// READ FROM SOCKET
					String data = in.readUTF();
					
					// DISPLAY WHAT WAS READ
					System.out.println("Received: \n" + data);
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
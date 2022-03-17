// TCPServer2.java: Multithreaded server
import java.net.*;
import java.io.*;
import java.util.*;


public class Server {
	private static int serverPort = 6001;

	
	public static void main(String args[]) {
		int numero = 0;
		try {
			File myObj = new File("usersData.txt");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
			  String data = myReader.nextLine();
			  if (data.length()!=0&&data.charAt(0)!='#'){
				User user=new User(data);
			  }

			  
				  
			}
			myReader.close();
		  } catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		  }

		
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
class User{
	String username;
	String expData;
	long cellNumber;
	public User(String data)
	{	
		String[] arrOfStr=data.split("\\t");
		if(arrOfStr.length>2){
			username=arrOfStr[0];
			expData=arrOfStr[1];
			try{
			cellNumber=Long.parseLong(arrOfStr[2]);}
			catch(NumberFormatException e){
				System.out.println("Cell number is invalid");
			}

		}
		System.out.println(cellNumber);
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
		String resposta;
		try {
			while (true) {
                PropertyValues properties = new PropertyValues();
		        properties.getPropValues();
				String received = in.readUTF();
				System.out.println("T[" + thread_number + "] Recebeu: " + received);

                // an echo server
                
				String menu = "**********MENU**********\n" 
                    + "1- ALTERAR PASSWORD\n"
                    + "2- CONFIG IP E PORTOS\n" 
                    + "3- LISTAR FICHEIROS DA DIRETORIA DO SERVIDOR\n"
                    + "4- MUDAR DIRETORIA DO SERVIDOR\n"
					+ "5- LISTAR FICHEIROS DA DIRETORIA DO CLIENTE\n"
                    + "6- MUDAR DIRETORIA DO CLIENTE\n"
					+ "7- DESCARREGAR FICHEIRO\n"
                    + "8- CARREGAR FICHEIRO\n";

				out.writeUTF(menu);

				String data = in.readUTF();
				System.out.println("T[" + thread_number + "] Recebeu: " + data);
				resposta = data.toUpperCase();
				out.writeUTF(resposta);
			}
		} catch (EOFException e) {
			System.out.println("EOF:" + e);
		} catch (IOException e) {
			System.out.println("IO:" + e);
		}
	}
}
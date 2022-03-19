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

public class Server {
	private static int serverPort = 6001;
	//TreeSet<User> tree= new TreeSet<User>(new myNameComparator());


	public static void main( String[] args ) {
		int numero = 0;


		
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
	long ccNumber;
	boolean athetication=false;
	boolean valid;
	String address;
	String pass;
	String department;
	long cellNumber;
	String username;
	Data expDate;
	public User(String data)
	{	
		String[] arrOfStr=data.split("\\t");
		System.out.println(arrOfStr.length);
		if(arrOfStr.length==7){

			address=arrOfStr[1];
			pass=arrOfStr[2];
			department=arrOfStr[3];
			username=arrOfStr[5];
			try{
				ccNumber=Long.parseLong(arrOfStr[0]);
				cellNumber=Long.parseLong(arrOfStr[4]);
				String[]date=arrOfStr[6].split("/");
				if(date.length==3)
					expDate=new Data(Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]));
				else{
					System.out.println("ERROR:Date is invalid (i.e.: DD/M/YY-numeric)");
					valid=false;
					return;}

			}
			catch(NumberFormatException e){
				System.out.println("ERROR:Data(CC-Number,Phone-Number,Date) of "+username+" is invalid");
				valid=false;
				return;
			}
			valid=true;
		}
		else {
			valid=false;
		}

	}

	public User () {

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
		HashSet<User> hs = new HashSet();
		try {
			File myObj = new File("usersData.txt");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				if (data.length()!=0&&data.charAt(0)!='#'){
					User user=new User(data);
					if(user.valid)
						hs.add(user);


				}
			}
			/*
			Iterator<User> iter = hs.iterator();
			while (iter.hasNext()) {

				// Printing all elements inside objects
				System.out.println(iter.next().username);
			}
			*/
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		try {
			while (true) {
				User currentUser=new User();
				while ( !currentUser.athetication ){
					String received = in.readUTF();
					Iterator<User> iter = hs.iterator();
					while (iter.hasNext()) {
						currentUser=iter.next();
						if(currentUser.username.equals("")&&currentUser.pass.equals(""))
						{
							currentUser.athetication=true;
							break;
						}
						if(currentUser.username.equals("")&&currentUser.pass.equals(""))
						{continue;}
					}
					System.out.println("T[" + thread_number + "] Recebeu: " + received);
					out.writeUTF("Tenta outra vez");
				}


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
						""";

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
// TCPServer2.java: Multithreaded server

import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/*class myNameComparator implements Comparator<User>
{
	public int compare(User s1, User s2)
	{
		return s1.username.compareTo(s2.username);
	}
}*/
class Cmd extends Thread{
    public boolean flagHideOrShow=false;
    UDPPingClient ping;
    Scanner sc = new Scanner(System.in);

    public Cmd(UDPPingClient ping){
        this.ping=ping;

    }
    public void run() {
        try {
            do {
                String hideOrShow;
                if((hideOrShow = sc.nextLine()).equals("")){
                    hideOrShow = hideOrShow.toLowerCase();
                    if (hideOrShow.equals("hide") || hideOrShow.equals("h"))
                        ping.flagHideOrShow = true;
                    else
                        ping.flagHideOrShow = false;

                }
            } while ( true );
        }
        catch (Exception e) {
            System.out.println("Exception handled");
        }
    }
}

public class ServerBackUp {
    // TreeSet<User> tree= new TreeSet<User>(new myNameComparator());
    Path root;
    Path currentDir;
    boolean statusMainServer =true;
    public static void main(String[] args)
            throws InterruptedException
    {
        int numero = 0;
        ServerBackUp server= new  ServerBackUp();
        UDPPingClient t= new UDPPingClient(server);
        t.start();
        t.join();

        int serverPort = 6003;
        try (ServerSocket listenSocket = new ServerSocket(serverPort)) {
            System.out.println("A escuta no porto " + serverPort);
            System.out.println("LISTEN SOCKET=" + listenSocket);


            while (true) {
                if(!server.statusMainServer){
                    System.out.println();
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
                numero++;
                Connection c = new Connection(clientSocket, numero);
                server.root = c.createDir("MainServer");
                server.currentDir = server.root;}
                else {
                    /*try {
                        Thread.sleep(1000);
                    }
                    catch (Exception e){

                    }*/

                }
            }
        } catch (IOException e) {
            System.out.println("Listen:" + e.getMessage());
        }
    }

    public void setrootectory(Path newPath) {
        Server.root = newPath;
    }
}

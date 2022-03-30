// TCPServer2.java: Multithreaded server

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
   public static void main(String[] args)
            throws InterruptedException
    {
        new Server(6003,6001,"ServerBack");   
    }
}

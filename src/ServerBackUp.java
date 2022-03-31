import java.util.Scanner;


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

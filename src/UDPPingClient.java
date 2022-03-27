
import java.net.*;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
class Cmd extends Thread{
	public boolean flagHideOrShow=false;
	UDPPingClient ping;
	Scanner sc = new Scanner(System.in);

	public Cmd(UDPPingClient ping){
		this.ping=ping;
		this.start();
	}
	public void run(){
		do {
			try {


				String hideOrShow = sc.nextLine();
				hideOrShow = hideOrShow.toLowerCase();
				if (hideOrShow.equals("hide") || hideOrShow.equals("h"))
					ping.flagHideOrShow = true;
				else
					ping.flagHideOrShow = false;
			}
			catch (Exception e){
				break;}

		}while (true);

	}

}
public class UDPPingClient extends Thread  {
	private static int serverPort = 6789;
	private static final int MAX_TIMEOUT = 1000;
	private static final int INTERVAL_BETWEEN_PING  = 30000;
	private static final int NUMBER_OF_PINGS  =10;
	ServerBackUp server;

	private static final String serverName = "localhost";

	public boolean flagHideOrShow=false;
	Scanner sc = new Scanner(System.in);

	public UDPPingClient(ServerBackUp server){
		Cmd cmd=new Cmd(this);
		this.server=server;
		this.start();
		String hideOrShow;


	}
	public void run() {
		try (DatagramSocket aSocket = new DatagramSocket()) {
			try (Scanner sc = new Scanner(System.in)) {
				while(true){
					if(!this.flagHideOrShow){
					System.out.println("___________NEW BLOCK PINGS_____________");}
				int sequence_number = 0;
				int pakageLost=0;
				while(sequence_number < NUMBER_OF_PINGS){

					InetAddress aHost = InetAddress.getByName(serverName);
					String str = "PING " + sequence_number + "\n";
					byte[] buf = new byte[1024];
					buf = str.getBytes();
					DatagramPacket ping = new DatagramPacket(buf, buf.length, aHost, serverPort);
					aSocket.send(ping);

					LocalDateTime myDateObj = LocalDateTime.now();
					DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMM HH:mm:ss");
					String time = myDateObj.format(myFormatObj);

					try {

						// Set up the timeout 1000 ms = 1 sec
						aSocket.setSoTimeout(MAX_TIMEOUT);
						DatagramPacket response = new DatagramPacket(new byte[1024], 1024);
						aSocket.receive(response);
						// Print the packet and the delay
						if(!this.flagHideOrShow){
						try {
							printData(response,time);
						}
						catch (Exception e) {
							System.out.println("IO: " + e.getMessage());
						}
						}

					} catch (IOException e) {
						if(!this.flagHideOrShow){
						// Print which packet has timed out
						System.out.println("["+time+"] Timeout for packet " + sequence_number);}
						pakageLost++;

					}
					// next packet
					sequence_number ++;
				}
					try {

						LocalDateTime myDateObj = LocalDateTime.now();
						DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMM HH:mm:ss");
						String time = myDateObj.format(myFormatObj);
						if(!this.flagHideOrShow) {
							System.out.println("If you want to hide the unitary prints of the pings type 'Hide' or 'H'" +
											   "\n_____________________________________\n " +
											   "[" + time + "] Perc. of package receveid " + ((NUMBER_OF_PINGS - pakageLost) / NUMBER_OF_PINGS * 100) + "% (" + (NUMBER_OF_PINGS - pakageLost) + "/" + NUMBER_OF_PINGS + ")");
						}
						else  {
							System.out.println(
											   "\n_____________________________________\n" +

											   "[" + time + "] Perc. of package receveid " + ((NUMBER_OF_PINGS - pakageLost) / NUMBER_OF_PINGS * 100) + "% (" + (NUMBER_OF_PINGS - pakageLost) + "/" + NUMBER_OF_PINGS + ")\n"+
											   "If you want to show the unitary prints of the pings type 'Show' or 'S'");
						}
						if(pakageLost>8){

							System.out.println("Nice");
							server.statusMainServer=false;
							return;}

						Thread.sleep(INTERVAL_BETWEEN_PING);
					}
					catch (Exception e) {
						System.out.println("Foda-se");
					}
				}
			}
		}catch (SocketException e){
			System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){
			System.out.println("IO: " + e.getMessage());
		}
	}




	private static void printData(DatagramPacket request,String time) throws Exception
	{
		// Obtain references to the packet's array of bytes.
		byte[] buf = request.getData();

		// Wrap the bytes in a byte array input stream,
		// so that you can read the data as a stream of bytes.
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);

		// Wrap the byte array output stream in an input stream reader,
		// so you can read the data as a stream of characters.
		InputStreamReader isr = new InputStreamReader(bais);

		// Wrap the input stream reader in a bufferred reader,
		// so you can read the character data a line at a time.
		// (A line is a sequence of chars terminated by any combination of \r and \n.)
		BufferedReader br = new BufferedReader(isr);

		// The message data is contained in a single line, so read this line.
		String line = br.readLine();

		// Print host address and data received from it.
		System.out.println(
				"[ "+time+" ] Received from " +
				request.getAddress().getHostAddress() + ": "+
		new String(line));
	}
}

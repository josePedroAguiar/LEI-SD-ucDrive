import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;

class CmdServer extends Thread {
	public boolean flagHideOrShow = false;
	UDPPingServer ping;
	Scanner sc = new Scanner(System.in);

	public CmdServer(UDPPingServer ping) {
		this.ping = ping;

	}

	public void run() {
		try {
			do {
				String hideOrShow;
				if ((hideOrShow = sc.nextLine()).equals("")) {
					hideOrShow = hideOrShow.toLowerCase();
					if (hideOrShow.equals("hide") || hideOrShow.equals("h"))
						ping.flagHideOrShow = true;
					else
						ping.flagHideOrShow = false;

				}
			} while (true);
		} catch (Exception e) {
			System.out.println("Exception handled");
		}
	}
}

public class UDPPingServer extends Thread {
	private static int port = 6789;
	// private static final int AVERAGE_DELAY = 100; // millisegundos
	public boolean flagHideOrShow = false;
	private static Scanner sc = new Scanner(System.in);

	public UDPPingServer() {
		this.start();
		String hideOrShow;
		/*
		 * do {
		 * hideOrShow = sc.nextLine();
		 * hideOrShow=hideOrShow.toLowerCase();
		 * if (hideOrShow.equals("hide")||hideOrShow.equals("h"))
		 * this.flagHideOrShow=true;
		 * else
		 * this.flagHideOrShow=false;
		 * 
		 * }while (true);
		 */
	}

	public void run() {

		try (DatagramSocket aSocket = new DatagramSocket(port)) {
			LocalDateTime myDateObj = LocalDateTime.now();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMM HH:mm:ss");
			String time = myDateObj.format(myFormatObj);
			System.out.println("[" + time + "] Socket for pings  was created");
			System.out.println("__________________________________________\n" +
					"	Ping Socket\n" +
					"__________________________________________");
			System.out.println("A escuta no porto " + port);
			System.out.println("__________________________________________");
			Random random = new Random();
			CmdServer cmd = new CmdServer(this);
			cmd.start();
			// System.out.println("Socket Datagram à escuta no porto " + port);
			int count = 0;
			while (true) {
				byte[] buffer = new byte[1024];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				if (!flagHideOrShow) {
					try {
						printData(request);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				/*
				 * if (random.nextDouble() < 0.3) {
				 * System.out.print("   Reply not sent.");
				 * continue;
				 * }
				 */

				// Simulate network delay.
				// Send reply.

				InetAddress clientHost = request.getAddress();
				int clientPort = request.getPort();
				byte[] buf = request.getData();
				DatagramPacket reply = new DatagramPacket(buf, buf.length, clientHost, clientPort);
				aSocket.send(reply);
				if (!flagHideOrShow && count == 9) {
					System.out.println("If you want to hide the unitary prints of the pings type 'Hide' or 'H'");
					count = 0;
				} else if (!flagHideOrShow && count == 9) {
					System.out.println("If you want to show the unitary prints of the pings type 'Show' or 'S'");
					count = 0;
				}
				count++;
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}

	private static void printData(DatagramPacket request) throws Exception {
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
		System.out.println("Reply sent to " + request.getAddress().getHostAddress() + ": " + new String(line));
	}
}
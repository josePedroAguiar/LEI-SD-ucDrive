import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.*;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UDPPingServer extends Thread {
	private static int port = 6789;
	public Server server;
	// private static final int AVERAGE_DELAY = 100; // millisegundos
	public boolean flagHideOrShow = false;

	public UDPPingServer(Server server) {
		this.server = server;
		this.start();
	}

	public void run() {

		while (true) {
			try (DatagramSocket aSocket = new DatagramSocket(port)) {
				LocalDateTime myDateObj = LocalDateTime.now();
				DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMM HH:mm:ss");
				String time = myDateObj.format(myFormatObj);
				System.out.println("[" + time + "] Socket for pings  was created");
				System.out.println("__________________________________________\n" +
						"	Ping Socket\n" +
						"__________________________________________");
				System.out.println("Listening in port " + port);
				System.out.println("__________________________________________");
				aSocket.setSoTimeout(10000);
				while (true) {
					byte[] buffer = new byte[1024];
					DatagramPacket request = new DatagramPacket(buffer, buffer.length);
					try {
						aSocket.receive(request);
						server.statusBackUpServer = true;
					} catch (SocketTimeoutException e2) {
						server.statusBackUpServer = false;

						System.out.println("Timeout reached!!! " + e2);

					}
					if (!server.statusBackUpServer)
						break;

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
					 // Simulate network delay.
					 * }
					 */

					

					InetAddress clientHost = request.getAddress();
					int clientPort = request.getPort();
					byte[] buf = request.getData();
					DatagramPacket reply = new DatagramPacket(buf, buf.length, clientHost, clientPort);
					aSocket.send(reply);
				}

			} catch (SocketException e) {
				System.out.println("Socket: " + e.getMessage());
				return;
			} catch (IOException e) {

				System.out.println("IO: " + e.getMessage());
			}
		}

	}

	private static void printData(DatagramPacket request) throws Exception {
		byte[] buf = request.getData();
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		InputStreamReader isr = new InputStreamReader(bais);
		BufferedReader br = new BufferedReader(isr);
		String line = br.readLine();
		System.out.println("Reply sent to " + request.getAddress().getHostAddress() + ": " + new String(line));
	}
}
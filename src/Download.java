import java.net.*;
import java.io.*;

class Download extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Connection c;
    String filename;
    String destination;
    int DownloadSocket = 0;
    Socket prevSocket;

    public Download(String filename, String destination, Socket cmd) {
        this.filename = filename;
        this.destination = destination;
        this.prevSocket = cmd;
        this.start();
    }

    public void run() {
        try (ServerSocket listenSocket = new ServerSocket(DownloadSocket)) {
            DataOutputStream out1 = new DataOutputStream(prevSocket.getOutputStream());
            out1.writeInt(listenSocket.getLocalPort()); // envia o porto aleatorio para o cliente

            Socket downloadSocket = listenSocket.accept(); // BLOQUEANTE
            System.out.println("CORRE CORRE CORRE");

            this.in = new DataInputStream(downloadSocket.getInputStream());
            this.out = new DataOutputStream(downloadSocket.getOutputStream());
            
        } catch (IOException e) {
            System.out.println("Listen:" + e.getMessage());
        }

        int bytes = 0;
        long size;
        try {
            size = in.readLong();

            File newF = new File(destination);
            try (FileOutputStream fileOutputStream = new FileOutputStream(newF)) {

                byte[] buffer = new byte[8 * 1024];
                while (true) {
                 
                    bytes = in.read(buffer, 0, (int) Math.min(buffer.length, size));
                    if (bytes == -1 || size <= 0)
                        break;

                    System.out.println(
                            "Recebendo " + filename + " (" + String.format("%.2f", ((float) bytes / size) * 100)
                                    + " %)");
                    fileOutputStream.write(buffer, 0, bytes);
                    size -= bytes; // read upto file size
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();

        return;

    }
}
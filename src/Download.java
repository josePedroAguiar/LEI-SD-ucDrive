import java.net.*;
import java.io.*;

class Download extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Connection c;

    public Download(String filename, User u, String destination) throws IOException {
        int filesSocket = 6002;

        this.start();
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        try (ServerSocket listenSocket = new ServerSocket(filesSocket)) {
            while (true) {
                Socket uploadSocket = listenSocket.accept(); // BLOQUEANTE
                c = new Connection(uploadSocket, Server.hs, 0);
                in = new DataInputStream(uploadSocket.getInputStream());
                out = new DataOutputStream(uploadSocket.getOutputStream());

            }
        } catch (IOException e) {
            System.out.println("Listen:" + e.getMessage());
        }

        int bytes = 0;
        long size = in.readLong();

        File newF = new File(destination);
        try (FileOutputStream fileOutputStream = new FileOutputStream(newF)) {

            byte[] buffer = new byte[8 * 1024];
            while (true) {
                bytes = in.read(buffer, 0, (int) Math.min(buffer.length, size));
                if (bytes == -1 || size <= 0)
                    break;

                System.out.println(
                        "Recebendo " + filename + " (" + String.format("%.2f", ((float) bytes / size) * 100) + " %)");
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes; // read upto file size
            }
        }
        System.out.println();

        this.interrupt();
    }
}
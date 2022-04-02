import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

class Download extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Connection c;
    String filename;
    String destination;
    Socket s;
    int DownloadSocket;

    public Download(String filename, String destination, int DownloadSocket) {
        this.filename = filename;
        this.destination = destination;
        this.DownloadSocket = DownloadSocket;
        this.start();

    }

    public synchronized void run() {
        try {
            s = new Socket("localhost", DownloadSocket);
            System.out.println("SOCKET=" + s);

            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());

            int bytes = 0;
            long size;

            size = in.readLong();

            File newF = new File(destination);
            try (FileOutputStream fileOutputStream = new FileOutputStream(newF)) {

                byte[] buffer = new byte[8 * 1024];
                while (true) {

                    bytes = in.read(buffer, 0, (int) Math.min(buffer.length, size));
                    if (bytes == -1 || size <= 0)
                        break;

                    if (((float) bytes / size) == 1.0f)
                        System.out.println(
                                "Recebendo " + filename + " (" + String.format("%.2f", ((float) bytes / size) * 100)
                                        + " %). Guardando em " + destination);

                    fileOutputStream.write(buffer, 0, bytes);
                    size -= bytes; // read upto file size
                }
            }

            System.out.println();
            return;

        } catch (UnknownHostException e) {
            System.out.println("Sock:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
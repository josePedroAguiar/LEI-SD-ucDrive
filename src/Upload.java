import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Upload extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Connection c;
    String filename;

    int UploadSocket = 0;
    Socket prevSocket;

    public Upload(String filename, Socket cmd) throws IOException {
        this.filename = filename;
        this.prevSocket = cmd;
        this.start();

    }

    public synchronized void run() {
        try (ServerSocket listenSocket = new ServerSocket(UploadSocket)) {
            DataOutputStream out1 = new DataOutputStream(prevSocket.getOutputStream());
            out1.writeInt(listenSocket.getLocalPort()); // envia o porto aleatorio para o cliente

            Socket uploadSocket = listenSocket.accept(); // BLOQUEANTE
            // System.out.println("CORRE CORRE CORRE");

            this.in = new DataInputStream(uploadSocket.getInputStream());
            this.out = new DataOutputStream(uploadSocket.getOutputStream());

        } catch (IOException e) {
            System.out.println("Listen:" + e.getMessage());
        }

        int bytes = 0;

        File file = new File(filename);
        // System.out.println("CORRE CORRE");
        try {
            out.writeLong(file.length());

            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                // break file into chunks
                byte[] buffer = new byte[8 * 1024];
                int progress = 0;
                while ((bytes = fileInputStream.read(buffer)) != -1) {
                    progress += bytes;
                    out.write(buffer, 0, bytes);
                    if (((float) progress / file.length()) == 1.0f)
                        System.out.println("Enviando " + filename + " ("
                                + String.format("%.2f", ((float) progress / file.length()) * 100) + " %)");
                    out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
import java.net.*;
import java.io.*;

class Upload extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket s;
    int UploadSocket;
    String filename;

    public Upload(String filename, int UploadSocket) throws IOException {
        this.filename = filename;
        this.UploadSocket = UploadSocket;
        this.start();

    }

    public void run() {
        try {
            s = new Socket("localhost", UploadSocket);
            System.out.println("SOCKET=" + s);

            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());

            int bytes = 0;

            File file = new File(filename);
            System.out.println("CORRE CORRE");
            out.writeLong(file.length());
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                // break file into chunks
                byte[] buffer = new byte[8 * 1024];
                int progress = 0;
                while ((bytes = fileInputStream.read(buffer)) != -1) {
                    progress += bytes;
                    out.write(buffer, 0, bytes);
                    System.out.println("Enviando " + filename + " ("
                            + String.format("%.2f", ((float) progress / file.length()) * 100) + " %)");
                    out.flush();
                }
            }
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
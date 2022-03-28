import java.net.*;
import java.io.*;

class Upload extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket s;

    public Upload(String filename) throws IOException {
        int downloadSocket = 6002;

        this.start();
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            s = new Socket("localhost", downloadSocket);
            System.out.println("SOCKET=" + s);

            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());

            int bytes = 0;

            File file = new File(filename);
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
        }
    }
}
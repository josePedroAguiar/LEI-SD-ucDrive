import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SendFile extends Thread {

    public Server server;

    public SendFile(Server server) {
        this.server = server;
        synchronized (server.filesToReplicate) {
            this.start();
        }
    }

    public void run() {
        send();
    }

    public synchronized void send() {
        while (true) {

            if (server.filesToReplicate.size() == 0) {
                // System.out.println(server.filesToReplicate);
                try {
                    Thread.sleep(10000);
                    continue;
                } catch (InterruptedException e) {

                    System.out.println("Thread foi interrompida!");
                    return;
                }

            }
            if (server.statusBackUpServer) {
                for (int k = 0; k < server.filesToReplicate.size(); k++) {
                    try {
                        String name = server.filesToReplicate.get(k);
                        Path path = Paths.get(name);

                        int length = (int) Files.size(path);
                        int port = 10001;
                        System.out.println("_____________Change File Socket__________________");
                        System.out.println("A escuta no porto " + port);
                        System.out.println("__________________________________________");
                        String s = new String(name + "@" + length);
                        byte[] var = s.getBytes();

                        DatagramSocket ds = new DatagramSocket();
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(name));
                        byte[] buf = new byte[1024];
                        DatagramPacket dp = new DatagramPacket(var, var.length, InetAddress.getByName("localhost"),
                                10001);
                        ds.send(dp);
                        ds.close();

                        int nPackage = (int) (length / 1024);
                        int lastPackageSize = (int) (length % 1024);

                        ds = new DatagramSocket();

                        byte[] allPacket = new byte[length];
                        bis.read(allPacket);
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        int i;
                        for (i = 0; i < nPackage; i++) {
                            outputStream = new ByteArrayOutputStream();
                            outputStream.write(allPacket, 1024 * i, 1024);
                            buf = new byte[1024];
                            buf = outputStream.toByteArray();
                            bis.read(buf);
                            dp = new DatagramPacket(buf, buf.length, InetAddress.getByName("localhost"), 10001);
                            ds.send(dp);

                        }
                        if (lastPackageSize != 0) {
                            outputStream = new ByteArrayOutputStream();
                            outputStream.write(allPacket, 1024 * i, lastPackageSize);
                            buf = new byte[lastPackageSize];
                            buf = outputStream.toByteArray();
                            bis.read(buf);
                            dp = new DatagramPacket(buf, buf.length, InetAddress.getByName("localhost"), 10001);
                            ds.send(dp);

                        }

                        ds.close();
                        bis.close();
                        server.filesToReplicate.remove(k);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    System.out.println("Thread foi interrompida!");
                }
            }
        }
    }

}

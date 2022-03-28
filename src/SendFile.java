import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendFile implements Runnable{


    @Override
    public void run() {


        send();
    }

    public void send(){


        try {


            BufferedInputStream bis = new BufferedInputStream(new FileInputStream("filegeneretor.py"));
            int length=bis.available();
            DatagramSocket ds = new DatagramSocket();
            byte[] buf = new byte[length];
            bis.read(buf);
            DatagramPacket dp = new DatagramPacket(buf,buf.length, InetAddress.getByName("127.0.0.1"),10001);
            ds.send(dp);
            ds.close();
            bis.close();
        } catch (IOException e) {


            e.printStackTrace();
        }
    }
    public static void main(String[] args) {


        new Thread(new ReceiveFile()).start();
        new Thread(new SendFile()).start();
    }
}


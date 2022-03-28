import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ReceiveFile implements Runnable{



    @Override
    public void run() {


        receive();
    }

    public void receive(){


        try {


            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(
                    "filegeneretor_copy.py"));
            DatagramSocket ds = new DatagramSocket(10001);
            byte[] buf = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buf,buf.length);
            ds.receive(dp);
            ds.close();
            fos.write(dp.getData());
            fos.flush();
            fos.close();
        } catch (IOException e) {


            e.printStackTrace();
        }

    }
}

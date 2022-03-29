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

            DatagramSocket ds;
            DatagramPacket dp;
            byte[] buf;
            int port=10001;
            ds = new DatagramSocket(10001);
            System.out.println("_____________Change File Socket__________________");
            System.out.println("A escuta no porto " + port);
            System.out.println("__________________________________________");
            byte[] var2 = new byte[1024];
            DatagramPacket var3 = new DatagramPacket(var2, var2.length);
            ds.receive(var3);
            String var4 = new String(var3.getData(), 0, var3.getLength());
            System.out.println("Server Recebeu: " + var4);
            String[] arrOfStr = var4.split("@");
            System.out.println(arrOfStr[1]);
            int foo = Integer.parseInt( arrOfStr[1]);



            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(
                    "copy/"+arrOfStr[0]));
            buf = new byte[foo];
            dp = new DatagramPacket(buf,buf.length);
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

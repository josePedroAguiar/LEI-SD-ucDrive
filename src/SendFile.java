import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SendFile implements Runnable{


    @Override
    public void run() {


        send();
    }

    public void send(){


        try {
            String name="ReceiveFile.java";
            Path path = Paths.get(name);
            int length=(int)Files.size(path);
            BufferedInputStream bis;
            int port=10001;
            DatagramSocket ds;
            System.out.println("_____________Change File Socket__________________");
            System.out.println("A escuta no porto " + port);
            System.out.println("__________________________________________");
            String s=new String(name+"@"+length);
            byte[] var = s.getBytes();
            ds = new DatagramSocket();
            byte[] buf = new byte[1024];
            DatagramPacket dp = new DatagramPacket(var, var.length, InetAddress.getByName("127.0.0.1"),10001);
            ds.send(dp);
            ds.close();

            int nPackage=(int)(length/1024);
            int lastPackageSize=(int)(length%1024);

            bis = new BufferedInputStream(new FileInputStream(name));
            ds = new DatagramSocket();
           
            byte [] allPacket = new byte[length];
            bis.read(allPacket);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int i;
            for(i=0;i<nPackage;i++){
                outputStream = new ByteArrayOutputStream();
                outputStream.write(allPacket,1024*i,1024);
                buf = new byte[1024];
                buf= outputStream.toByteArray( );
                bis.read(buf);
                dp = new DatagramPacket(buf,buf.length, InetAddress.getByName("127.0.0.1"),10001);
                ds.send(dp);
                System.out.println("T_T");
            }
            System.out.println("-_-");
            System.out.println(i);
            if(lastPackageSize!=0){
                outputStream = new ByteArrayOutputStream();
                outputStream.write(allPacket,1024*i,lastPackageSize);
                buf = new byte[lastPackageSize];
                buf= outputStream.toByteArray( );
                bis.read(buf);
                dp = new DatagramPacket(buf,buf.length, InetAddress.getByName("127.0.0.1"),10001);
                ds.send(dp);
             
             
            }
            ds.close();
            bis.close();

            /*
            bis = new BufferedInputStream(new FileInputStream(name));
            ds = new DatagramSocket();
            buf = new byte[length];
            bis.read(buf);
            dp = new DatagramPacket(buf,buf.length, InetAddress.getByName("127.0.0.1"),10001);
            ds.send(dp);
            ds.close();
            bis.close();
            */

        } catch (IOException e) {


            e.printStackTrace();
        }

          
    }
    public static void main(String[] args) {


        new Thread(new ReceiveFile()).start();
        new Thread(new SendFile()).start();
    }
}


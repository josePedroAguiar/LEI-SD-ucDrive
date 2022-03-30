import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
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
            long foo = Long.parseLong( arrOfStr[1]);
            int nPackage=(int)(foo/1024);
            int lastPackageSize=(int)(foo%1024);

            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(
                "copy/"+arrOfStr[0]));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

            for(int i=0;i<nPackage;i++){
                buf = new byte[1024];
                dp = new DatagramPacket(buf,buf.length);
                ds.receive(dp);
                outputStream.write(dp.getData() );
            }
            if(lastPackageSize!=0){
                buf = new byte[lastPackageSize];
                dp = new DatagramPacket(buf,buf.length);
                ds.receive(dp);
                outputStream.write(dp.getData() );
            }
            byte c[] = outputStream.toByteArray( );
            fos.write(c);
            ds.close();
            fos.flush();
            fos.close();
        } catch (IOException e) {


            e.printStackTrace();
        }
    }

            /*BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(
                    "copy/"+arrOfStr[0]));
            buf = new byte[foo];
            dp = new DatagramPacket(buf,buf.length);
            ds.receive(dp);
            ds.close();
            fos.write(dp.getData());
            fos.flush();
            fos.close();
        } catch (IOException e) {


            e.printStackTrace();}*/
        

    
}

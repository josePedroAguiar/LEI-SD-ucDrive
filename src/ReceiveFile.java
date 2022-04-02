import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File; 
public class ReceiveFile extends Thread {
    DatagramSocket ds;

    public void run() {

        receive();
    }

    public void receive() {

        try {


            DatagramPacket dp;
            byte[] buf;
            int port = 10001;
            ds = new DatagramSocket(10001);
            while(true){
            System.out.println("_____________Change File Socket__________________");
            System.out.println("A escuta no porto " + port);
            System.out.println("__________________________________________");
            byte[] var2 = new byte[1024];
            DatagramPacket var3 = new DatagramPacket(var2, var2.length);
            ds.receive(var3);
            String var4 = new String(var3.getData(), 0, var3.getLength());
            //System.out.println("Server Recebeu: " + var4);
            String[] arrOfStr = var4.split("@");
            if(arrOfStr.length==2){     
            try{
            long foo = Long.parseLong(arrOfStr[1]);
            long nPackage = (long) (foo / 1024);
            int lastPackageSize = (int) (foo % 1024);

            Path p;
            if (arrOfStr[0].contains("MainServer")){
                p = Paths.get(arrOfStr[0].replace("MainServer", "ServerBack"));
                File myObj = new File(arrOfStr[0].replace("MainServer", "ServerBack")); 
                if (myObj.delete()) { 
                    System.out.println("Deleted the file: " + myObj.getName());
                  } else {
                    System.out.println("Failed to delete the file.");
                  } 
            }
            else{
                p = Paths.get(arrOfStr[0].replace("ServerBack", "MainServer"));
                File myObj = new File(arrOfStr[0].replace("ServerBack", "MainServer")); 
                if (myObj.delete()) { 
                    System.out.println("Deleted the file: " + myObj.getName());
                  } else {
                    System.out.println("Failed to delete the file.");
                  } 
            
            }
            Files.createDirectories(p.getParent());
            Files.createFile(p);
           
            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(p.toFile()));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            for (int i = 0; i < nPackage; i++) {
                buf = new byte[1024];
                dp = new DatagramPacket(buf, buf.length);
                ds.receive(dp);
                outputStream.write(dp.getData());
            }
            if (lastPackageSize != 0) {
                buf = new byte[lastPackageSize];
                dp = new DatagramPacket(buf, buf.length);
                ds.receive(dp);
                outputStream.write(dp.getData());
            }
            byte c[] = outputStream.toByteArray();
            fos.write(c);
          
            fos.flush();
            fos.close();

        }
        catch(NumberFormatException e){
            ;
        }
        }
       
       
        }
        } 
        catch (SocketException e) {
            System.out.println("Backup file socket closed!");
          
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    
    }
}

import java.io.*;
import java.util.*;

public class CreateConfig {
    public static void main(String[] args) {
        File configFile = new File("user.properties");

        try {
            Properties props = new Properties();
            props.setProperty("user", "joao");
            props.setProperty("pass", "123456789");
            props.setProperty("department", "DEI");
            props.setProperty("cell", "919191919");
            props.setProperty("address", "Coimbra");
            props.setProperty("CCnumber", "123456789");
            props.setProperty("expDate", "21/10/2019");
            FileWriter writer = new FileWriter(configFile);
            props.store(writer, "user settings");
            writer.close();
        } catch (FileNotFoundException ex) {
            // file does not exist
        } catch (IOException ex) {
            // I/O error
        }
    }
}
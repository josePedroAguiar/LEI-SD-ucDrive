/*import java.io.*;
import java.util.*;

public class CreateConfig {

    private String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
    public static void main(String[] args) {
        File configFile = new File("username.txt");
        if (configFile.createNewFile() || configFile.exists()) { // Verifica se o ficheiro existe e se é um ficheiro de texto
            try (BufferedWriter br = new BufferedWriter(new FileWriter(configFile))) {
                Random rand = new Random();
                br.write();
                br.close();
            } catch (FileNotFoundException ex) {
                // file does not exist
            } catch (IOException ex) {
                // I/O error
            }
        }
    }
}*/
import java.nio.file.*;

class User {
    long ccNumber;
    boolean athetication = false;
    boolean valid;
    String address;
    String pass;
    String department;
    long cellNumber;
    String username;
    Data expDate;
    Path root;
    Path currentDir;

    public User(String data) {
        String[] arrOfStr = data.split("\\t");
        // System.out.println(arrOfStr.length);
        if (arrOfStr.length == 7) {

            address = arrOfStr[1];
            pass = arrOfStr[2];
            department = arrOfStr[3];
            username = arrOfStr[5];
            try {
                ccNumber = Long.parseLong(arrOfStr[0]);
                cellNumber = Long.parseLong(arrOfStr[4]);
                String[] date = arrOfStr[6].split("/");
                if (date.length == 3)
                    expDate = new Data(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
                else {
                    System.out.println("ERROR: Date is invalid (i.e.: DD/M/YY-numeric)");
                    valid = false;
                    return;
                }

            } catch (NumberFormatException e) {
                System.out.println("ERROR: Data(CC-Number,Phone-Number,Date) of " + username + " is invalid");
                valid = false;
                return;
            }
            valid = true;
        } else {
            valid = false;
        }
    }

    public User() {
    }
}
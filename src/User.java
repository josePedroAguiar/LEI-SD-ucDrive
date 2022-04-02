import java.nio.file.Path;

class User {
    private long ccNumber;
    private boolean authenticated = false;
    private boolean valid;
    private String address;
    private String pass;
    private String department;
    private long cellNumber;
    private String username;
    private Data expDate;
    private Path root;
    private Path currentDir;
    private Path rootServer;
    private Path currentDirServer;
    private String MainServer_host;
    private int MainServer_port;
    private String BackUpServer_host;
    private int BackUpServer_port;

    public String getBackUpServer_host() {
        return BackUpServer_host;
    }

    public int getBackUpServer_port() {
        return BackUpServer_port;
    }

    public String getMainServer_host() {
        return MainServer_host;
    }

    public int getMainServer_port() {
        return MainServer_port;
    }

    public String getUsername() {
        return username;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getPass() {
        return pass;
    }

    public Path getRoot() {
        return root;
    }

    public Path getRootServer() {
        return rootServer;
    }

    public Path getCurrentDir() {
        return currentDir;
    }

    public Path getCurrentDirServer() {
        return currentDirServer;
    }

    public String getAddress() {
        return address;
    }

    public long getCcNumber() {
        return ccNumber;
    }

    public long getCellNumber() {
        return cellNumber;
    }

    public String getDepartment() {
        return department;
    }

    public Data getExpDate() {
        return expDate;
    }

    public void setBackUpServer_host(String backUpServer_host) {
        BackUpServer_host = backUpServer_host;
    }

    public void setBackUpServer_port(int backUpServer_port) {
        BackUpServer_port = backUpServer_port;
    }

    public void setMainServer_host(String mainServer_host) {
        MainServer_host = mainServer_host;
    }

    public void setMainServer_port(int mainServer_port) {
        MainServer_port = mainServer_port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setExpDate(Data expDate) {
        this.expDate = expDate;
    }

    public void setCcNumber(long ccNumber) {
        this.ccNumber = ccNumber;
    }

    public void setCellNumber(long cellNumber) {
        this.cellNumber = cellNumber;
    }

    public void setCurrentDir(Path currentDir) {
        this.currentDir = currentDir;
    }

    public void setCurrentDirServer(Path currentDirServer) {
        this.currentDirServer = currentDirServer;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setRoot(Path root) {
        this.root = root;
    }

    public void setRootServer(Path rootServer) {
        this.rootServer = rootServer;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public User(String data) {
        String[] arrOfStr = data.split("\\t");

        if (arrOfStr.length == 7) {

            setAddress(arrOfStr[1]);
            setPass(arrOfStr[2]);
            setDepartment(arrOfStr[3]);
            setUsername(arrOfStr[5]);

            try {
                setCcNumber(Long.parseLong(arrOfStr[0]));
                setCellNumber(Long.parseLong(arrOfStr[4]));
                String[] date = arrOfStr[6].split("/");
                if (date.length == 3)
                    setExpDate(
                            new Data(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2])));
                else {
                    System.out.println("ERROR: Date is invalid (i.e.: DD/M/YY-numeric)");
                    setValid(false);
                    return;
                }

            } catch (NumberFormatException e) {
                System.out.println("ERROR: Data(CC-Number,Phone-Number,Date) of " + username + " is invalid");
                setValid(false);
                return;
            }
            setValid(true);
        } else {
            setValid(false);
        }
    }

    public User() {
    }
}
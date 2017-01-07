import java.io.Serializable;

public class Cliente implements Serializable {

    private String username;
    private String password;
    private String ip;
    private int porto;

    public Cliente(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Cliente(String username, String password, String ip, int porto) {
        this.username = username;
        this.password = password;
        this.ip = ip;
        this.porto = porto;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPorto() {
        return porto;
    }

    public void setPorto(int porto) {
        this.porto = porto;
    }
}

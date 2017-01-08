/**
 * Created by romeu on 1/7/17.
 */
public class ClienteUDP {

    String username;
    String ip;
    String serverName;

    int port;

    public ClienteUDP(String username, String ip, String serverName, int port) {
        this.username = username;
        this.ip = ip;
        this.serverName = serverName;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

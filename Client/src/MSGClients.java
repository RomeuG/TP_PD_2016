import java.io.Serializable;

public class MSGClients implements Serializable {
    private static final long serialVersionUID = 10132L;

    String serverName;
    String username;

    public MSGClients(String serverName, String username) {
        this.serverName = serverName;
        this.username = username;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

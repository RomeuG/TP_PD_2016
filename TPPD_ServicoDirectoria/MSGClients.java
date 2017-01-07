import java.io.Serializable;

public class MSGClients implements Serializable {
    String serverName;

    public MSGClients(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}

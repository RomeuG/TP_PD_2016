import java.io.Serializable;

/**
 * Created by Andre on 07/01/2017.
 */
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

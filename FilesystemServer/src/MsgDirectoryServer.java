import Communication.Cliente;
import java.io.Serializable;
import java.util.ArrayList;

public class MsgDirectoryServer implements Serializable
{
    private static final long serialVersionUID = 10127L;
    
    private String serverName;
    ArrayList<Communication.Cliente> clientesOn;

    public MsgDirectoryServer(String serverName, ArrayList<Communication.Cliente> clientesOn) {
        this.serverName = serverName;
        this.clientesOn = clientesOn;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public ArrayList<Cliente> getClientesOn() {
        return clientesOn;
    }

    public void setClientesOn(ArrayList<Cliente> clientesOn) {
        this.clientesOn = clientesOn;
    }
}

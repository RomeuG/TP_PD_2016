import java.io.Serializable;
import java.util.ArrayList;

public class MsgDirectoryServer implements Serializable
{
    private static final long serialVersionUID = 10127L;

    private String serverName;
    private ArrayList<Cliente> clientesOn;
    private int portTCP;

    public MsgDirectoryServer(String serverName, ArrayList<Cliente> clientesOn, int portTCP) {
        this.serverName = serverName;
        this.clientesOn = clientesOn;
        this.portTCP = portTCP;
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

    public int getPortTCP() {
        return portTCP;
    }

    public void setPortTCP(int portTCP) {
        this.portTCP = portTCP;
    }
}
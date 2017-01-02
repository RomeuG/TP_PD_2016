
import java.io.Serializable;
import java.util.ArrayList;


public class Server implements Serializable
{
    private static final long serialVersionUID = 10128L;

    private String serverName;
    ArrayList<Cliente> clientesOn;
    private String address;
    private int port;

    public Server(String serverName, ArrayList<Cliente> clientesOn) {
        this.serverName = serverName;
        this.clientesOn = clientesOn;
    }

    public Server(String serverName, ArrayList<Cliente> clientesOn, String address, int port) {
        this.serverName = serverName;
        this.clientesOn = clientesOn;
        this.address = address;
        this.port = port;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

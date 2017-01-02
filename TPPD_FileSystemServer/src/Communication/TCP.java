package Communication;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCP extends Communication implements Serializable
{
    Socket socket;
    Cliente cli;
    
    public TCP(Socket socket, Cliente cli) {
        this.socket = socket;
        this.cli = cli;
    }
    
    public TCP(String hostname, int port) {
        try {
            this.socket = new Socket(hostname, port);
        } catch (UnknownHostException ex) {
        } catch (IOException ex) {}
    }
    
    public Cliente getCli() {
        return cli;
    }

    public void setCli(Cliente cli) {
        this.cli = cli;
    }
}

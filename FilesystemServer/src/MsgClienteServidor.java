import java.io.Serializable;
import java.net.InetAddress;

public class MsgClienteServidor implements Serializable
{
    private static final long serialVersionUID = 1010L;
    
    private Cliente cli;
    private InetAddress addr;
    private int port;
    
    public MsgClienteServidor(InetAddress addr, int port) {
        this.addr = addr;
        this.port = port;
    }
    
}

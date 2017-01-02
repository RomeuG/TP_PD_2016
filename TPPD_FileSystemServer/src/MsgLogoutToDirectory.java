import java.io.Serializable;
import java.net.InetAddress;

public class MsgLogoutToDirectory implements Serializable
{
    private static final long serialVersionUID = 1010L;
    
    private String cli;
    private InetAddress addr;
    private int port;
    
    public MsgLogoutToDirectory(String cli, InetAddress addr, int port) {
        this.cli = cli;
        this.addr = addr;
        this.port = port;
    }
    
}

import java.io.Serializable;
import java.util.List;

public class Heartbeat implements Serializable
{
    private static final long serialVersionUID = 7526472295622776147L;
    
    private String ip;
    private int port;
    private List<clientes>;
    
    // sends a heartbeat message to the directory service every 30 seconds
    public Heartbeat(String ip, int port) {
        
    }
    
    public void start() {
        sendMessage("Arrived");
    }
    
    public void stop() {
        sendMessage("Departed");
    }
}

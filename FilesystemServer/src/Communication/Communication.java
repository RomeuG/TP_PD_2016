package Communication;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

 /* Esta classe abstrata mostra como construir uma nova comunicacao */
public abstract class Communication 
{
    final int BUFSIZE = 4096;
    final String ADDR = "127.0.0.1";
    final int PORT = 7000;
    
    public static String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            return null;
        }
    }
    
    public abstract void setSoTimeout(int timeout);
    public abstract void sendMessage(Serializable message);
    public abstract Object receiveMessage();
    public abstract void sendListMessage(List<Serializable> list);
    public abstract List<Object> receiveListMessage();
    public abstract void close();
}

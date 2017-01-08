

import java.net.InetAddress;
import java.net.UnknownHostException;

 /* Esta classe abstrata mostra como construir uma nova comunicacao */
public abstract class Communication 
{
    final String ADDR = "127.0.0.1";
    final int PORT = 5001;
    
    public static String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            return null;
        }
    }
}

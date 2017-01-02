package Transfers;

import java.net.Socket;

/**
 *
 * @author João
 */
public interface FileTransfers 
{
    void updateObservers(Object arg);
    void transferir(String filePathName, Socket socket);
}

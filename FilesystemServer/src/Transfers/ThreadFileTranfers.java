package Transfers;

import java.io.IOException;
import java.net.Socket;
import java.util.PriorityQueue;

/**
 *
 * @author João
 */
public abstract class ThreadFileTranfers extends Thread 
{
    public enum TIPO { Download, Upload };
    protected FileTransfers transferFile;
    protected PriorityQueue<String> filesPath;
    protected Socket socket;
    
    protected abstract void inicializarSocket();
    
    @Override
    public void run() {
        inicializarSocket();
        
        while(true) {
            String fileName = filesPath.poll();
            if(fileName == null)
                break;
            else
                transferFile.transferir(fileName, socket);
        }
        try {
            socket.close();
        } catch (IOException ex) {
            System.err.println("[Thread File Transfers] " + ex);
        }
    }
}

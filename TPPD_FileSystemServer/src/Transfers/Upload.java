package Transfers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;

public class Upload extends Observable implements FileTransfers 
{
    private int ALLOC_SIZE = 51200;
    private int nFiles;
    private int count;
    
    public Upload(int nFiles) {
        this.nFiles = nFiles;
        count = 0;
    }
    
    @Override
    public void updateObservers(Object arg) {
        setChanged();
        notifyObservers(arg);
    }
    
    @Override
    public void transferir(String filePathName, Socket socket) {
        File file = new File(filePathName);
        
        // 1. Preparar para enviar mensagens
        // Object String format: download:<fileName>:<fileSize>
        sendObjectMessage(socket, "download:"+file.getName()+":"+file.length());
        
        // 2. Enviar ficheiro
        try {
            OutputStream out = socket.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[ALLOC_SIZE];
            System.out.println("Upload started!");
            long totalBytes = 1;
            
            while(true) {
                double percent = (totalBytes*100)/(file.length()*1.0);
                updateObservers(Math.round(percent)+"%");
                int bytesRead = fis.read(buffer);
                if(bytesRead == -1)
                    break;
                out.write(buffer);
                totalBytes += bytesRead;
            }
            System.out.println("Upload complete!");
            fis.close();
        } catch (IOException e) {
            System.err.println("[Upload Strategy] " + e);
            updateObservers("[Upload Strategy] " + e);
        }
        
        count++;
        if(count == nFiles)
            sendObjectMessage(socket, "download:complete");
    }
    
    private void sendObjectMessage(Socket socket, Object message) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.err.println("[Upload Strategy] " + e);
            updateObservers("[Upload Strategy] " + e);
        }
    }
}

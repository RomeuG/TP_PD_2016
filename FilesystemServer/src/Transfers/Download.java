package Transfers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Observable;

public class Download extends Observable implements FileTransfers 
{
    private int ALLOC_SIZE = 51200;
    
    @Override
    public void updateObservers(Object arg) {
        setChanged();
        notifyObservers(arg);
    }
    
    @Override
    public void transferir(String folderName, Socket socket) {
        String fileName;
        long fileSize;
        
        while(true) 
        {
            // 1. Preparar para receber mensagens
            String str = (String) readObjectMessage(socket);
            if("download:complete".equals(str))
                break; // Download de ficheiros terminado
            String [] splitted = str.split(":");
            fileName = splitted[1];
            fileSize = Long.parseLong(splitted[2]);

            // 2. Receber ficheiro
            try {
                InputStream is = socket.getInputStream();
                FileOutputStream fos = new FileOutputStream(folderName + "/" + fileName);
                byte[] buffer = new byte[ALLOC_SIZE];
                System.out.println("Download started!");
                long totalBytes = 1;
                long nBlocos = (fileSize/ALLOC_SIZE) + 1;
                
                for(long i=0; i<nBlocos; i++) {
                    double percent = (totalBytes*100)/(fileSize*1.0);
                    updateObservers(Math.round(percent)+"%");
                    int bytesReceived = is.read(buffer);
                    
                    fos.write(buffer);
                    totalBytes += bytesReceived;
                }
                updateObservers("100%");
                System.out.println("Download complete!");
                fos.close();
            } catch (IOException e) {
                System.err.println("[Download Strategy] " + e);
                updateObservers("[Download Strategy] " + e);
                break;
            }
        }
    }
    
    private Object readObjectMessage(Socket socket) {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            // Object String format: download:<fileName>:<fileSize>
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Download Strategy] " + e);
            updateObservers("[Download Strategy] " + e);
            return null;
        }
    }
}

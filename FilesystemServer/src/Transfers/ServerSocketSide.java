package Transfers;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Observer;
import java.util.PriorityQueue;

public class ServerSocketSide extends ThreadFileTranfers
{
    private int tcp_port;
    
    private ServerSocketSide(Observer o, TIPO tipo, int tcp_port, int nFiles) {
        this.tcp_port = tcp_port;
        
        switch(tipo) {
            case Download:
                Download d = new Download();
                d.addObserver(o);
                transferFile = d;
                break;
                
            case Upload:
                Upload u = new Upload(nFiles);
                u.addObserver(o);
                transferFile = u;
                break;
        }
    }
    
    public ServerSocketSide(Observer o, TIPO tipo, int tcp_port, String filePath) {
        this(o, tipo, tcp_port, 1);
        
        filesPath = new PriorityQueue<>();
        filesPath.add(filePath);
    }
    
    public ServerSocketSide(Observer o, TIPO tipo, int tcp_port, List<String> filesPathName) {
        this(o, tipo, tcp_port, filesPathName.size());
        
        filesPath = new PriorityQueue<>();
        filesPath.addAll(filesPathName);
    }

    @Override
    protected void inicializarSocket() {
        ServerSocket s;
        
        try {
            s = new ServerSocket(tcp_port);
            socket = s.accept();
        } catch (IOException ex) {
            System.err.println("[Thread ServerSocket] " + ex);
        }
    }
}

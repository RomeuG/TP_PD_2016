package Transfers;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Observer;
import java.util.PriorityQueue;

public class SocketSide extends ThreadFileTranfers 
{
    private String tcp_ip;
    private int tcp_port;
    
    private SocketSide(Observer o, TIPO tipo, String tcp_ip, int tcp_port, int nFiles) {
        this.tcp_ip = tcp_ip;
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
    
    public SocketSide(Observer o, TIPO tipo, String tcp_ip, int tcp_port, String filePath) {
        this(o, tipo, tcp_ip, tcp_port, 1);
        
        filesPath = new PriorityQueue<>();
        filesPath.add(filePath);
    }
    
    public SocketSide(Observer o, TIPO tipo, String tcp_ip, int tcp_port, List<String> filesPathName) {
        this(o, tipo, tcp_ip, tcp_port, filesPathName.size());
        
        filesPath = new PriorityQueue<>();
        filesPath.addAll(filesPathName);
    }

    @Override
    protected void inicializarSocket() {
        try {
            socket = new Socket(tcp_ip, tcp_port);
        } catch (IOException ex) {
            System.err.println("[Thread Socket] " + ex);
        }
    }
    
}

package Communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TCP extends Communication implements Serializable
{
    Socket socket;
    Cliente cli;
    
    public TCP(Socket socket, Cliente cli) {
        this.socket = socket;
        this.cli = cli;
    }
    
    public TCP(String hostname, int port) {
        try {
            this.socket = new Socket(hostname, port);
        } catch (UnknownHostException ex) {
        } catch (IOException ex) {}
    }
    
    public Cliente getCli() {
        return cli;
    }

    public void setCli(Cliente cli) {
        this.cli = cli;
    }
    
    @Override
    public void setSoTimeout(int timeout) {
        try {
            socket.setSoTimeout(timeout);
        } catch (SocketException ex) {}
    }
    
    @Override
    public void sendMessage(Serializable message) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(message);
            out.flush();
        } catch (IOException ex) {}
    }
    
    @Override
    public Object receiveMessage() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object messageReceived = in.readObject();
            return messageReceived;
        } catch (IOException | ClassNotFoundException ex) {}
        
        return null;
    }
    
    @Override
    public void sendListMessage(List<Serializable> list) {
        sendMessage(new Integer(list.size()));
        for(Serializable e : list)
            sendMessage(e);
    }
    
    @Override
    public List<Object> receiveListMessage() {
        List<Object> list = new ArrayList<>();
        Integer nElements = (Integer)receiveMessage();
        
        if(nElements>0) {
            for(int i=0; i<nElements; i++)
                list.add(receiveMessage());
        }
        
        return list;
    }
    
    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {}
    }
}

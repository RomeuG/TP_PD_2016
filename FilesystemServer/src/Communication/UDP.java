package Communication;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class UDP extends Communication 
{
    private DatagramSocket socket;
    private DatagramPacket packet;
    private InetAddress addr;
    private byte[] b;

    public UDP() {
        try {
            socket = new DatagramSocket();
        } catch (SocketException ex) {}
        
        try {
            addr = InetAddress.getByName(ADDR);
        } catch (UnknownHostException e) {}
        
        b = new byte[BUFSIZE];
        packet = new DatagramPacket(b, b.length, addr, PORT);
    }
    
    @Override
    public void setSoTimeout(int timeout) {
        try {
            socket.setSoTimeout(timeout);
        } catch (SocketException ex) {}
    }

    @Override
    public void sendMessage(Serializable message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object receiveMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sendListMessage(List<Serializable> list) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Object> receiveListMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
        socket.close();
    }
}

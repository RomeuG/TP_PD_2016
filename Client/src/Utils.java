
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Utils implements InterfaceCli
{
    Socket sock;
    static String ip;
    static int port;
    
    public Utils(String ip, int port) {
        this.ip = ip;
        this.port = port;
        
        try {
            sock = new Socket(InetAddress.getByName(ip), port);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public boolean register(String username, String password) {
        ObjectInputStream in;
        ObjectOutputStream out;
        Object obj;
        
        try {
            in = new ObjectInputStream(sock.getInputStream());
            out = new ObjectOutputStream(sock.getOutputStream());
            out.writeObject(username+":"+password);
            out.flush();
            
            obj = in.readObject();
            
            if(obj instanceof String)
            {
                String str = (String) obj;
            
                if(str.equals("<Regist_Success>"))
                    return true;
            
                if(str.equals("<Regist_Failed>"))
                    return false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        return false;
    }

    @Override
    public boolean login(String username, String password) {
        ObjectInputStream in;
        ObjectOutputStream out;
        Object obj;
        
        try {
            in = new ObjectInputStream(sock.getInputStream());
            out = new ObjectOutputStream(sock.getOutputStream());
            out.writeObject(username+":"+password);
            out.flush();
            
            obj = in.readObject();
            
            if(obj instanceof String)
            {
                String str = (String) obj;
            
                if(str.equals("<Login_Success>"))
                    return true;
            
                if(str.equals("<Login_Failed>"))
                    return false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        return false;
    }

    @Override
    public boolean logout(String username) {
        ObjectInputStream in;
        ObjectOutputStream out;
        Object obj;
        
        try {
            in = new ObjectInputStream(sock.getInputStream());
            out = new ObjectOutputStream(sock.getOutputStream());
            out.writeObject(username+":logout");
            out.flush();
            
            obj = in.readObject();
            
            if(obj instanceof String)
            {
                String str = (String) obj;
            
                if(str.equals("<Logout_OK>"))
                    return true;
                return false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        return false;
    }

    @Override
    public boolean copyFile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean moveFile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeFile() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

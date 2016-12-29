
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Utils implements InterfaceCli
{
     private Socket sock;
     private String ip;
     private int port;
    
    // ALTERAR
    public Utils(String ip, int port) {
        //this.ip = ip;
        //this.port = port;
        this.ip = "10.65.132.42";
        this.port = 7000;
        
        try {
            sock = new Socket(InetAddress.getByName(this.ip), this.port);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Socket getSock() {
        return sock;
    }
    public String getIp() {
        return ip;
    }
    public int getPort() {
        return port;
    }
    
    @Override
    public boolean register(String username, String password) {
        ObjectInputStream in;
        ObjectOutputStream out;
        Object obj;
        
        try {
            out = new ObjectOutputStream(sock.getOutputStream());
            out.writeObject("registar:"+username+":"+password);
            out.flush();
            
            in = new ObjectInputStream(sock.getInputStream());
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
            out = new ObjectOutputStream(sock.getOutputStream());
            out.writeObject("login:"+username+":"+password);
            out.flush();
            
            in = new ObjectInputStream(sock.getInputStream());
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
    public boolean logout() {
        ObjectInputStream in;
        ObjectOutputStream out;
        Object obj;
        
        try {
            out = new ObjectOutputStream(sock.getOutputStream());
            out.writeObject("logout:");
            out.flush();
            
            in = new ObjectInputStream(sock.getInputStream());
            obj = in.readObject();
            
            if(obj instanceof String)
            {
                String str = (String) obj;
            
                return str.equals("<Logout_OK>");
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

    @Override
    public void getFileContent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean makeDir(String dir) {
        ObjectInputStream in;
        ObjectOutputStream out;
        Object obj;
        
        try {
            out = new ObjectOutputStream(sock.getOutputStream());
            out.writeObject("makedir:"+dir);
            out.flush();
            
            in = new ObjectInputStream(sock.getInputStream());
            obj = in.readObject();
            
            if(obj instanceof String)
            {
                String str = (String) obj;
            
                return str.equals("<Dir_Created>");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        return false;
    }

    @Override
    public boolean createFile(String fileName) {
        ObjectInputStream in;
        ObjectOutputStream out;
        Object obj;
        
        try {
            out = new ObjectOutputStream(sock.getOutputStream());
            out.writeObject("create_file:"+fileName);
            out.flush();
            
            in = new ObjectInputStream(sock.getInputStream());
            obj = in.readObject();
            
            if(obj instanceof String)
            {
                String str = (String) obj;
            
                return str.equals("<File_Created>");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        return false;
    }
}


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Utils implements InterfaceCli
{
    private Socket sock;
    private String ip;
    private int port;
    private String username;
     
    public Utils(String ip, int port) {
        this.ip = ip;
        this.port = port;
        
        try {
            sock = new Socket(InetAddress.getByName(this.ip.substring(1, this.ip.length())), this.port);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
    public boolean getFileContent(String path) {
        ObjectInputStream in;
        OutputStream out;
        ObjectOutputStream outs = null;
        
        try {
            outs = new ObjectOutputStream(sock.getOutputStream());
            outs.writeObject("download:"+path);
            outs.flush();
            
            String pattern = Pattern.quote(System.getProperty("file.separator"));
            String[] arr = path.split(pattern);
            
            out = new FileOutputStream(arr[arr.length-1]);
            in = new ObjectInputStream(sock.getInputStream());

            while (true) {
                Object o = in.readObject();
                
                if(o instanceof MsgSendFile)
                {
                    MsgSendFile msg = (MsgSendFile) o;
                    
                    if (msg.isMoreFile()) {
                        out.write(msg.getArr());
                    }
                    else
                        break;
                }
            }
            
            out.close();
            
            return true;
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        return false;
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
            out.writeObject("upload:create_file:"+fileName);
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

    @Override
    public DirectoryInfo changeWorkingDirectory(String path) {
        ObjectInputStream in;
        ObjectOutputStream out;
        Object obj;
        
        try {
            out = new ObjectOutputStream(sock.getOutputStream());
            out.writeObject("change_dir:"+path);
            out.flush();
            
            in = new ObjectInputStream(sock.getInputStream());
            obj = in.readObject();
            
            if(obj instanceof DirectoryInfo) {
                DirectoryInfo dir = (DirectoryInfo) obj;
            
                return dir;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
}

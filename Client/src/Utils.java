
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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
            System.out.println("Erro " + ex);
        } catch (IOException ex) {
            System.out.println("Erro " + ex);
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
            System.out.println("Erro " + ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro " + ex);
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
            System.out.println("Erro " + ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro " + ex);
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
            System.out.println("Erro " + ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro " + ex);
        }
        
        return false;
    }

    @Override
    public void copyFile(String src, String dst) {
        ObjectOutputStream out;
        
        try {
            out = new ObjectOutputStream(sock.getOutputStream());
            out.writeObject("cp:"+src+":"+dst);
            out.flush();
            
        } catch (IOException ex) {
            System.out.println("Erro " + ex);
        }
    }

    @Override
    public void moveFile(String src, String dst) {
        ObjectOutputStream out;
        
        try {
            out = new ObjectOutputStream(sock.getOutputStream());
            out.writeObject("mv:"+src+":"+dst);
            out.flush();
            
        } catch (IOException ex) {
            System.out.println("Erro " + ex);
        }
    }

    @Override
    public boolean removeFile(String path) {
        ObjectOutputStream out;
        
        try {
            out = new ObjectOutputStream(sock.getOutputStream());
            out.writeObject("rm:"+path);
            out.flush();
            
            return true;
        } catch (IOException ex) {
            System.out.println("Erro " + ex);
        }
        
        return false;
    }

    @Override
    public boolean getFileContent(String path) {
        ObjectOutputStream outs = null;
        ObjectInputStream ob;

        try {
            outs = new ObjectOutputStream(sock.getOutputStream());
            outs.writeObject("download:"+path);
            outs.flush();

            ob = new ObjectInputStream(sock.getInputStream());
            Long size = (Long) ob.readObject();
            int nbytes;
            InputStream in = sock.getInputStream();
            byte[] fileChunck = new byte[1024];

            String pattern = Pattern.quote(System.getProperty("file.separator"));
            String[] arr = path.split(pattern);

            FileOutputStream localFileOutputStream = new FileOutputStream(arr[arr.length - 1]);

            Long count = 0L;
            
            if(size == 0)
                return false;
            
            while ((nbytes = in.read(fileChunck)) > 0) {
                //System.out.println("Recebido o bloco n. " + ++contador + " com " + nbytes + " bytes.");
                localFileOutputStream.write(fileChunck, 0, nbytes);
                //System.out.println("Acrescentados " + nbytes + " bytes ao ficheiro " + localFilePath+ ".");
                count += nbytes;
                if(count >= size)
                    break;
            }

            System.out.println("Transferencia concluida");
            return true;
        } catch (IOException ex) {
            System.out.println("Erro " + ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro " + ex);
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
            System.out.println("Erro " + ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro " + ex);
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
            System.out.println("Erro " + ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro " + ex);
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
            System.out.println("Erro " + ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Erro " + ex);
        }
        
        return null;
    }
}

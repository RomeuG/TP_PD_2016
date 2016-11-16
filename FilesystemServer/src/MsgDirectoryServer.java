import java.io.Serializable;

public class MsgDirectoryServer implements Serializable
{
    private static final long serialVersionUID = 7526472295622776147L;
    
    private FileServer server;
    
    public MsgDirectoryServer(FileServer server) {
        this.server = server;
    }

    public FileServer getServer() {
        return server;
    }
    public void setServer(FileServer server) {
        this.server = server;
    }
}

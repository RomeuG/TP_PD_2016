
import java.io.Serializable;
import java.util.ArrayList;


public class DirectoryInfo implements Serializable 
{
    private static final long serialVersionUID = 10130L;
    
    ArrayList<String> dir;
    ArrayList <String> ficheirosName;

    public DirectoryInfo(ArrayList dir, ArrayList ficheirosName) {
        this.dir = dir;
        this.ficheirosName = ficheirosName;
    }

    public ArrayList<String> getDir() {
        return dir;
    }

    public void setDir(ArrayList<String> dir) {
        this.dir = dir;
    }

    public ArrayList<String> getFicheirosName() {
        return ficheirosName;
    }

    public void setFicheirosName(ArrayList<String> ficheirosName) {
        this.ficheirosName = ficheirosName;
    }
}

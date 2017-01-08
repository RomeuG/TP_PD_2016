
import java.io.Serializable;


public class MsgSendFile implements Serializable 
{
    private byte[] arr;
    private boolean moreFile;
    
    public MsgSendFile(boolean moreFile) {
        this.arr = new byte[4096];
        this.moreFile = moreFile;
    }

    public byte[] getArr() {
        return arr;
    }

    public void setArr(byte[] arr) {
        this.arr = arr;
    }

    public boolean isMoreFile() {
        return moreFile;
    }

    public void setMoreFile(boolean moreFile) {
        this.moreFile = moreFile;
    }
}

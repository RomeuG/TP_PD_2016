
import java.io.Serializable;

public class ServerInfo implements Serializable {
    private String nome;
    private String ip;
    private int port;

    public ServerInfo(String nome, String ip, int port) {
        this.nome = nome;
        this.ip = ip;
        this.port = port;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }  
}

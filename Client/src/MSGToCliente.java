import java.io.Serializable;

/**
 * Created by romeu on 1/7/17.
 */
public class MSGToCliente implements Serializable {
    private static final long serialVersionUID = 10133L;

    String destinatario;
    String rementente;
    String mensagem;
    String serverName;

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getRementente() {
        return rementente;
    }

    public void setRementente(String rementente) {
        this.rementente = rementente;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public MSGToCliente(String destinatario, String rementente, String mensagem, String serverName) {
        this.destinatario = destinatario;
        this.rementente = rementente;
        this.mensagem = mensagem;
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}

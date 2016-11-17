public class MsgNewClient extends MsgDirectoryServer
{
    private Cliente cliLogado;
    
    public MsgNewClient(FileServer server, Cliente cliLogado) {
        super(server);
        
        this.cliLogado = cliLogado;
    }

    public Cliente getCliLogado() {
        return cliLogado;
    }
    public void setCliLogado(Cliente cliLogado) {
        this.cliLogado = cliLogado;
    }
    
}

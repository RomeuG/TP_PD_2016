
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MsgDirectoryServer implements Serializable
{
    private static final long serialVersionUID = 10127L;
    int PORTO_UDP;
    private List<Cliente> listaDeClientes;
    boolean ligado;
    
    public MsgDirectoryServer(int PORTO_UDP) {
        listaDeClientes = new ArrayList<>();
        ligado = true;
        this.PORTO_UDP = PORTO_UDP;
    }

    public void addCliente(Cliente cliente)
    {
        listaDeClientes.add(cliente);
    }

    public void removeCliente(Cliente cliente)
    {
        listaDeClientes.remove(cliente);
    }



}

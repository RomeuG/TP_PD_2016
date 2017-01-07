import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by romeu on 1/7/17.
 */
public class MsgListaClientes implements Serializable {

    private List<Cliente> listaClientes;

    MsgListaClientes(ArrayList<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    public List<Cliente> getListaClientes() {
        return listaClientes;
    }

    public void setListaClientes(List<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }
}

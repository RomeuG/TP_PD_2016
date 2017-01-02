import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by romeu on 12/31/16.
 */
public class InfoParaCliente implements Serializable {

    private ArrayList<Server> listaServers;

    InfoParaCliente(ArrayList<Server> listaServers) {
        this.listaServers = listaServers;
    }

    public ArrayList<Server> getListaServers() {
        return listaServers;
    }

    public void setListaServers(ArrayList<Server> listaServers) {
        this.listaServers = listaServers;
    }
}

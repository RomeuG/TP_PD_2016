package Communication;

import java.io.Serializable;

public class Cliente implements Serializable
{
    private String username;
    private String password;
    // ip serv directoria, porto
    // List<servidor>
    // logado
    
    public Cliente(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}

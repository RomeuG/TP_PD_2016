public class Cliente
{
    private String username;
    private String password;
    private boolean logado;
    private String IP_CLIENTE;
    private int PORTO_CLIENTE;
    
    public Cliente(String username, String password, String IP_CLIENTE, int PORTO_CLIENTE) {
        this.username = username;
        this.password = password;
        this.IP_CLIENTE = IP_CLIENTE;
        this.PORTO_CLIENTE = PORTO_CLIENTE;
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

    public String getIP_CLIENTE() {
        return IP_CLIENTE;
    }

    public int getPORTO_CLIENTE() {
        return PORTO_CLIENTE;
    }
}

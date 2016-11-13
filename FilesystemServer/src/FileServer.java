import Communication.*;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class FileServer 
{
    public int PORT_TCP = 63000;
    private String IP_TCP;
    
    private final String defaultFolder = "Storage";
    private List<TCP> clientes;
    
    public FileServer() {
        // Abrir/ Criar diretório principal do sistema de ficheiros
        if (CreateDirectory(".", defaultFolder))
            System.out.println("Root Created");
        
        clientes = new ArrayList<>();
    }
    
    /**
     * This method start a Thread to start accepting clients and 
     * a Thread to send a HeartBeat to the Directory Service 
     * and reuse the main thread to receive commands.
     */
    public void start() {
        AtendeClientes tAtende = null;
        
        try {
            tAtende = new AtendeClientes();
            tAtende.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String adminMessage;
        do {
            Scanner sc = new Scanner(System.in);
            System.out.print(" >> ");
            adminMessage = sc.next();
        } while(!"exit".equals(adminMessage));
        
        
        // Encerrar todas as Threads a correr
        try {
            tAtende.setRunning(false);
            tAtende.join();
        } catch (InterruptedException e) {}
        
        System.out.println("[Remote Server] shutdown");
    }
    
    /* Create a Directory with specified 'dName' in path */
    private boolean CreateDirectory(String path, String dName) {
        File theDir = new File(path + "/" + dName);
        
        // if the directory does not exist, create it
        if (!theDir.exists())
          return theDir.mkdir();
        
        return true;
    }
    
    public synchronized void defineIP_TCP(String ipTCP) {
        IP_TCP = ipTCP;
    }
    
    /**
     * Thread -> Accept new clients
     */
    class AtendeClientes extends Thread 
    {
        private ServerSocket serverSocket;
        private List<AtendeCliente> clientThreads;
        private boolean running;

        public AtendeClientes() throws IOException {
            serverSocket = new ServerSocket(PORT_TCP);
            serverSocket.setSoTimeout(10000);
            defineIP_TCP(Communication.getHostAddress());
            
            clientThreads = new ArrayList<AtendeCliente>();
            running = true;
        }

        public synchronized void setRunning(boolean running) {
            this.running = running;
        }
        
        @Override
        public void run() {
            synchronized(running) {
                System.out.println("[Thread Atendimento Clientes] A aguardar novos clientes");
                while(running) {
                    try {
                        Socket s = serverSocket.accept();
                        TCP t = new TCP(s);
                        clientes.add(t);
                        AtendeCliente atendeCliente = new AtendeCliente(t);
                        clientThreads.add(atendeCliente);
                        atendeCliente.start();
                    } catch (IOException ex) {}
                }
            }
            
            System.out.println( "[Thread Atendimento Clientes] Server is not accepting new clients anymore");
            
            // Warn all clients
            for(int i=0; i<clientThreads.size(); i++) {
                try {
                    clientThreads.get(i).setRunning(false);
                    clientThreads.get(i).join();
                } catch (InterruptedException ex) {}
            }
            try {
                serverSocket.close();
            } catch (IOException ex) {}
            
            // Warn Directory Service
            // ...
        }
    }
    
    /**
     * Thread -> Manage single client
     */
    class AtendeCliente extends Thread implements Observer 
    {
        private TCP socket;
        private boolean running;
        
        public AtendeCliente(TCP socket) {
            this.socket = socket;
            running = true;
        }

        public synchronized void setRunning(boolean running) {
            this.running = running;
        }
        
        @Override
        public void run() {
            synchronized(running) {
                while(running) {
                    String msg = (String) socket.receiveMessage();
                    String[] splitted = msg.split(":");
                    switch(splitted[0]) {
                        case "login":
                            executarComandoLogin(splitted);
                            break;
                            
                        case "logout":
                            executarComandoLogout(splitted);
                            break;
                            
                        case "registar":
                            executarComandoRegistar(splitted);
                            break;
                            
                        case "download":
                            executarComandoDownload(splitted);
                            break;
                            
                        case "upload":
                            executarComandoUpload(splitted);
                            break;
                            
                        case "exit":
                            executarComandoExit();
                            break;
                    }
                }
                socket.sendMessage("exit");
                socket.close();
            }
        }
        
        private void executarComandoLogin(String[] splittedList) {
            
        }
        
        private void executarComandoLogout(String[] splittedList) {
            
        }
        
        private void executarComandoRegistar(String[] splittedList) {
            
        }
        
        private void executarComandoDownload(String[] splittedList) {
            
        }
        
        private void executarComandoUpload(String [] splittedList) {
            
        }
        
        private void executarComandoExit() {
            setRunning(false);
        }
        
        private void sendUpdates() {
            // Enviar Lista de Clientes
            
            // Enviar Lista de Ficheiros
            
        }
        
        @Override
        public void update(Observable o, Object arg) {
            System.out.println(arg);
        }
    }
}

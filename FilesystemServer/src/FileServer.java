import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileServer 
{
    public int PORT_TCP = 63000;
    
    private final String defaultFolder = "Storage";
    private List<ServerSocket> clientes;
    
    public FileServer() {
        // Abrir/ Criar diretório principal do sistema de ficheiros
        if (CreateDirectory(".", defaultFolder))
            System.out.println("Root Created");
        
        clientes = new ArrayList<>();
    }
    
    /**
     * This method start a Thread to start accepting clients and reuse the main
     *  thread to receive commands.
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
                        clientes.add(serverSocket);
                        AtendeCliente atendeCliente = new AtendeCliente(s);
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
        }
    }
    
    /**
     * Thread -> Manage single client
     */
    class AtendeCliente extends Thread implements Observer 
    {
        private Socket socket;
        private boolean running;
        
        public AtendeCliente(Socket socket) {
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
                    ObjectInputStream in = null;
                    try {
                        in = new ObjectInputStream(socket.getInputStream());
                        String message = (String) in.readObject();
                        String[] splitted = message.split(":");
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
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
            
        }
        
        @Override
        public void update(Observable o, Object arg) {
            System.out.println(arg);
        }
    }
}

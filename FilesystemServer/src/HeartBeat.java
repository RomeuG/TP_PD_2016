import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

/**
 * Created by Andre on 17/11/2016.
 */
class HeartBeat extends Thread
{
    private DatagramSocket s;
    private InetAddress addr;
    private Boolean running;
    private int PORTO_UDP_DIRECTORY_SERVER;
    private int meuPorto;

    // Construtor
    public HeartBeat(String IP_UDP_DIRECTORY_SERVER, int PORTO_UDP_DIRECTORY_SERVER, int meuPorto) throws SocketException, UnknownHostException {
        s = new DatagramSocket();
        addr = InetAddress.getByName(IP_UDP_DIRECTORY_SERVER);
        this.meuPorto = meuPorto;
        this.PORTO_UDP_DIRECTORY_SERVER = PORTO_UDP_DIRECTORY_SERVER;
        running = true;
    }

    public synchronized void setRunning(Boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        synchronized(running) {
            while(running) {
                try {
                    ByteArrayOutputStream bout = null;
                    ObjectOutputStream out = null;
                    DatagramPacket sendP;

                    s.setSoTimeout(10000);

                    bout = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(bout);
                    out.writeObject(new MsgDirectoryServer(meuPorto));
                    out.flush();

                    sendP = new DatagramPacket(bout.toByteArray(), bout.size(), addr, PORTO_UDP_DIRECTORY_SERVER);

                    s.send(sendP);
                } catch (UnknownHostException e) {
                    System.out.println("Erro " + e);
                } catch (NumberFormatException e) {
                    System.out.println("Erro " + e);
                } catch (SocketTimeoutException e) {
                    System.out.println("Erro " + e);
                } catch (IOException e) {
                    System.out.println("Erro " + e);
                }

                try {
                    running.wait(500);
                } catch (InterruptedException ex) {}
            }
        }
    }
}
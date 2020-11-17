
import javax.swing.*;
import java.net.InetAddress;

public class UdpTransmitter extends TimedTransmitter {
    private final UdpSocket socket;
    private final int port;
    private final InetAddress dest;

    public UdpTransmitter(UdpSocket sc, JTextArea source, InetAddress dest, int port) {
        super (source);
        this.dest = dest;
        this.port = port;
        this.socket = sc;
    }

    void doSend (byte[] buff)
    {
        socket.sendDirect (dest, port, buff);
    }
}

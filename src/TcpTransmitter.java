import javax.swing.*;

public class TcpTransmitter extends PeriodicTransmitter {
    private final TcpSocket socket;

    public TcpTransmitter(TcpSocket sc, JTextArea source) {
        super (source);
        this.socket = sc;
    }

    void doSend (byte[] buff)
    {
        socket.sendDirect(buff);
    }
}

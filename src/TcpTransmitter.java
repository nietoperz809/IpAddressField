import javax.swing.*;

public class TcpTransmitter extends TimedTransmitter {
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

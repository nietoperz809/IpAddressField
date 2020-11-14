
import javax.swing.*;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class PeriodicalUdpTransmitter {
    private final JTextArea source;
    private final UdpSocket udpTransmitter;
    private final int port;
    private final InetAddress dest;
    private Timer timer;

    public PeriodicalUdpTransmitter(UdpSocket sc, JTextArea source, InetAddress dest, int port) {
        this.dest = dest;
        this.port = port;
        this.udpTransmitter = sc;
        this.source = source;
    }

    public void start(int millisecs, boolean asHex) throws Exception {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                byte[] buff;
                if (!asHex)
                    buff = Utils.unescape(source.getText()).getBytes();
                else
                    buff = Utils.readHex(source.getText());
                udpTransmitter.sendDirect (dest, port, buff);
            }
        }, 0, millisecs);
    }

    public void stop()
    {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}

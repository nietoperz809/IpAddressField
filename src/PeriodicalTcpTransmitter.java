import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class PeriodicalTcpTransmitter {
    private final JTextArea source;
    private final TcpSocket tcpTransmitter;
    private Timer timer;

    public PeriodicalTcpTransmitter(TcpSocket sc, JTextArea source) {
        this.tcpTransmitter = sc;
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
                tcpTransmitter.sendDirect(buff);
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

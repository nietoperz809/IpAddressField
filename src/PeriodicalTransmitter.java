import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class PeriodicalTransmitter {
    private final JTextArea source;
    private final SocketClass transmitter;
    private Timer timer;

    public PeriodicalTransmitter(SocketClass sc, JTextArea source) {
        this.transmitter = sc;
        this.source = source;
    }

    public void start(int millisecs, boolean asHex) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                byte[] buff;
                if (!asHex)
                    buff = source.getText().getBytes();
                else
                    buff = Utils.readHex(source.getText());
                transmitter.sendDirect(buff);
            }
        }, 0, millisecs);
    }

    public void stop() {
        timer.cancel();
        timer = null;
    }
}

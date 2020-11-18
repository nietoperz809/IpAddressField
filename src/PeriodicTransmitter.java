import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class PeriodicTransmitter {
    JTextArea source;
    ScheduledExecutorService sched;

    public PeriodicTransmitter(JTextArea src) {
        source = src;
    }

    abstract void doSend(byte[] buff);

    public void start(int millisecs, boolean asHex) throws Exception {
        sched = Executors.newScheduledThreadPool(1);
        sched.scheduleAtFixedRate(() -> {
            byte[] buff;
            if (!asHex)
                buff = Utils.unescape(source.getText()).getBytes();
            else
                buff = Utils.readHex(source.getText());
            doSend(buff);
        }, 0, millisecs, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (sched != null) {
            sched.shutdown();
            sched = null;
        }
    }
}

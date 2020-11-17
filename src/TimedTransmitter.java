import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public abstract class TimedTransmitter
{
    JTextArea source;
    Timer timer;

    public TimedTransmitter (JTextArea src)
    {
        source = src;
    }

    abstract void doSend (byte[] buff);

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
                doSend (buff);
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

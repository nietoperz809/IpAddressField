import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utils {
    static private final char[] hex = "0123456789ABCDEF".toCharArray();
    static public final ExecutorService exec = Executors.newFixedThreadPool(10);

    public static byte[] readHex(String in) {
        ArrayList<Byte> arr = new ArrayList<>();
        int state = 0;
        int bt = 0;
        for (char c : in.toCharArray()) {
            c = Character.toLowerCase(c);
            int a = 0;
            if (c >= '0' && c <= '9') {
                a = c - '0';
            } else if (c >= 'a' && c <= 'f') {
                a = c - 'a' + 10;
            } else
                continue;
            a &= 15;
            switch (state) {
                case 0:
                    bt = a << 4;
                    state = 1;
                    break;
                case 1:
                    bt = bt | a;
                    arr.add((byte) bt);
                    state = 0;
                    break;
            }
        }
        byte[] ret = new byte[arr.size()];
        for (int s = 0; s < ret.length; s++) {
            ret[s] = arr.get(s);
        }
        return ret;
    }

    public static String unescape(String in) {
        StringBuffer sb = new StringBuffer();
        int state = 0;
        for (char c : in.toCharArray()) {
            if (state == 0) {
                if (c == '\r' || c == '\n')
                    continue;
                if (c == '\\')
                    state = 1;
                else
                    sb.append(c);
            } else {
                if (c == 'r')
                    sb.append('\r');
                else if (c == 'n')
                    sb.append('\n');
                else if (c == '\\')
                    sb.append('\\');
                else
                    sb.append('\\').append(c);
                state = 0;
            }
        }
        if (state == 1)
            sb.append('\\');
        return sb.toString();
    }

    public static String toHex(byte[] in, int len) {
        StringBuilder buf = new StringBuilder();
        for (int s = 0; s < len; s++) {
            byte b = in[s];
            buf.append(hex[b >>> 4]);
            buf.append(hex[b & 0x0f]);
            buf.append(' ');
        }
        return buf.toString();
    }

    public static void infoBox(Component c, String msg, String title) {
        JOptionPane.showMessageDialog(c, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void errorBox(Component c, String msg, String title) {
        JOptionPane.showMessageDialog(c, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void playWaveFromResource(String name) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = new BufferedInputStream(loader.getResourceAsStream(name));
        playWave(is);
    }

    public static void playWave(InputStream is) {
        //CountDownLatch syncLatch = new CountDownLatch(1);
        try {
            Clip clip = AudioSystem.getClip();
//            clip.addLineListener(e ->
//            {
//                if (e.getType() == LineEvent.Type.STOP) {
//                    syncLatch.countDown();
//                }
//            });
            clip.open(AudioSystem.getAudioInputStream(is));
            clip.start();
            //syncLatch.await();
        } catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

//////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        String s = unescape("hello\\r\\n \\\\\r\n ");
        byte[] bb = s.getBytes();
        System.out.println(Arrays.toString(bb));
    }
}

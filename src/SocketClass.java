
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isRectangleContainingRectangle;

interface Callback {
    default void received(byte[] buff, int n) {
        System.out.println("received: " + n + " bytes");
    }

    default void connected() {
        System.out.println("connected");
    }

    default void closed() {
        System.out.println("closed");
    }

    default void error(String info) {
        System.out.println("error: " + info);
    }

    default void rxfail() {
        System.out.println("rxfail");
    }

}

public class SocketClass {
    Socket socket;
    ServerSocket srvs;
    Callback cb = new Callback() {
    };
    ExecutorService exec = Executors.newFixedThreadPool(10);

    public SocketClass(Callback cb)
    {
        this.cb = cb;
    }

    public SocketClass() {
    }

    public void setCallback (Callback cb)
    {
        this.cb = cb;
    }

    public void sendDirect (byte[] buff)
    {
        try {
            OutputStream os = socket.getOutputStream();
            os.write(buff);
        } catch (IOException e) {
            invokeLater(() -> cb.error("send failed"));
        }
    }

    public void send (byte[] buff)
    {
        exec.submit(() ->
        {
            sendDirect(buff);
        });
    }

    public void close() {
        if (srvs != null) {
            try {
                srvs.close();
                srvs = null;
            } catch (IOException e) {
                invokeLater(() -> cb.error("close failed"));
            }
        }
        if (socket != null) {
            exec.submit(() ->
            {
                try {
                    socket.close();
                    invokeLater(() -> cb.closed());
                    socket = null;
                } catch (IOException e) {
                    invokeLater(() -> cb.error("close failed"));
                }
            });
        } else {
            invokeLater(() -> cb.error("socket doesn't exist"));
        }
    }

    private void handleRx() {
        exec.submit(() -> {
            try {
                InputStream in = socket.getInputStream();
                while (true) {
                    byte[] buff = new byte[1024];
                    int n = in.read(buff);
                    if (n == -1)
                    {
                        socket.close();
                        if (srvs != null)
                            srvs.close();
                        invokeLater(() -> cb.closed());
                        return;
                    }
                    invokeLater(() -> cb.received(buff, n));
                }
            } catch (Exception e) {
                invokeLater(() -> cb.rxfail());
            }
        });
    }

    public void listen(int port) {
        exec.submit(() ->
        {
            try {
                srvs = new ServerSocket(port);
                socket = srvs.accept();
                invokeLater(() -> cb.connected());
                handleRx();
            } catch (IOException e) {
                invokeLater(() -> cb.error("listen failed"));
            }
        });
    }

    public void connect(InetAddress addr, int port) {
        exec.submit(() ->
        {
            try {
                socket = new Socket(addr, port);
                invokeLater(() -> cb.connected());
                handleRx();
            } catch (IOException e) {
                invokeLater(() -> cb.error("conn failed"));
            }
        });
    }
}

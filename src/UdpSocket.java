import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static javax.swing.SwingUtilities.invokeLater;

interface UDPCallback {
    default void txfail() {
        System.out.println("Transmit Error");
    }

    default void rxfail() {
        System.out.println("Receive Error");
    }

    default void rxdata (byte[] dat, int len) {
        System.out.println("Rx: "+dat.length+" bytes");
    }
}

public class UdpSocket {
    private final UDPCallback cb;

    public UdpSocket (UDPCallback cb)
    {
        this.cb = cb;
    }

    private DatagramSocket rxSocket;

    private void runRxLoop()
    {
        Utils.exec.submit(() -> {
            while (rxSocket != null)
            {
                byte[] buffer = new byte[1500];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    rxSocket.receive(packet);
                    invokeLater(() -> cb.rxdata(packet.getData(), packet.getLength()));
                } catch (IOException e) {
                    invokeLater(() -> cb.rxfail());
                    return;
                }
            }
        });
    }

    public void startReceive (InetAddress ip, int port)
    {
        try {
            rxSocket = new DatagramSocket (port, ip);
            rxSocket.setBroadcast(true);
            runRxLoop();
        } catch (SocketException e) {
            invokeLater(() -> cb.rxfail());
        }
    }

    public void stopReceive ()
    {
        if (rxSocket != null) {
            rxSocket.close();
            rxSocket = null;
        }
    }


    public void send (InetAddress ip, int port, byte[] dat)
    {
        Utils.exec.submit(() -> {
            DatagramPacket pkt = new DatagramPacket (dat, dat.length, ip, port);
            try {
                DatagramSocket sock = new DatagramSocket();
                sock.setBroadcast(true);
                sock.send (pkt);
            } catch (IOException e) {
                invokeLater(() -> cb.txfail());
            }
        });
    }

}

import javax.swing.*;
import java.awt.*;

public class UdpPanel extends JPanel {
    private final JToggleButton TxRepeat = new JToggleButton("Repeat ms");
    private final JCheckBox TxHex = new JCheckBox("Hex");
    private final JCheckBox RxHex = new JCheckBox("Hex");
    private final JTextField RxPort;
    private final JIp4Control TxIP;
    private final JTextField TxPort;
    private final JTextField TxPeriod = new JTextField();
    private final JIp4Control RxIP;
    private final JTextArea RxText = new JTextArea();
    private final JTextArea TxText = new JTextArea();
    private final JToggleButton RxTglButton = new JToggleButton("Rx");

    private UdpTransmitter udpTrans;

    private final UdpSocket sock = new UdpSocket(new UDPCallback() {
        @Override
        public void rxfail() {
            sock.stopReceive();
            RxTglButton.setSelected(false);
        }

        @Override
        public void txfail() {
            TxRepeat.setSelected(false);
            if (udpTrans != null)
                udpTrans.stop();
            Utils.errorBox (UdpPanel.this, "UDP Tx fail", "Socket error");
        }

        @Override
        public void rxdata(byte[] dat, int len) {
            if (RxHex.isSelected())
                RxText.append (Utils.toHex(dat, len));
            else
                RxText.append(new String (dat, 0, len));
        }
    });

    /**
     * Create the panel.
     */
    public UdpPanel() {
        setLayout(null);
        setBackground(Color.yellow);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(292, 43, 234, 223);
        add(scrollPane);
        scrollPane.setViewportView(RxText);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(10, 43, 250, 223);
        add(scrollPane_1);
        scrollPane_1.setViewportView(TxText);

        RxPort = new JTextField();
        RxPort.setBounds(461, 12, 65, 20);
        add(RxPort);
        RxPort.setColumns(10);

        TxIP = new JIp4Control();
        TxIP.putAddress(new byte[]{-1,-1,-1,-1});
        TxIP.setBounds(66, 12, 110, 20);
        add(TxIP);
        //TxIP.setColumns(10);

        TxPort = new JTextField();
        TxPort.setBounds(179, 12, 65, 20);
        add(TxPort);
        //TxPort.setColumns(10);

        JButton TxButton = new JButton("Tx");
        TxButton.addActionListener(e -> {
            try {
                byte[] buff;
                if (TxHex.isSelected())
                    buff = Utils.readHex(TxText.getText());
                else
                    buff = Utils.unescape(TxText.getText()).getBytes();
                sock.send (TxIP.getAddress(), Integer.parseInt(TxPort.getText()), buff);
            } catch (Exception ex) {
                System.out.println("Tx high level fail");
            }
        });
        TxButton.setMargin(new Insets(2, 2, 2, 2));
        TxButton.setBounds(29, 11, 30, 23);
        add(TxButton);

        TxRepeat.addActionListener(e -> {
            if (TxRepeat.isSelected())
            {
                try {
                    udpTrans = new UdpTransmitter(sock, TxText, TxIP.getAddress(), Integer.parseInt(TxPort.getText()));
                    udpTrans.start (Integer.parseInt(TxPeriod.getText()), TxHex.isSelected());
                } catch (Exception exception) {
                    TxRepeat.setSelected(false);
                }
            }
            else
            {
                udpTrans.stop();
                udpTrans = null;
            }
        });
        TxRepeat.setMargin(new Insets(2,2,2,2));
        TxRepeat.setBounds(35, 290, 86, 23);
        add(TxRepeat);

        TxPeriod.setBounds(127, 291, 86, 20);
        TxPeriod.setText("1000");
        add(TxPeriod);
        //TxPeriod.setColumns(10);

        TxHex.setBounds(96, 268, 97, 23);
        add(TxHex);

        RxHex.setBounds(386, 268, 97, 23);
        add(RxHex);

        RxIP = new JIp4Control();
        //RxIP.putAddress(new byte[]{(byte)255,(byte)255,(byte)255,(byte)255});
        RxIP.setBounds(342, 12, 117, 20);
        add(RxIP);
        //RxIP.setColumns(10);

        RxTglButton.addActionListener(e -> {
            if (RxTglButton.isSelected()) {
                try {
                    sock.startReceive(RxIP.getAddress(), Integer.parseInt(RxPort.getText()));
                } catch (Exception ex) {
                    RxTglButton.setSelected(false);
                }
            } else {
                sock.stopReceive();
            }
        });
        RxTglButton.setMargin(new Insets(2, 2, 2, 2));
        RxTglButton.setBounds(292, 9, 40, 23);
        add(RxTglButton);
    }
}


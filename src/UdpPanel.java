import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

public class UdpPanel extends JPanel {
    private JTextField RxPort;
    private JIp4Control TxIP;
    private JTextField TxPort;
    private JTextField TxPeriod;
    private JIp4Control RxIP;
    private final JTextArea RxText = new JTextArea();
    private final JToggleButton RxTglButton = new JToggleButton("Rx");

    private final UdpSocket sock = new UdpSocket(new UDPCallback() {
        @Override
        public void rxfail() {
            sock.stopReceive();
            RxTglButton.setSelected(false);
        }

        @Override
        public void rxdata(byte[] dat, int len) {
            String str = new String (dat, 0, len);
            RxText.append(str);
        }
    });

    /**
     * Create the panel.
     */
    public UdpPanel() {
        setLayout(null);
        //setBounds(0, 0, 536, 352);


        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(292, 43, 234, 223);
        add(scrollPane);

        scrollPane.setViewportView(RxText);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(10, 43, 250, 223);
        add(scrollPane_1);

        JTextArea TxText = new JTextArea();
        scrollPane_1.setViewportView(TxText);

        RxPort = new JTextField();
        RxPort.setBounds(461, 12, 65, 20);
        add(RxPort);
        RxPort.setColumns(10);

        TxIP = new JIp4Control();
        TxIP.setBounds(66, 12, 110, 20);
        add(TxIP);
        TxIP.setColumns(10);

        TxPort = new JTextField();
        TxPort.setBounds(179, 12, 65, 20);
        add(TxPort);
        TxPort.setColumns(10);

        JButton TxButton = new JButton("Tx");
        TxButton.setMargin(new Insets(2, 2, 2, 2));
        TxButton.setBounds(29, 11, 30, 23);
        add(TxButton);

        JCheckBox TxRepeat = new JCheckBox("Repeat >>");
        TxRepeat.setBounds(35, 290, 86, 23);
        add(TxRepeat);

        TxPeriod = new JTextField();
        TxPeriod.setBounds(127, 291, 86, 20);
        add(TxPeriod);
        TxPeriod.setColumns(10);

        JCheckBox TxHex = new JCheckBox("Hex");
        TxHex.setBounds(96, 268, 97, 23);
        add(TxHex);

        JCheckBox RxHex = new JCheckBox("Hex");
        RxHex.setBounds(386, 268, 97, 23);
        add(RxHex);

        RxIP = new JIp4Control();
        //RxIP.putAddress(new byte[]{(byte)255,(byte)255,(byte)255,(byte)255});
        RxIP.setBounds(342, 12, 117, 20);
        add(RxIP);
        RxIP.setColumns(10);

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


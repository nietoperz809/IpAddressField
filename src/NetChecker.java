import java.awt.EventQueue;
import java.nio.charset.Charset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.BevelBorder;

//@SuppressWarnings({"LocalVariableOfConcreteClass", "MagicNumber"})
public class NetChecker extends JFrame {
    private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    private final JPanel contentPane = new JPanel();
    private final JIp4AddressInput connectIP = new JIp4AddressInput();
    private final JTextField connectPort = new JTextField();
    private final JTextField listenPort = new JTextField();
    private final JCheckBox chckbxHexReceive = new JCheckBox("Hex");
    private final JCheckBox chckbxHexSend = new JCheckBox("Hex");
    private final JTextArea textAreaRx = new JTextArea();
    private final JTextArea textAreaTx = new JTextArea();

    private final SocketClass sc = new SocketClass(new Callback() {
        @Override
        public void received(byte[] buff, int n) {
            String str;
            if (chckbxHexReceive.isSelected()) {
                str = Utils.toHex(buff, n);
            } else {
                str = new String(buff, 0, n, Charset.defaultCharset());
            }
            textAreaRx.setCaretPosition(textAreaRx.getDocument().getLength());
            textAreaRx.replaceSelection(str);
        }
    });

    /**
     * Create the frame.
     */
    public NetChecker() {
        setTitle("NetTester");
        setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 552, 308);
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        tabbedPane.setBounds(0, 1, 536, 268);
        contentPane.add(tabbedPane);

        JPanel panel = new JPanel();
        tabbedPane.addTab("TCP", null, panel, null);
        panel.setLayout(null);

        JToggleButton tglbtnConnect = new JToggleButton("Connect");
//        tglbtnConnect.addActionListener(new ActionListener() {
//            SocketClass sc = new SocketClass();
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                if (tglbtnConnect.isSelected())
//                    sc.connect(connectIP.getAddress(), 80); // Google: 172.217.23.142
//                else
//                    sc.close();
//            }
//        });
        tglbtnConnect.setBounds(10, 11, 83, 23);
        panel.add(tglbtnConnect);

        connectIP.setBounds(103, 12, 110, 20);
        panel.add(connectIP);
        connectIP.setColumns(10);

        connectPort.setBounds(215, 12, 52, 20);
        panel.add(connectPort);
        connectPort.setColumns(10);

        JToggleButton tglbtnListen = new JToggleButton("Listen");
        tglbtnListen.addActionListener(e -> {
            if (tglbtnListen.isSelected()) {
                int port = Integer.parseInt(listenPort.getText().trim());
                sc.listen(port);
            } else
                sc.close();
        });

        tglbtnListen.setBounds(313, 11, 83, 23);
        panel.add(tglbtnListen);

        listenPort.setColumns(10);
        listenPort.setBounds(406, 12, 86, 20);
        panel.add(listenPort);

        textAreaTx.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        textAreaTx.setLineWrap(true);
        textAreaTx.setBounds(10, 37, 252, 175);
        panel.add(textAreaTx);

        JScrollPane sp1 = new JScrollPane(textAreaRx);
        textAreaRx.setLineWrap(true);
        sp1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        sp1.setBounds(272, 37, 252, 175);
        panel.add(sp1);

        JButton btnSend = new JButton("Send");
        btnSend.addActionListener(e -> {
            byte[] bt;
            String str = textAreaTx.getText();
            if (chckbxHexSend.isSelected()) {
                bt = Utils.readHex(str);
            } else {
                bt = str.getBytes();
            }
            sc.send(bt);
        });
        btnSend.setBounds(10, 217, 89, 23);
        panel.add(btnSend);

        JLabel lblReceived = new JLabel("Received");
        lblReceived.setBounds(296, 223, 66, 14);
        panel.add(lblReceived);

        chckbxHexSend.setBounds(120, 217, 97, 23);
        panel.add(chckbxHexSend);

        JButton btnClrRx = new JButton("Clear");
        btnClrRx.addActionListener(e -> textAreaRx.setText(""));
        btnClrRx.setBounds(430, 217, 89, 23);
        panel.add(btnClrRx);

        chckbxHexReceive.setBounds(370, 217, 97, 23);
        panel.add(chckbxHexReceive);

        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("UDP", null, panel_1, null);
        panel_1.setLayout(null);

        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("DNS", null, panel_2, null);
        panel_2.setLayout(null);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                NetChecker frame = new NetChecker();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
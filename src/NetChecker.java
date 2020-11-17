import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.BevelBorder;

//@SuppressWarnings({"LocalVariableOfConcreteClass", "MagicNumber"})
public class NetChecker extends JFrame {
    private PeriodicalTcpTransmitter perTX;
    private final JToggleButton chckbxRepeat = new JToggleButton("Repeat Millisecs >>");
    private final JPanel tcpPanel = new JPanel();
    private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    private final JIp4Control connectIP = new JIp4Control();
    private final JTextField connectPort = new JTextField();
    private final JTextField listenPort = new JTextField();
    private final JCheckBox chckbxHexReceive = new JCheckBox("Hex");
    private final JCheckBox chckbxHexSend = new JCheckBox("Hex");
    private final JTextArea textAreaRx = new JTextArea();
    private final JTextArea textAreaTx = new JTextArea();
    private final JTextField dnsDomainName = new JTextField();
    private final JIp4Control dnsIP4Address = new JIp4Control();
    private final JTextField perTxInterval = new JTextField();
    private final JToggleButton tglbtnConnect = new JToggleButton("Connect");
    private final JToggleButton tglbtnListen = new JToggleButton("Listen");

    private final TcpSocket sc = new TcpSocket(new Callback() {
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

        void doForSocketClose()
        {
            if (perTX != null)
            {
                perTX.stop();
                perTX = null;
            }
            tglbtnConnect.setSelected(false);
            tglbtnListen.setSelected(false);
            chckbxRepeat.setSelected(false);
        }

        @Override
        public void connected() {
            Utils.infoBox (tcpPanel, "Connection established", "Info");
        }

        @Override
        public void closed() {
            doForSocketClose();
            Utils.infoBox (tcpPanel, "Socket closed", "Info");
        }

        @Override
        public void error(String info) {
            doForSocketClose();
            Utils.errorBox (tcpPanel, info, "Socket error");
        }

        @Override
        public void rxfail() {
            doForSocketClose();
            Utils.errorBox (tcpPanel, "Connection closed", "Socket error");
        }
    });

    /**
     * Create the frame.
     */
    public NetChecker() {
        setTitle("IP Tester");
        setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 552, 402);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());
        //tabbedPane.setBounds(0, 0, 552, 352);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

//        JLabel lab1 = new JLabel("hello");
//        lab1.setBackground(Color.BLACK);
//        lab1.setForeground(Color.WHITE);
//        lab1.setOpaque(true);
//        contentPane.add (lab1, BorderLayout.SOUTH);

        tabbedPane.addTab("TCP", null, tcpPanel, null);
        tcpPanel.setLayout(null);
        tcpPanel.setBackground (new Color (100,100,255));

        tglbtnConnect.addActionListener(e -> {
            if (tglbtnConnect.isSelected())
            {
                try {
                    InetAddress in = connectIP.getAddress();
                    int port = Integer.parseInt(connectPort.getText());
                    sc.connect (in, port); // Google: 172.217.23.142
                } catch (Exception ex) {
                    tglbtnConnect.setSelected(false);
                }
            }
            else
                sc.close();
        });
        tglbtnConnect.setBounds(10, 11, 83, 23);
        tcpPanel.add(tglbtnConnect);

        connectIP.setBounds(103, 12, 110, 20);
        tcpPanel.add(connectIP);
        //connectIP.setColumns(10);

        connectPort.setBounds(215, 12, 52, 20);
        tcpPanel.add(connectPort);
        //connectPort.setColumns(10);

        tglbtnListen.addActionListener(e -> {
            if (tglbtnListen.isSelected()) {
                try {
                    int port = Integer.parseInt(listenPort.getText().trim());
                    sc.listen(port);
                } catch (NumberFormatException numberFormatException) {
                    tglbtnListen.setSelected(false);
                }
            } else
                sc.close();
        });
        tglbtnListen.setBounds(313, 11, 83, 23);
        tcpPanel.add(tglbtnListen);

        //listenPort.setColumns(10);
        listenPort.setBounds(406, 12, 86, 20);
        tcpPanel.add(listenPort);

        textAreaTx.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        textAreaTx.setLineWrap(true);
        textAreaTx.setBounds(10, 37, 252, 175);
        tcpPanel.add(textAreaTx);

        JScrollPane sp1 = new JScrollPane(textAreaRx);
        textAreaRx.setLineWrap(true);
        sp1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        sp1.setBounds(272, 37, 252, 175);
        tcpPanel.add(sp1);

        JButton btnSend = new JButton("Send");
        btnSend.addActionListener(e -> {
            byte[] bt;
            String str = textAreaTx.getText();
            if (chckbxHexSend.isSelected()) {
                bt = Utils.readHex(str);
            } else {
                bt = Utils.unescape(str).getBytes();
            }
            sc.send(bt);
        });
        btnSend.setBounds(10, 217, 89, 23);
        tcpPanel.add(btnSend);

        JLabel lblReceived = new JLabel("Received");
        lblReceived.setBounds(296, 223, 66, 14);
        tcpPanel.add(lblReceived);

        chckbxHexSend.setBounds(120, 217, 97, 23);
        tcpPanel.add(chckbxHexSend);

        JButton btnClrRx = new JButton("Clear");
        btnClrRx.addActionListener(e -> textAreaRx.setText(""));
        btnClrRx.setBounds(430, 217, 89, 23);
        tcpPanel.add(btnClrRx);

        chckbxHexReceive.setBounds(370, 217, 50, 23);
        tcpPanel.add(chckbxHexReceive);

        chckbxRepeat.addActionListener(e -> {
            if (chckbxRepeat.isSelected())
            {
                perTX = new PeriodicalTcpTransmitter(sc, textAreaTx);
                int interval;
                try {
                    interval = Integer.parseInt(perTxInterval.getText());
                    perTX.start (interval, chckbxHexSend.isSelected());
                } catch (Exception ex) {
                    chckbxRepeat.setSelected(false);
                }
            }
            else
            {
                perTX.stop();
            }
        });
        chckbxRepeat.setBounds(20, 247, 135, 23);
        tcpPanel.add(chckbxRepeat);

        perTxInterval.setBounds(155, 247, 86, 20);
        perTxInterval.setText ("1000");
        tcpPanel.add(perTxInterval);
        perTxInterval.setColumns(10);

        UdpPanel panel_1 = new UdpPanel();
        tabbedPane.addTab("UDP", null, panel_1, null);
        panel_1.setLayout(null);

        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("DNS", null, panel_2, null);
        panel_2.setLayout(null);

        dnsDomainName.setBounds(130, 71, 226, 20);
        panel_2.add(dnsDomainName);
        dnsDomainName.setColumns(10);

        JLabel lblTargetDomain = new JLabel("Target Domain");
        lblTargetDomain.setBounds(202, 46, 100, 14);
        panel_2.add(lblTargetDomain);

        JLabel lblHasThisIpv = new JLabel("has this IP4V Address");
        lblHasThisIpv.setBounds(188, 102, 130, 14);
        panel_2.add(lblHasThisIpv);

        dnsIP4Address.setBounds(164, 127, 158, 20);
        panel_2.add(dnsIP4Address);
        dnsIP4Address.setColumns(10);

        JButton btnQuery = new JButton(" Query ...");
        btnQuery.addActionListener(e -> {
            try {
                InetAddress i = InetAddress.getByName(dnsDomainName.getText());
                dnsIP4Address.putAddress(i);
            } catch (UnknownHostException unknownHostException) {
                System.out.println("Host not found");
            }
        });
        btnQuery.setBounds(199, 163, 89, 23);
        panel_2.add(btnQuery);

        JButton cpToTCP = new JButton("Copy to TCP");
        cpToTCP.addActionListener(e -> {
            try {
                connectIP.putAddress(dnsIP4Address.getAddress());
            } catch (UnknownHostException unknownHostException) {
                unknownHostException.printStackTrace();
            }
            tabbedPane.setSelectedIndex(0);
        });
        cpToTCP.setBounds (330, 127, 158, 23);  //(164, 300, 158, 23);
        panel_2.add(cpToTCP);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.put("ToggleButton.select", Color.GREEN);
                SwingUtilities.updateComponentTreeUI(new JToggleButton());
                NetChecker frame = new NetChecker();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

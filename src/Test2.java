import javax.swing.*;
import java.net.InetAddress;

public class Test2 extends JFrame
{
    public Test2()
    {
        setTitle("My Gui");
        setSize(400, 400);
        // Create JButton and JPanel
        JButton button = new JButton(">>");
        JIp4Control jt = new JIp4Control();
        JIp4Control jt2 = new JIp4Control();
        JPanel panel = new JPanel();
        // Add button to JPanel
        panel.add(jt);
        panel.add(button);
        panel.add(jt2);
        // And JPanel needs to be added to the JFrame itself!
        this.getContentPane().add(panel);
        setVisible(true);
        setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);

        button.addActionListener (e ->
        {
            InetAddress in = jt.getAddress();
            jt2.putAddress(in);
            System.out.println(jt);
        });
    }

    public static void main(String[] args)
    {
        Test2 t = new Test2();
    }
}

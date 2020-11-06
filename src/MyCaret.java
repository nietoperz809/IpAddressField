import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class MyCaret extends DefaultCaret
{
    public MyCaret()
    {
        super();
        setBlinkRate(500);
    }

    protected synchronized void damage(Rectangle r)
    {
        if (r == null) return;

        // give values to x,y,width,height (inherited from java.awt.Rectangle)
        x = r.x;
        y = r.y;
        height = r.height;
        // A value for width was probably set by paint(), which we leave alone.
        // But the first call to damage() precedes the first call to paint(), so
        // in this case we must be prepared to set a valid width, or else paint()
        // will receive a bogus clip area and caret will not get drawn properly.
        if (width <= 0)
            width = getComponent().getWidth();

        repaint(); // calls getComponent().repaint(x, y, width, height)
    }

    public void paint(Graphics g)
    {
        JTextComponent comp = getComponent();
        if (comp == null)
            return;

        int dot = getDot();
        Rectangle r = null;
        char dotChar;
        try {
            r = comp.modelToView(dot);
            if (r == null) return;
            dotChar = comp.getText(dot, 1).charAt(0);
        } catch (BadLocationException e) { return; }

        if ( (x != r.x) || (y != r.y) ) {
            // paint() has been called directly, without a previous call to
            // damage(), so do some cleanup. (This happens, for example, when the
            // text component is resized.)
            repaint(); // erase previous location of caret
            x = r.x;   // Update dimensions (width gets set later in this method)
            y = r.y;
            height = r.height;
        }

        g.setColor(comp.getCaretColor());
        g.setXORMode(comp.getBackground()); // do this to draw in XOR mode

        width = Math.max (g.getFontMetrics().charWidth(dotChar), 2);
        if (isVisible())
            g.fillRect(r.x, r.y, width, r.height);
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame("FancyCaret demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextArea area = new JTextArea(8, 32);
        area.setCaret(new MyCaret());
        area.setText("VI\tVirgin Islands \nVA      Virginia\nVT\tVermont");
        frame.getContentPane().add(new JScrollPane(area), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}
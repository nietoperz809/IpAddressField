import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public final class JIp4AddressInput extends JTextField
{
    private final char[] buff = "  0.  0.  0.  0".toCharArray();

    private int bpos = 0;

    private void putnum (int num, final int offset)
    {
        final int a = num/100;
        num -= a*100;
        final int b = num/10;
        num -= b*10;
        this.buff[offset] = (char)('0'+a);
        this.buff[offset+1] = (char)('0'+b);
        this.buff[offset+2] = (char)('0'+num);
    }

    private void align (final int base)
    {
        final int end = base+3;
        StringBuffer sb = new StringBuffer();
        for (int s=base; s<end; s++)
        {
            if (' ' != buff[s])
                sb.append(this.buff[s]);
        }
        while (1 < sb.length() && '0' == sb.charAt(0))
            sb.delete(0,1);
        while (3 > sb.length())
            sb.insert(0, ' ');
        try
        {
            final int num = Integer.parseInt(sb.toString().trim());
            if (255 < num)
                sb = new StringBuffer("255");
            if (0 > num)
                sb = new StringBuffer("  0");
        }
        catch (final NumberFormatException e)
        {
            sb = new StringBuffer("  0");
        }
        for (int s=base; s<end; s++)
        {
            this.buff[s] = sb.charAt(s-base);
        }
    }

    private void alignAll()
    {
        this.align(0);
        this.align(4);
        this.align(8);
        this.align(12);
    }

    private void fwd ()
    {
        this.bpos = 15 == bpos ? this.bpos : this.bpos +1;
    }

    private void back ()
    {
        this.bpos = 0 == bpos ? this.bpos : this.bpos -1;
    }

    private void backspace()
    {
        this.back();
        if (3 == bpos || 7 == bpos || 11 == bpos)
        {
            return;
        }
        if (15 > bpos)
            this.buff[this.bpos] = ' ';
    }

    private void setChar (final char c)
    {
        if (3 == bpos || 7 == bpos || 11 == bpos)
        {
            this.fwd();
        }
        if (15 > bpos)
            this.buff[this.bpos] = c;
        this.fwd();
    }

    public JIp4AddressInput()
    {
        this.setPreferredSize(new Dimension(110, 30));
        this.setEditable(false);

        final Action beep = this.getActionMap().get(DefaultEditorKit.deletePrevCharAction);
        beep.setEnabled (false);

        this.setFont(new Font("monospaced", Font.PLAIN, 12));
        this.setText(new String (this.buff));

        this.setCaret(new MyCaret());

        this.addFocusListener(new FocusListener()
        {
            @Override
            public void focusGained(final FocusEvent e)
            {
                JIp4AddressInput.this.setText(new String (JIp4AddressInput.this.buff));
                JIp4AddressInput.this.setCaretPosition(0);
                JIp4AddressInput.this.getCaret().setVisible(true);
            }

            @Override
            public void focusLost(final FocusEvent e)
            {
                JIp4AddressInput.this.alignAll();
                JIp4AddressInput.this.setText(new String(JIp4AddressInput.this.buff));
            }
        });

        this.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped (final KeyEvent e)
            {
                JIp4AddressInput.this.bpos = JIp4AddressInput.this.getCaretPosition();
                final char c = e.getKeyChar();
                if (('0' <= c && '9' >= c) || ' ' == c)
                {
                    JIp4AddressInput.this.setChar(c);
                }
                else if (KeyEvent.VK_BACK_SPACE == c)
                {
                    JIp4AddressInput.this.backspace();
                }
                else if (KeyEvent.VK_ENTER == c)
                {
                    JIp4AddressInput.this.alignAll();
                }
                JIp4AddressInput.this.setText(new String(JIp4AddressInput.this.buff));
                JIp4AddressInput.this.setCaretPosition(JIp4AddressInput.this.bpos);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public final InetAddress getAddress()
    {
        final String[] parts = new String(this.buff).split("\\.");
        final byte[] adr = new byte[4];
        for (int s = 0; 4 > s; s++)
            adr[s] = (byte)Integer.parseInt(parts[s].trim());
        try {
            return InetAddress.getByAddress(adr);
        } catch (final UnknownHostException e) {
            return null;
        }
    }

    public final void putAddress(final InetAddress in)
    {
        final byte[] adr = in.getAddress();
        this.putnum(adr[0]&0xff, 0);
        this.putnum(adr[1]&0xff, 4);
        this.putnum(adr[2]&0xff, 8);
        this.putnum(adr[3]&0xff, 12);
        this.alignAll();
        this.setText(new String(this.buff));
    }

    @Override
    public String toString()
    {
        return "JIp4AddressInput{" +
                "buff=" + Arrays.toString(buff) +
                '}';
    }
}

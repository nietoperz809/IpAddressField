import java.util.ArrayList;
import java.util.Arrays;

public class Utils
{
    static private final char[] hex = "0123456789ABCDEF".toCharArray();

    public static byte[] readHex (String in)
    {
        ArrayList<Byte> arr = new ArrayList<>();
        int state = 0;
        int bt = 0;
        for (char c : in.toCharArray())
        {
            c = Character.toLowerCase(c);
            int a = 0;
            if (c >= '0' && c <='9')
            {
                a = c-'0';
            }
            else if (c >= 'a' && c <= 'f')
            {
                a = c-'a'+10;
            }
            else
                continue;
            a &= 15;
            switch (state)
            {
                case 0:
                    bt = a<<4;
                    state = 1;
                    break;
                case 1:
                    bt = bt | a;
                    arr.add((byte)bt);
                    state = 0;
                    break;
            }
        }
        byte[] ret = new byte[arr.size()];
        for (int s=0; s<ret.length; s++)
        {
            ret[s] = arr.get(s);
        }
        return ret;
    }

    public static String toHex (byte[] in, int len)
    {
        StringBuilder buf = new StringBuilder();
        for (int s=0; s<len; s++)
        {
            byte b = in[s];
            buf.append(hex[b >>> 4]);
            buf.append(hex[b & 0x0f]);
            buf.append(' ');
        }
        return buf.toString();
    }

    public static void main(String[] args) {
        byte[] bb = readHex("1gghhzzatt");
        System.out.println(Arrays.toString(bb));
    }
}

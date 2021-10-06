
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * <p> Description: </p>
 */
public class PrintStreamDemo {
    public static void main(String[] args) throws UnsupportedEncodingException {
        PrintStream psu = new PrintStream(System.out, true, "UTF-8");
        PrintStream psg = new PrintStream(System.out, true, "GBK");
        psu.println(123);
        psg.println(123);
        psu.println('国');
        psg.println('国');
    }
}

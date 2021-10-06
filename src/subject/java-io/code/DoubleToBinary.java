
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p> Description: </p>
 */
public class DoubleToBinary {

    public static String toBinary(double v){
        byte[] buf = null;
        {
            ByteArrayOutputStream o1 = new ByteArrayOutputStream();
            DataOutputStream o2 = new DataOutputStream(o1);
            try {
                o2.writeDouble(v);
            } catch (IOException e) {
                e.printStackTrace();
            }
            buf = o1.toByteArray();
        }
        StringBuilder sb = new StringBuilder();
        // 两位一取，四位一空格
        String[] map = {"00", "01", "10", "11"};
        int mask = 0B11;
        for (byte b:buf){
            sb.append(map[b>>6 & mask]);
            sb.append(map[b>>4 & mask]);
            sb.append(" ");
            sb.append(map[b>>2 & mask]);
            sb.append(map[b & mask]);
            sb.append(" ");
        }
        // 去掉最后的空格
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
    public static void main(String[] args) {
        double v = 12.3;
        System.out.println("double 类型的数值 " + v + " 的二进制存储格式为：");
        System.out.println(toBinary(v));
    }
}
/*

double 类型的数值 12.3 的二进制存储格式为：
0100 0000 0010 1000 1001 1001 1001 1001 1001 1001 1001 1001 1001 1001 1001 1010

*/

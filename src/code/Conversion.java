/**

类型转换实验
 */

public class Conversion{
    public static void main(String[] args){
        byte b = 3;
        b = 4;
        System.out.println(b);
        b = 5;
        System.out.println(b);

        short s = 12;
        // incompatible types: possible lossy conversion from short to char
        // char  ch = s;
        // System.out.println(ch);
    }
}

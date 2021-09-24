/**

类型转换
*/
public class Conversion{
    public static void main(String[] args){
        byte b = 3;
        // error: incompatible types:
        // b = b + 1;
        b += 3;
        System.out.println("b: " + b);
    }
}

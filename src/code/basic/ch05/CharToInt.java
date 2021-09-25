/**
short 和 char 升级为int，补1还是补0？
short 我知道，正数补0，负数补1；那么char呢?
Ans: char 都是补零。
*/

public class CharToInt{

    public static void main(String[] args){
        char ch = 0xFFFF;
        // incompatible types: possible lossy conversion from int to shor
        // short sh = 0xFFFF;
        short sh = (short)0xFFFF;
        System.out.println("ch:" + ch);
        System.out.println("sh:" + sh);

        int i = ch, j = sh;
        System.out.println("i:" + i);
        System.out.println("j:" + j);

    }
}

/*
$ javac CharToInt.java
$ java CharToInt
ch:￿
sh:-1
i:65535
j:-1
 */

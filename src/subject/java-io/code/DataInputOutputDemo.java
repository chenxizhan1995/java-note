/**
DataInputStream 和 DataOutputStream 类使用示例。
先把几个基本类型的数据写入文件，然后再读取。
2021-10-06
*/

import java.io.*;

public class DataInputOutputDemo{
    public static void main(String[] args){
        int a = 100;
        double d = 128.0;
        boolean flag = false;

        try (OutputStream fout = new FileOutputStream("test.dat");
            DataOutputStream out = new DataOutputStream(fout)){
            out.writeInt(a);
            out.writeDouble(d);
            out.writeBoolean(flag);
        } catch (IOException e){
            System.out.println(e);
        }

        try (DataInputStream in = new DataInputStream(new FileInputStream("test.dat"))){
            int x = in.readInt();
            double e = in.readDouble();
            boolean flag2 = in.readBoolean();
            System.out.println("x:"+x);
            System.out.println("e:"+e);
            System.out.println("flag2:"+flag2);
        } catch (IOException e){
            System.err.println(e);
        }
    }
}

/**
$ javac DataInputOutputDemo.java
$ java DataInputOutputDemo
x:100
e:128.0
flag2:false


$ ll test.dat
-rw-r--r-- 1 xizhan xizhan 13 Oct  6 15:19 test.dat
*/

/**
把文件内容显示在标准输出

2021-10-04

Example: java ShowFile  foo.txt
*/
import java.io.*;
public class ShowFile{
    public static void main(String[] args){

        if (args.length != 1){
            System.out.println("Usage: java ShowFile <fileName>");
            return;
        }

        final String filePath = args[0];

        try (InputStream fin = new FileInputStream(filePath)){
            byte[] buf = new byte[8192];
            int count = 0;
            while (-1 != (count = fin.read(buf))){
                System.out.write(buf, 0, count);
            }
        } catch (FileNotFoundException e){
            System.out.println("找不到文件: " + filePath);
        } catch (IOException e){
            System.err.println("发生了 IO 错误" + e);
        }
    }
}

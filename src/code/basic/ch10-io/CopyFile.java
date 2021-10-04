/** 复制文件
2021-10-04
*/
import java.io.*;
public class CopyFile{
    public static void main(String[] args){
        if (args.length != 2){
            System.out.println("Usage: CopyFile <srcFile> <destFile>");
            return;
        }
        try (InputStream fin = new FileInputStream(args[0]);
            OutputStream fout = new FileOutputStream(args[1])
        ){
            byte[] buf = new byte[8192];
            int count;

            while (-1 != (count = fin.read(buf))){
                fout.write(buf, 0, count);
            }
        } catch (FileNotFoundException e){
            System.err.println("找不到文件:" + e.getMessage());
        } catch (IOException e){
            System.err.println("IO错误:" + e);
        }
    }
}

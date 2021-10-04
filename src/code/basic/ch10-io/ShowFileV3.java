/**
将文件内容显示在控制台上
2021-10-04
*/

import java.io.*;

public class ShowFileV3{
    public static void  main(String[] args){
        if (args.length != 1){
            System.out.println("Usage: ShowFileV3 <fileName>");
            return;
        }

        final String fileName = args[0];
        InputStream fin = null;

        try {
            fin = new FileInputStream(fileName);
            int i;
            while (-1 != (i=fin.read())){
                System.out.write(i);
            }
        } catch (FileNotFoundException e){
            System.err.println("找不到文件：" + fileName);
            return;
        } catch (IOException e){
            System.err.println("IO错误" + e);
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e){
                System.out.println("关闭文件失败:" + fileName);
            }
        }
    }
}

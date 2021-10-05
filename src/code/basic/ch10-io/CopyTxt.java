/**
使用 Java 字符流复制文件。

*/

import java.io.*;
public class CopyTxt{

    public static void main(String[] args){
        if (args.length != 2){
            System.out.println("Usage: Java CopyTxt <src> <dst>");
            return;
        }

        try (Reader rd = new FileReader(args[0]);
        Writer wt = new FileWriter(args[1])){
            for (int ch = rd.read(); ch != -1; ch = rd.read()){
                wt.write(ch);
            }
        } catch(FileNotFoundException e){
            System.err.println(e.getMessage());
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}

import java.io.File;
import java.net.MalformedURLException;

/**
 * <p> Description: java.io.File 类的用法</p>
 */
public class FileDemo {
    public static void main(String[] args) throws MalformedURLException {
        System.out.println("列出所有根");
        for (File f : File.listRoots()){
            System.out.println(f);
        }
        System.out.println("当前目录对应的绝对路径");
        File f =new File(".");
        System.out.println(f.getAbsoluteFile());
        System.out.println(f.getAbsolutePath());
        System.out.println("当前路径对应的绝对路径的 URI 形式");
        File f2 = f.getAbsoluteFile();
        System.out.println(f2.toURI());
        System.out.println(f2.toURL());
    }
}

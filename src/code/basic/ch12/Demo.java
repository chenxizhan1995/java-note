import java.util.*;
public class Demo{
    public static void main(String[] args){
        List la = new ArrayList<String>();
        la.add("hello");
        List<Integer> li = la;

        System.out.println("hello");
        // java.lang.ClassCastException
        Integer i = li.get(0);
        System.out.println(i);

        System.out.println("done");

        List<Integer>[] ga = (List<Integer>[])new ArrayList[10];
    }
}

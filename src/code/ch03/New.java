/**

试试 new 对象失败的样子
 */

import java.util.List;
import java.util.ArrayList;

public class New {
    long a,b,c,d,e,f,g;

    public static void main(String[] args){
        List<New> list = new ArrayList<>((int)1e8);
        while (true){
            list.add(new New());
        }
    }
}

/**
$ javac New.java
$ time java New

Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
        at New.main(New.java:15)

real    0m5.146s
user    0m33.592s
sys     0m2.810s

# 把最大堆内存设置小一些，可以更快遇到期望的行为
$ time java -Xmx10m New
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
        at java.base/java.util.ArrayList.<init>(ArrayList.java:154)
        at New.main(New.java:13)

real    0m0.062s
user    0m0.067s
sys     0m0.022s
*/

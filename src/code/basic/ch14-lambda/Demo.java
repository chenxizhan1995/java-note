/** lambda 表达式，一个形参，且声明了类型，圆括号不得省略
2021-10-21
*/
import java.util.function.*;
public class Demo{
    void fun(){
        Predict<Integer> isEven = (Integer x) -> x%2 ==0;
        Predict<Integer> isOdd = Integer x -> x%2 ==0;

        // 单独的lambda表达式，不可以。
        () -> 12; // error: not a statement
    }
}

/**
Demo.java:7: error: ';' expected
        Predict<Integer> isOdd = Integer x -> x%2 ==0;
                                        ^
Demo.java:7: error: not a statement
        Predict<Integer> isOdd = Integer x -> x%2 ==0;
                                         ^
2 errors

*/

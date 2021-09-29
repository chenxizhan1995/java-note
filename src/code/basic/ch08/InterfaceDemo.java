/**
接口的基础语法：声明、实现、接口引用
 */
public class InterfaceDemo{
    public static void main(String[] args){
        IFoo foo = new Foo();
        System.out.println(foo.add(1,2));
    }
}
interface IFoo{
    String MESSAGE = "hello, interface";

    int add(int i, int j);

}
class Foo implements IFoo{
    @Override
    public int add(int i, int j){
        return i + j;
    }
}

/**
$ javac InterfaceDemo.java
$ java InterfaceDemo
3
 */

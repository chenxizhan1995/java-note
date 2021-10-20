import static java.lang.System.out;
public class GenDemo3<T>{
    void fun1(Gen<? extends Number> a){}
    void fun2(Gen<? super Long> b){}
    void fun3(Gen<? extends Number super Long> c){}
    void fun4(Gen<? super Long extends Number> c){}
    public static void main(String[] args){

    }
}
/**
$ javac GenDemo3.java
GenDemo3.java:5: error: > expected
    void fun3(Gen<? extends Number super Long> c){}
                                   ^
GenDemo3.java:6: error: > expected
    void fun4(Gen<? super Long extends Number> c){}
                               ^
2 errors
*/

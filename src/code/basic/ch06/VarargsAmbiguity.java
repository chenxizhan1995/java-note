/*
varargs 的歧义
*/

public class VarargsAmbiguity{

    static void vaTest(boolean... v){

    }
    static void vaTest(int ... v){

    }

    public static void main(String[] args){
        // error: reference to vaTest is ambiguous
        // vaTest();
        // 方法重载正确，但这个调用有问题，两个 vaTest()方法的优先级相同，编译器无法解析。
    }
}

class NoAmbiguity{

    static void vaTest(boolean... v){

    }
    static void vaTest(int ... v){

    }
    static void vaTest(){

    }
    public static void main(String[] args){
        // 这个可以，因为普通方法优先于 varargs 方法。
        vaTest();
    }
}

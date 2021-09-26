/**
静态代码段示例。

 */
public class StaticBlock{
    static double rootOf2;
    static double rootOf3;
    {
        System.out.println("Inside Static Code Block");
        rootOf2 = Math.sqrt(2);
        rootOf3 = Math.sqrt(3);
    }

    public StaticBlock(){
        System.out.println("Inside Constructor.");
    }
    public static void main(String[] args){
        StaticBlock demo = new StaticBlock();
        System.out.println("root of 2:" + StaticBlock.rootOf2);
    }
}

/**

$ javac StaticBlock.java
$ java StaticBlock

Inside Static Code Block
Inside Constructor.
root of 2:1.4142135623730951

*/

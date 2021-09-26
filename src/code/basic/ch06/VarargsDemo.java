/**
可变参数方法示例
 */

public class VarargsDemo{
    public int sum(int... num){
        int sum = 0;
        for (int i:num){
            sum+=i;
        }
        return sum;
    }
    public static void main(String[] args){
        VarargsDemo demo = new VarargsDemo();
        System.out.println("sum():" + demo.sum());
        System.out.println("sum(1,2,3):" + demo.sum(1,2,3));
    }
}

/*
$ javac VarargsDemo.java
$ java VarargsDemo
sum():0
sum(1,2,3):6
*/

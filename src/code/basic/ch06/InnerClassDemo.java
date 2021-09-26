/**
内部类示例

 */

public class InnerClassDemo{
    public static void main(String[] args){
        Outer outer = new Outer(new int[]{1,4,1,5,9,2,6,5,3,5});
        outer.analyze();

        // 在客户端代码中创建内部类实例的语法：
        // ps: 没想到关键字 new 还能跟在标识符的后面，神奇。
        Outer.Inner inner = outer.new Inner();
        System.out.println("min:" + inner.min());

        // 下面这两种语法都是错误的。
        // Outer.Inner inner2 = outer.new Outer.Inner();
        // outer.Inner inner2 = outer.new Inner();
    }
}

class Outer{
    int[] arr;

    Outer(int[] numbers){
        if (numbers == null || numbers.length == 0){
            throw new IllegalArgumentException("数组不能空着");
        }
        arr = numbers;
    }
    public void analyze(){
        Inner inner = new Inner();
        System.out.println("min:" + inner.min());
        System.out.println("max:" + inner.max());
        System.out.println("avg:" + inner.avg());
    }
    class Inner{
        int min(){
            int min = arr[0];
            for (int i:arr){
                if (i < min) min = i;
            }
            return min;
        }
        int max(){
            int max = arr[0];
            for (int i:arr){
                if (i > max) max = i;
            }
            return max;
        }
        double avg(){
            double sum = 0;
            for (int i:arr){ sum += i;}
            return (sum+0.0)/arr.length;
        }
    }
}

/**

$ javac InnerClassDemo.java
$ java InnerClassDemo
min:1
max:9
avg:4.1
min:1
*/

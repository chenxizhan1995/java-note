class Square{
    int length;
}

public class SquareDemo{
    public static void main(String[] args){
        Square q1 = new Square();
        q1.length = 3;
        System.out.println("正方形的面积是: " + q1.length * q1.length);
    }
}
/*
$ javac SquareDemo.java
$ java SquareDemo
正方形的面积是: 9
*/

public class SquareDemo2{

    public static void main(String[] args){
        Square q = new Square();
        q.side = 5;
        System.out.println("正方形的面积是：" + q.area());
    }
}
class Square{
    int side;
    int area(){
        return side*side;
    }
}
/**
$ javac SquareDemo2.java
$ java SquareDemo2
正方形的面积是：25
*/

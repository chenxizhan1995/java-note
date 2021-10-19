/**


Q. import static java.lang.Math.*; 可以省略为 import Math.*; 吗？java.lang 包名前缀可以省略吗？
Ans: 不能。error: cannot find symbol: Math


$ javac ImportDemo.java
ImportDemo.java:7: error: cannot find symbol
import static Math.*;
              ^
  symbol: class Math
1 error

*/

import static Math.*;

public class ImportDemo{
    public void main(String[] args){

    }
}

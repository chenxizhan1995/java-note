/**
泛型父子类关系
*/
import java.util.*;

public class GenDemo{
    public static void main(String[] args){
        {
            List<Object> lo = new ArrayList<Object>();

            // List<String> ls = lo; // incompatible types: List<Object> cannot be converted to List<String>

            // lo.add(new Object());
            // String s = ls.get(0); // Runtime Error
        }

        {
            List<String> ls = new ArrayList<String>();
            // List<Object> lo = ls; // error: incompatible types: List<String> cannot be converted to List<Object>

            // lo.add(new Object());
            // String s = ls.get(0); // Runtime Error
        }

    }
}

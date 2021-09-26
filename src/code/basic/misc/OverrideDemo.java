
public class OverrideDemo{

}
class A{
    private void foo(){}
}

class B extends A{
    public void foo(){}
}



class B2 extends A{
    // method does not override or implement a method from a supertype
    // @Override
    // public void foo(){}
}

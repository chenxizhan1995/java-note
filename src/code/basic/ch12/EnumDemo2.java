/**
枚举可以声明构造器。
*/
import static java.lang.System.out;
public class EnumDemo2{
    enum Size {
        SMALL(5), MEDIUM(10), BIG(15);
        private int size;
        Size(int size){ this.size = size; }
    }

    public static void main(String[] args){
        out.println(Size.SMALL);

        out.println(Pet.DOG.run());
    }

    // 这个类，演示
    enum Pet{
        DOG("Coco"){
            @Override
            public String run(){
                return "Dog " + this.name + " running.";
            }
        },
        CAT("TOM"){
            @Override
            public String run(){
                return "Cat " + this.name + " running.";
            }
        }
        ;
        Pet(String name){ this.name = name; }
        protected String name;
        public String eat(){
            return name + " eating..";
        }
        public abstract String run();
    }
}
/**
$ javac EnumDemo2.java && java EnumDemo2
SMALL
Dog Coco running.

*/

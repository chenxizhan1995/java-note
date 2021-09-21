public class Switch{
    public static void main(String[] args){
        char ch = 'E';
        switch(ch){
            default:
                System.out.println("default");
            case 'A':
                System.out.println("A");
                break;
            case 'B':
                System.out.println("B");
        }
    }
}

/*
输出结果为
default
A
*/

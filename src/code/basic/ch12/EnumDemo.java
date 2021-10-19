import static java.lang.System.out;

public class EnumDemo{
    enum Color { RED, GREEN, BLUE }

    public static void main(String[] args){
        Color color = Color.RED;

        out.println(color == Color.RED);
        out.println();
        switch (color){
            // 注意，case 后的枚举常量不需要也不允许带有 Color. 前缀，
            // 因为 switch 中含有枚举类型信息
            case RED:   out.println("RED");
            break;
            case BLUE:  out.println("BLUE");
            break;
            case GREEN: out.println("GREEN");
            break;
        }
        out.println();

        out.println(color.name());

        // 遍历枚举
        for (Color c:Color.values()){
            out.println(c);
        }

        out.println();
        // 根据字符串取得对应的枚举
        out.println(Color.valueOf("RED"));
    }
}

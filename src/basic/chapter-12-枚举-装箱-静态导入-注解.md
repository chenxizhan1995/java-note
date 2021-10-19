# 枚举-装箱-静态导入-注解
2021-10-19

- 基础：定义枚举、声明枚举变量、== 比较、用于switch语句
- Java 中的枚举是类类型
  - 可以提供构造函数，可以添加方法，可以实现接口等等
  - 所有都有两个预定义方法：values 和 valueOf
    - public static T[] values()  取得枚举类的所有常量，
    - public static T valueOf(String name) 根据字符串获得对应的枚举常量
- 枚举继承 `Enum<E extends Enum<E>>`

- 自动装箱

  - 包装器类
  - Number、构造函数、valueOf
  - 自动装箱和自动拆箱的含义
  - 发生的时机：赋值、传参、返回值、表达式
  - 表达式：++，+= 等等

- 静态导入
  - `import static java.lang.Math.*;`
  - `import static java.lang.Math.pow;`
  - `import java.lang.System.out;` 不建议这么做，因为反而不够明晰。大家都写 System.out

- 注解
  - 语法
  - 自动继承 java.lang.annotation.Annotation 接口；同时注解声明不能包含 extends 子句
  - 标记注解是什么？不带形参的注解是标记注解。ps：就是`形参`.


- 枚举常量一般用 PASCAL_CASE，这只是约定，并没有语法约束。
- 枚举和 final 变量各有所长，并不互相替代。
## 枚举基础

```java
{{#include ../code/basic/ch12/EnumDemo.java}}
```
## 作为类的枚举

values 和 valuesOf 方法

## demo: 构造器、实例方法、实例变量
每个枚举常量对应一个 public final 的字段声明，类型为枚举类型。

每个枚举常量创建时都调用了构造函数，构造函数必须是无修饰符或者为private修饰符。

Q. 枚举构造器的访问修饰符是？

Q. 枚举如果声明了抽象方法，abstract 是必须的吗？

```java
{{#include ../code/basic/ch12/EnumDemo2.java}}
```
## 枚举继承 Enum
枚举不许使用 extends 关键字，同时所有枚举 E 都隐式继承 `java.lang.Enum<E extends Enum<E>>`。
Enum 定义了 final String name() 方法，返回枚举常量的字符串形式；
String toString() 方法，返回枚举常量的字符串形式，二者的区别在于，name方法是final的，
故确保获取的是枚举常量的字符串值；toString 可以在子类重写，提供更具可读性的返回值。

枚举还定义了 final int ordinal() 方法，它返回枚举常量的索引，从 0 开始，递增。
int compareTo(E e) 按 ordinal 进行比较。

## Number、构造函数、valueOf
八种基本类型对应的封装类分别是：Byte、Short、Integer、Long、Float、Double、Character、Boolean。

其中 Byte、Short、Integer、Long、Float、Double 都有公共的父类 java.lang.Number，这个
类定义了一系列到基本类型数值的方法：

- byte byteValue()
- short shortValue()
- int intValue()
- long longValue()
- float floatValue()
- double doubleValue()

这六种数值类型，都提供了两个构造函数，用于从字符串或基本类型值构造对应的包装类型。
- Long(long)
- Long(String longStr)  如果不是数值，则抛出 NumberFormatException

JDK 9 开始，这六个包装器类的构造函数标记为弃用，同时提供了同样参数的静态 valueOf() 方法。
- public static Long valueOf(long)
- public static Long valueOf(String longVal)



# 类、对象和方法
2021-09-22

- 类是什么？类在 Java 中的重要性？
- 概念：类的成员（member），实例变量
- 类的基本形式是？

**类**是定义对象形式的模板，指定了数据以及操作数据的代码。类形成了Java中面向对象程序设计的基础。

组成类的方法和变量都称为类的成员。数据成员也叫做实例变量。

类的基本形式
```java
class ClassName{
  // 数据成员
  type var1;
  type var2;
  //...
  type varN;

  // 方法
  type method1(){

  }

  type method2(parameters){

  }
  //....
  type methodN(parameters){

  }
}
```

> 最佳实践：同一个类应该只组织逻辑相关的信息。

示例：SquareDemo
- 定义类
- 创建对象：
  - Type var = new Type(arg1, arg2, ..., argN);
  - Type var = new Type();
- 句点访问类成员
- 每个对象有各自的实例变量副本
- 创建对象2：new 运算符、引用变量和赋值
  - 引用变量
  - 引用变量和基本类型变量赋值的区别
```java
{{#include ../code/ch03/SquareDemo.java}}
```

示例：SquareDemo2
类可以包含方法
- 方法的定义形式
- 据点运算符访问方法
- 方法的调用形式
```java
{{#include ../code/ch03/SquareDemo2.java}}
```
````java
type methodName(parameters){
  // body
}
````

- 方法的返回值、return 语句：return语句的形式要和返回值对应
  - 无返回值的方法，以 void 为返回值类型；对应的return语句形式为 `return;`
  - 有返回值的方法，以具体类型为返回值类型，对应的return语句形式为 `return expr;` 其中 expr 的类型要和方法返回值类型相兼容
    - Q. “兼容”的详细规则？

- 方法的形参、实参
  - 形参个数可以是任意多个（包括零个）；调用时，实参的个数和顺序要和形参一一对应。
  - 自JDK5，新增可变参数语法，后面提到。

构造函数
- 构造函数的基本形式：和类同名，无返回值
- 默认构造函数：所有的类都有构造函数，没有定义构造函数，Java会自动提供默认的构造函数；有定义构造函数时，Java 不再提供默认构造函数。
- Q. 对象的实例变量有默认值，这个默认值是在什么时候赋值的？还有静态代码块、实例代码块的知识。

this 关键字
- 调用方法时，会向方法自动传递一个隐式实参 this
- 方法中不加 this 前缀，可以直接方法类的成员
- Java 允许形参或局部变量与实例变量同名，这时实例变量会被隐藏，通过使用 this 可以访问隐藏的实例变量。
> 最佳实践：除非要访问被局部变量隐藏的实例变量，不然不要加 this。不加 this 访问类成员是标准的做法。

new 运算符
- new 运算符并不一定会总是成功，当内存不足时，会抛出运行时异常。
`java.lang.OutOfMemoryError: Java heap space`

```java
{{#include ../code/ch03/New.java}}
```

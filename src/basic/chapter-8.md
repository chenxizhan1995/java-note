# 包和接口
2021-09-29 chenxizhan update
2021-09-28 chenxizhan new

- 包的意义所在
- 包的语法： package 语句
- 默认包：所有不声明包的类，自动作为默认包的成员。默认包是透明的。
- 包，在文件系统中对应为目录结构
- 包和classpath
- 包和访问修饰符
- import 语句：使用其它包内的类需要使用全限定名，这很麻烦，所以提供了 import 语句。
- java.lang 包是自动导入的。就好像每个程序都隐含有 `import java.lang.*;` 语句。

- Q. 包可以辅助访问控制，但并没有强制手段限制别人不许在包中搞事，比如可以声明同一个包下的类，利用继承，重写方法等等。
  - final可以禁止继承，但同时把自己也给禁止了。
- Q. 好像是不许程序员声明 java.xxx 的包的。在哪里有说明？JLS吗？

- Q. foo.bar.Foo 类 访问 foo.bar.baz.Bar 类的时候，可以使用 baz.Bar 这种方式吗？
  还是说 Java 中只允许 导入或者全限定名，没有半截名字。
  Ans: Java 的类和接口只有全限定名和简单名，没有半截的名字。

- 接口的基础知识：语法，字段默认是 psf，方法默认是 public 的
- 实现接口：implements，不实现接口的所有方法，就必须是抽象类
- 接口引用：多态
- 接口可以扩展：extends
- 接口默认方法的语法
- 接口默认方法的两个用途
- 接口默认方法，遇到多重继承时的逻辑问题与语法规则处理
- 接口的 static 方法
- 接口的私有方法
  - 语法
  - 目的/动机：避免默认方法出现重复代码端
- Q. 接口字段自动是 psf 的，还有一些类似约束，是什么？同样，enum 也有一些类似约束
  - 接口字段默认是 psf，可以显式指定，但不建议。
  - 接口方法默认是 public abstract 的，可以显式指定，但不建议。
  - 非顶层枚举自动是 static，可以显式指定，但没必要
  - 枚举不许使用 final 或 abstract 修饰，是编译错误。

## 包的意义
1. 主要是为类/接口提供名称空间机制，作为一种组织代码的手段。
2. 附带增加了一种访问控制手段

包引入了新一级的访问控制级别：private、default、protected、public，权限依次放开。

## 包的的基本知识
用 package 声明包。如果指定package语句，它必须是第一条非注释语句，否则是语法错误。
```java
package hello.world;

public class A{}
```

包名可以有层级关系，但他们并不因此产生成员关系。
foo 和 foo.bar 是不同的包，特别的，foo.bar 并不是 foo 的成员。
比如，foo 中不能使用 bar.XX 访问 bar 类；bar 中的类不能访问 foo 的包私有成员。

包在文件系统中对应为目录结构。

TODO：包和 classpath
TODO：模块和classpath

## 接口的基本知识
### 基础语法：声明、实现、引用、扩展
常规接口的语法形式，
顶层接口和顶层类一样，可以是 public 的可以是包私有的（无访问修饰符）。
```java
public interface IFoo{
    type method();
    type method2(parameters);

    type CONSTANT_1 = val1;
    type CONSTANT_2 = val2;
}
```
- 接口字段自动是 psf（public static final） 的，所以实际上是常量，可以通过接口名访问。
  ps：对字段，可以显示指定 psf，不报错，但不建议。
- 接口的方法没有方法体
- 接口的方法自动是 public 的，可以冗余指定 public，但不建议。
- 既不是 default 也不是 static 的方法隐含是 abstract 的，可以显式指定，但不建议。

如下：
```java
public interface IFoo{
    // public、abstract 修饰符都是冗余的，可以省略，且建议省略。
    public abstract type method();
    public abstract type method2(parameters);
    // public、static、final 修饰符都是冗余的，可以省略，且建议省略。
    public static final type CONSTANT_1 = val1;
    public static final type CONSTANT_2 = val2;
}
```

- 要定义一组共享的常量，可以创建一个接口，该接口只定义常量，不声明方法，要使用常量的类只需要实现该接口即可。

示例：接口的声明、实现，声明接口引用

```java
{{#include ../code/basic/ch08/InterfaceDemo.java}}
```

接口可以继承：
```java
interface IA{

}

interface IB extends IA {}
```

### 新语法：默认方法、静态方法、私有方法
JDK 8 为接口增加了新语法：默认方法和静态方法。
```java
public interface IFoo{
    default type method(){

    }
    static method2(parameters){

    }

}
```
默认方法是实例方法，不可以用接口名直接访问，必须通过实现类的实例才行。
默认方法可以被继承，可以被重写。

ex：一个类实现多个接口，若有相同的默认方法，则会产生冲突，java 对此有规定：
1. 如果实现类实现了方法，则以实现类为准
2. 如果实现类没有实现此方法，则是编译错误。
Q. 如果类是抽象类，还会是编译错误吗？

静态方法，通过接口名称调用。
静态方法不能被子类继承，也不能被子接口继承。

JDK 9 引入了私有方法。
私有方法只能被默认方法或者其它私有方法调用。
引入私用方法，主要是为默认方法提供代码重用的手段，避免多个默认方法编写重复的代码段。

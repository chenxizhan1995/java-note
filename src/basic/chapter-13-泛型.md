# 泛型
2021-10-19

- 基础语法：定义泛型类、创建泛型实例、调用它的方法：详情略
- 泛型类中的类型参数可以：声明成员变量，声明实例方法的返回类型
- 同一个泛型类，不同类型实参的实例不兼容，不可互相赋值，否则是编译错误

- 两个类型参数的泛型类

- 类型上界（bounded type）: `class NumFun<T extends Number>{}`
- 通配符实参: `boolean absEqual(NumFun<?> ob)`
  - Q. 通配符可以用在哪里？ class Box<?> 这种格式显然是不行的
  - 受限通配符：`void test(Gen<? extends A> param)`
  - 下界 和 `<? super Type>`

- 泛型方法：`static <T extends Comparable<T>, V extends> bolean arraysEqual(T[] x, V[] y)`;
  - 泛型类中的方法可以使用类的类型参数，从而自动成为与类型形参相关的泛型方法
  - 但方法可以声明自己的泛型参数

- 泛型构造函数：类不是泛型，而构造函数是泛型。有这样例子。
- 接口也可以是泛型的 `interface Comp<T>{}`

- 原类型和遗留代码

- Java 的泛型通过类型擦除+适当的隐式强制类型转换而实现，同一个泛型类，编译器不会创建不同的类型实例，只有一个类型

- Q. 上界、下界、继承关系
- Q. 可以同时声明上下界吗？`fun(Gen<? super Type extends Type2> p)`
  Ans: 不能。报错。
- Q. 类型形参可以指定下界吗？`class Box<T super Type>`
  Ans：不能。
- 泛型和子类型
## 原类型和遗留代码

使用泛型类时省略泛型参数，则得到原类型（raw type）。原类型可以和泛型实例互相赋值。
原类型的主要缺点是失去了类型安全性。
```java
Box<String> sBox = new Box<String>();

Box rawBox = new Box();

Box rawBox2 = sBox; // 语法可以，也没有警告

Box<Long> lBox = rawBox2; // 语法可以，但会有警告
// Long val = lBox.getVal(); // 语法可以，运行时会报错。
// 之所以如此，是中间经过原类型逃过了编译时类型检查

```

应该把原类型的使用限制在必须混合使用遗留代码和新的泛型代码的情况下，原类型只是
过渡措施，而非用于新代码的功能。

## 擦除和歧义 略
## 泛型的一些限制
1. 不能创建泛型参数的实例：`T obj = new T();` new 是不可以的，但可以 `T obj`。
  因为编译器无法确定T的具体类型，无法调用对应的构造函数。
2. 不能创建泛型类型的数组：`T[] arr = new T[10];`，理由同上。
3. 不能创建泛型实例的数组：`Box<Long>[] arr = new Box<Long>[10];` Q. 为什么不行？
  - 但可以这样做：`Box<?>[] arr = new Box<?>[10];`

## 泛型和子类型
[java 泛型类的继承关系和转型问题 - 翎野 - 博客园](https://www.cnblogs.com/lingyejun/p/14222676.html)
[Generics and Subtyping (The Java™ Tutorials > Bonus > Generics)](https://docs.oracle.com/javase/tutorial/extra/generics/subtype.html)

也就是说：`List<String>`不是`List<Object>`的子类。更清楚地说，二者不存在父子类关系。
子类值可以赋值给父类引用。
PS：但存了字符串的List可以看作是存了Object的list。

```
Let's test your understanding of generics. Is the following code snippet legal?

List<String> ls = new ArrayList<String>(); // 1
List<Object> lo = ls; // 2

Line 1 is certainly legal. The trickier part of the question is line 2. This boils down to the question: is a List of String a List of Object. Most people instinctively answer, "Sure!"

Well, take a look at the next few lines:

lo.add(new Object()); // 3
String s = ls.get(0); // 4: Attempts to assign an Object to a String!

Here we've aliased ls and lo. Accessing ls, a list of String, through the alias lo, we can insert arbitrary objects into it. As a result ls does not hold just Strings anymore, and when we try and get something out of it, we get a rude surprise.

The Java compiler will prevent this from happening of course. Line 2 will cause a compile time error.

In general, if Foo is a subtype (subclass or subinterface) of Bar, and G is some generic type declaration, it is not the case that G<Foo> is a subtype of G<Bar>. This is probably the hardest thing you need to learn about generics, because it goes against our deeply held intuitions.

For example, if the department of motor vehicles supplies a list of drivers to the census bureau, this seems reasonable. We think that a List<Driver> is a List<Person>, assuming that Driver is a subtype of Person. In fact, what is being passed is a copy of the registry of drivers. Otherwise, the census bureau could add new people who are not drivers into the list, corrupting the DMV's records.

To cope with this sort of situation, it's useful to consider more flexible generic types. The rules we've seen so far are quite restrictive.
```

```java
List<String> ls = new ArrayList<String>();
List<Object> lo = lo;

lo.add(new Object());
String s = ls.get(0); // Runtime Error
```



这个也不行：
```java
List<Object> lo = new ArrayList<Object>();
List<String> ls = lo; // incompatible types: List<Object> cannot be converted to List<String>

lo.add(new Object());
String s = ls.get(0); // Runtime Error
```
## ext: 为什么不允许创建泛型实例数组？

Java 中任何类型 T 的数组 T[] 都可以向上转型为其父类 S 型的数组 S[]。
```java
String[] strArr = new String[5];
Object[] objArr = strArr;   // 符合语法规则
// 符合语法规则，编译通过，但运行时会抛出 ArrayStoreException
objArr[0] = Integer.valueOf(1);
```

有办法hack出来类似的泛型数组骗过编译器的，不过那样运行时一样会出错。
泛型在设计上被称为不可具体化类型，如果允许它生成具体化类型的数组，在擦除之后会
发生经典的“猫插入狗列表”的问题。这是一个不易发现的逻辑错误，所以Java的决策者当机立断不允许这么做。

唯一绕过限制，创建泛型数组的方式，是先创建一个原生类型数组，然后再强制转型。
```java
List<Integer>[] ga = (List<Integer>[])new ArrayList[10];
```

PS：猫插入狗列表问题：
> Java会在编译后将范型的类型信息抹掉，如果Java允许我们使用类似
`Map<Integer, String>[] mapArray = new Map<Integer, String>[20];`的语句，
我们在随后的代码中可以把它转型为Object[]然后往里面放`Map<Double, String>`实例。
这样做不但编译器不能发现类型错误，就连运行时的数组存储检查对它也无能为力，它能看到的
是我们往里面放Map的对象，我们定义数组时指定的信息`<Integer, String>`在编译时已经
被抹掉了，于是而对它而言，只要是Map，都是合法的。想想看，我们本来定义的是装
`Map<Integer, String>`的数组，结果我们却可以往里面放任何Map，接下来如果有代码
试图按原有的定义去取值，后果是什么不言自明。

PPS：存入的时候能存，但取出数组时会报告运行时错误：ClassCastException

## ext2：堆污染
Heap pollution(堆污染)

当把一个不带泛型的对象赋值给一个带泛型的变量时, 就有可能发生堆污染.

堆污染在编译时并不会报错, 只会在编译时提示有可能导致堆污染的警告. 在运行时,如果
发生了堆污染, 那么就会抛出类型转换异常.

上面的猫插入狗列表就是一种堆污染。

## ext3：堆污染和@SafeVarargs
```java
public class ArrayBuilder {

  public static <T> void addToList (List<T> listArg, T... elements) {
    for (T x : elements) {
      listArg.add(x);
    }
  }
  public static void faultyMethod(List<String>... l) {
    Object[] objectArray = l;     // Valid
    objectArray[0] = Arrays.asList(42);
    String s = l[0].get(0);       // ClassCastException thrown here
  }
}

public class HeapPollutionExample {

  public static void main(String[] args) {

    List<String> stringListA = new ArrayList<String>();
    List<String> stringListB = new ArrayList<String>();

    ArrayBuilder.addToList(stringListA, "Seven", "Eight", "Nine");
    ArrayBuilder.addToList(stringListB, "Ten", "Eleven", "Twelve");
    List<List<String>> listOfStringLists =
      new ArrayList<List<String>>();
    ArrayBuilder.addToList(listOfStringLists,
      stringListA, stringListB);

    ArrayBuilder.faultyMethod(Arrays.asList("Hello!"), Arrays.asList("World!"));
  }
}
```
编译时，ArrayBuilder.addToList 方法的定义产生以下警告：

warning: [varargs] Possible heap pollution from parameterized vararg type T

当编译器遇到 varargs 方法时，会将 varargs 形式参数转换为数组。在方法
ArrayBuilder.addToList 中，编译器形参 T... elements 转换为 T[] elements，由于
类型擦除，最终变为 Object[] elements，这样存在堆污染的可能性。

如果你定义了可变的参数化方法，并且能保证该方法体不会抛出ClassCastException异常，你可以通过下面一些选项来消除警告
- 添加下面的注解  `@SafeVarargs`
- 添加下面的注解  `@SuppressWarnings({"unchecked", "varargs"})`
- 使用编译选项    `-Xlint:varargs`

ps：对于 ArrayBuilder.addToList，如果不加注解，编译时会有两个警告：
1.  在方法定义：[unchecked] Possible heap pollution from parameterized vararg type T
2.  在方法调用：`[unchecked] unchecked generic array creation for varargs parameter of type List<String>[]`
如果在定义处使用 `@SafeVarargs` 注解，这两个警告都不会有。
如果在定义处使用 `@SuppressWarnings({"unchecked", "varargs"})`注解，则不会有第一个警告，但第二个警告仍然有。

> 在java SE 5和6，程序员需要保证他调用的方法不会Heap Pollution发生，如果这个方法不是这个程序员写的，这很难保证，
但在java SE 7中，可保证不会发生Heap Pollution！
Q. JSE7 如何保证这一点的？

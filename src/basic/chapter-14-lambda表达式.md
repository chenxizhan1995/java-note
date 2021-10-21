# lambda表达式和方法引用
2021-10-20

- lambda 表达式的语法
- 函数式接口：是指有且仅有一个抽象方法的接口
- 方法引用：静态方法、实例方法、任意对象的实例方法、构造器引用
- 变量捕获

- lambda 表达式、匿名类、函数式接口、匿名方法、闭包
- 常用的函数式接口，java.util.function 包

## 语法
`parameter -> body`

每个 lambda 表达式实际是创建了一个匿名类实例。
```java
interface F1{
    void test();
}
interface F2{
    void test(int n);
}

F1 f1 = () -> 6.8;

F2 f2 = (n) -> n%2 == 0;
F2 f2 = n   -> n%2 == 0;
F2 f2 = (int n) -> n%2 ==0;
// 语法错误
// int n->n%2 ==0;
```
所以lambda表达式不能独立出现，
```java
1+2; // 这种是单独的表达式，可以

() -> 12; // 单独的lambda表达式，不可以。背后的逻辑是，编译器无法找到足够的信息。
// 可以为这种孤儿lambda表达式匹配指定默认匹配的函数式接口，但复杂且没必要
```

lambda 只能在定义了lambda表达式的目标类型的上下文中出现，包括：变量赋值、实参、返回值。

形参部分：
1. ()                               当形参个数为零时，必须有一个空圆括号。
2. (param1, param2, ..., paramN)    正常的形式是，圆括号内放形参，无需类型，因为可自动推断
3. (type1 param1, type2 param2, ..., typeN paramN)  当然可以显式指定类型
4. singleParam                       仅有一个形参，且不指定类型时，可以省略圆括号
5. 但 (type singleParam)             如果显式指定类型，圆括号不得省略

无形参，圆括号不许省略，如果 () -> body; 可以省略为 -> body; 那么
(x) -> body 就有歧义，是类型转换呢，还是恰有一个形参的 lambda 表达式呢？
`
形参可以指定类型，可以省略类型，但不可以混用， `(int x, y) -> x+1;` 是语法错误。

语句体部分，可以一个表达式，也可以是一个代码块。且
`param -> expr;` 可以认为等效于 `param -> { return expr; }`

即，只有一个表达式时，表达式的值就是lambda的返回语句的值。

要使用多个表达式，就要用花括号形式。

返回值不是必须的，要看其匹配的函数接口是否需要返回值。

lambda 表达式中的return语句只会结束lambda本身的执行，不会导致定义lambda表达式的
方法退出。
## 函数式接口
函数接口可以包含多个方法，只有有且仅有一个抽象方法，就是函数接口。

比如，其它方法可以是默认方法，静态方法，私有方法。

```java
public interface Fun{
    void test(int n);
}

public interface Fun{
    void test(int n);

    default String hello(){ return "hello"; }
}
```
## lambda、函数式接口和匿名类
lambda 对标的就是匿名类，而且是匿名方法的匿名类。

每个 lambda 表达式实际是创建了一个匿名类实例。

设有函数式接口 Fun
```java
public interface Fun{ int accept(int x, int y); }
```
则：
```java
Fun add = (x, y) -> x+y;
```
等价于
```java
Fun add = new Fun{
   @Override
   public int accept(int x, int y){
      return x + y;
   }
}
```
为什么要这么做呢？因为Java承诺一次编写到处运行，Java要兼容旧版JVM。
把lambda按语法糖处理，翻译成匿名类，就可以生成兼容低版本JVM的字节码文件了。

## 方法引用
- 静态方法引用：TypeName::methodName
- 实例方法引用：obj::methodName       对标 fun(...) 接口方法
- 实例方法引用：TypeName::methodName  对标 fun(TypeName, ...) 的接口方法，用于调用任意实例的实例方法
- 构造器引用：  TypeName::new         对标 TypeName fun(...) 的接口方法

静态方法引用，假设函数式接口签名为 `type fun(Type1 param1, Type2, param2);`，那
静态方法引用要求静态方法的返回值和参数列表完全一致（或者兼容）。

实例方法引用，obj::methodName，要求返回值和参数列表一致。
调用时对应关系为：`fun(arg1, arg2)  --> obj.methdoName(arg1, arg2);`

实例方法引用 `Type1::methodName`，要求返回值一致，函数式接口的第一个参数匹配调用对象，后续参数
匹配 methodName 的参数，（也就是 methodName 方法中的 this 会是函数式接口的第一个参数）。
调用时，对应关系为：`fun(arg1, arg2) ---> arg1.methodName(arg2)`。

构造器引用，返回值类型和参数列表匹配，就可以。

### ext：
数组的构造函数引用：`type[]::new`。
方法 `type[] fun(int n)`，可以对应 `type[]::new`.
一般，如果某个函数式接口要引用数组构造函数，那么该方法只接受一个int类型的参数。

PS：可以引用多维数组构造函数吗？
Ans: 实测可以，形如 `intp[][]::new`，但是它对应的函数式接口形式为：
`int[][] apply(int m);`，而不是想象中的`int[][] apply(int m, int n);`

## 变量捕获
lambda表达式可以使用和修改所在类的实例变量、静态变量，调用所在类的方法；可以访问
但只能访问 effective final 的局部变量。

ps：也是可以理解的，方法局部变量在方法结束后即销毁，但lambda表达式实际是匿名类的
实例，是对象，它可以在方法结束后继续存在，如果修改了局部变量，不好搞了。

## misc

lambda 表达式不可以声明类型参数。语法不允许。其实，lambda表示根据其对应的函数接口，
本身有"泛型"的意思。

- 对泛型方法的引用    `MyClass::<Integer>myGenMeth;`，
- 对泛型构造函数的引用`MyClass<Integer>::new`
- 对数组的构造函数引用`type[]::new`

## 常用预定义函数式接口
都早 java.util.function 包。还有些接口提供了默认方法和静态方法作为工具，可以留意一下。

如果需要编写通用的算法，就要定义合适的函数式接口以便接收lambda表达式。借助预定义
函数式接口，会方便一些。

基本的接口
```java
接口名              方法签名
Function<T, R>    R apply(T param)
Predicate<T>      boolean test(T param)

Supplier<T>       T get()
Comsumer<T>       void accept(T param)
```
类似的
```java
BiFunction<T, U, R> R apply(T, U)
BiPredicate<T, U>   boolean test(T, U)

BiConsumer<T, U>    void accept(T, U)
// 但是，没有 BiSupplier<T, U>，因为Java只允许一个返回值

// 还有一些针对基本类型的函数式接口
// 还有一些默认方法
```

Predicate 有默认方法 and, or, negate，以 and 为例：
### Predicate 的 and 方法
过滤一组字符串中，以 A 开头且长度小于 5 的字符串。
```java
@Test
public void whenFilterList_thenSuccess(){
   List<String> names = Arrays.asList("Adam", "Alexander", "John", "Tom");
   List<String> result = names.stream()
     .filter(name -> name.startsWith("A"))
     .filter(name -> name.length() < 5)
     .collect(Collectors.toList());

   assertEquals(2, result.size());
   assertThat(result, contains("Adam","Alexander"));
}

// 或者
@Test
public void whenFilterList_thenSuccess(){
   List<String> names = Arrays.asList("Adam", "Alexander", "John", "Tom");
   List<String> result = names.stream()
     .filter(name -> name.startsWith("A") && name -> name.length() < 5)
     .collect(Collectors.toList());

   assertEquals(2, result.size());
   assertThat(result, contains("Adam","Alexander"));
}
// 或者，借助 and
@Test
public void whenFilterListWithCombinedPredicatesUsingAnd_thenSuccess(){
    Predicate<String> predicate1 =  str -> str.startsWith("A");
    Predicate<String> predicate2 =  str -> str.length() < 5;

    List<String> result = names.stream()
      .filter(predicate1.and(predicate2))
      .collect(Collectors.toList());

    assertEquals(1, result.size());
    assertThat(result, contains("Adam"));
}
```

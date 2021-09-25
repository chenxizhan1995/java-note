# 其它数据类型与运算符
2021-09-25

## 数组
- 一维数组：声明、分配空间、初始化、下标访问、.length 属性
- for-each
- 多维数组：声明、分配空间、不规则多维数组
- 数组的快捷初始化语法

- 两种数组变量风格

Java中的数组是作为对象实现的，这点和C语言不同。

### 一维数组
- 声明一维数组的语法 `type[] arr = new type[N];`
- 数组以下标方式访问（读取和赋值）`arr[i]`，下标从 0 开始。

```java

// 声明
int[] arr;
// 分配空间
arr = new int[3];
// 赋值（初始化）
arr[0] = 1;
arr[1] = 2;
arr[2] = 3;

// 使用
for (int i = 0; i< arr.length; i++){
  System.out.println("arr["+i+"]:" + arr[i]);
}

// 声明和分配空间可以合并为一条语句
int[] arr2 = new int[3];
```

### for-each
```java
int[] a = new int[3];
// 赋值（初始化）
arr[0] = 1;
arr[1] = 2;
arr[2] = 3;

// 遍历数组
for (int i = 0; i< a.length; i++）{
  System.out.println(a[i]);
}
// 使用 for-each 遍历数组
for (int el:a）{
  System.out.println(el);
}
// for-each 迭代时，迭代元素对原集合/原数组是只读的，对迭代元素赋值并不影响原数组
for (int el:a）{
  el = el+10;
  System.out.println(el);
}
// 迭代完成，数组 a 的内容还是 1,2,3
```

for-each 用于两个方面：1. 数组 2. 由 Java 集合框架定义的集合对象
### 多维数组
二维数组 `type[][] arr = new type[m][n];` 是这样理解的：首先变量 arr 是一个数组，
它含有m个元素，每个元素 e 是都是类型为 `type[] `的一维数组，且数组 e 的长度为 n。
多维数组以此类推。

```java
// 规则的二维数组: 声明、分配空间
int [][] arr = new int[3][4];
// 初始化
arr[0][0] = 100;
arr[0][1] = 101;
arr[0][2] = 102;
arr[1][0] = 110;
arr[1][1] = 111;
arr[1][2] = 112;
arr[2][0] = 120;
arr[2][1] = 121;
arr[2][2] = 122;

// 二维数组分配空间时可以省略第二个维度
// 二维数组也可以是不规则的
int [][] arr2 = new int[3][];
arr2[0] = new int[3];
arr2[1] = new int[10];
arr2[2] = new int[0];
```

m 维数组同理；
```java
type[]...[] arr = new type[m1][m2]...[mn];
// 也可以在分配空间的时候只指定最左边的前k的维度的长度
type[]...[] arr = new type[m1][m2]..[mk][]..[];
// 例如
int[][][][][] arrd5 = new int[9][8][][][];
```

### 数组的快捷初始化语法
```java
// 声明、分配空间、初始化可以一步完成
int[] arr = {1,2,3};
// 此时，编译器会自动计算元素个数，分配等量的空间，然后把数组元素初始化进去。

// 多为数组，多套几个或括号
String[][] messges = {
  {"hello", "world"},
  {"Fuck", "Go away"}
};
```

数组的快捷初始化语法仅限于在数组声明时使用，数组声明后就不能在这样赋值了。
```java
// 这种写法，编译报错
int[] arr;
arr = {1,3,3};
```
这第二条语句有三个编译错误：
```
Test.java:5: error: illegal start of expression
        arr = {1,3,3};
              ^
Test.java:5: error: not a statement
        arr = {1,3,3};
               ^
Test.java:5: error: ';' expected
        arr = {1,3,3};
```
但可以改成如下方式
```java
int[] arr;
arr = new int[]{1,3,3};
```

Q. 语句 `int[] a = new int[3]{1,2,3};` 可以编译通过吗？
Ans：不能，JDK11 实测编译报错：`array creation with both dimension expression and initialization is illegal`
但这中写法是可以的 `int[] a = new int[]{1,2,3};`

方法调用时，可以通过这种方式

```java
void fun(int[][] arr){}

fun(new int[3][3]{{1,2,3}, {4,5,6}, {7,8,9}});
```
### 两种数组变量的表示方式

- type[] arrr 等价于 type arr[], 通常 Java 中使用前一种方式。

- `int[] a1, a2, a3;` 等价于 `int a1[], a2[], a3[];`
- `int[] a1, a2[], a3[][]; `等价于
```java
int[] a1;
int[][] a2;
int[][][] a3;
```


## 字符串
- 构造字符串的方式：1. 字符串常量 2. new String("hello")

- Java 中，字符串比较的正确方式是使用 equals() 方法：`s1.equals(s2)`。
- 常见的错误方式是 `s1 == s2`，对于引用类型，== 运算符判定为相等当且仅当二者指向同一个对象。

- 字符串对象（String 对象）是不可变的，String 类的每个修改字符串的方法其实都是返回了新的字符串对象。
- 对应的，Java 提供了“可变字符串”类：JDK1.0 的StringBuffer和JDK5.0开始的StringBuilder。二者接口相同，区别在于
  StringBuffer 是线程安全的，而StringBuilder是线程不安全的，StringBuilder比StringBuffer效率更高一些，根据
  实际情况选择合适的。

TODO: 字符串是个很大的主题，有东西可挖。
## 局部类型推断和 var

变量可以在声明时直接初始化，且初始化时值必须和变量类型相同（或可自动转换），
所以原则上不需要为初始化的变量显式指定类型，它们的类型可以从值的类型推导出来。
```java
int a = 13;

// 借助局部类型推断可以写成
var a = 13;

// 还可以声明数组
var arr = new int[10]; // arr 推断为 intp[] 类型。

```

var 的使用有一些限制和需要注意的地方。

1. var 只能用于局部变量的类型推断，在声明实例变量、形参和返回值类型时，不可以使用 var。
2. var 对数组变量推断时，不可以跟着方括号， `var[] a = new int[10];` 和 `var a[] = new int[10];` 都是错误的。\
   var 不能与数组初始化器一起使用： `var a = {1,3,3};` 是错误的。
3. 需要强调，只有在初始化变量时才能用 var。`var foo;` 这条语句是错误的，因为没有初始化，无法推定类型。

4. var 的主要用途是用作引用类型的变量的类型推断。涉及到泛型，变量的类型会很长，使用自动推断就免得书写特别长的类型信息，
  可以使代码更简洁。用于声明引用类型时，不能用null作为初始化器，因为无法推定具体的类型。ps：最多推定为 Object。
5. 过多使用 var 会降低代码的可读性，本质上，这是一个应该明智使用的特性。
6. for 循环中使用 var。
  - `for (var i = 0; i< 10; i++){ }`
  - `for (var item:list){ }`

Q. 会有不准确的时候。例如
```java
List<String> list1 = new ArrayList<>();
var list2 = new ArrayList<String>();
```
上面，list1 是 `List<String>` 类型，而 list2 则是 `ArrayList<String>`类型。
不过凡是可以使用父类变量的地方都可以替换为子类变量，同时var仅限于局部变量，所以无所谓了。
## 位运算符
- 与或取反，移位运算
- 无符号右移，也叫充零右移。
- byte、short、char 类型，在使用位运算符的时候也会先把数值提升为 int 再位移，所以要当心某些情形下的表现。
  - 正值位移无移位运算没问题
  - 负值，执行充零右移时，byte 要移动24个1后才会出现0，short 则要移动16个1才会出现零。
- Q. char 升级为 int，是什么情况？
  Ans: 左侧一律补零。
```java
{{#include ../code/basic/ch05/CharToInt.java}}
```

根据优先级：
```java
int status = 1;
if ((status & 1) != 8); // 这里的括号是必须的，否则 status & 1 != 8 等价于 status & (1 != 8)，而这个会报错。

a + b >> c & d + e 等价于 `((a+b) >> c) & (d + e)`
```

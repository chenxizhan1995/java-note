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

- TODO:原类型和遗留代码

- Java 的泛型通过类型擦除+适当的隐式强制类型转换而实现，同一个泛型类，编译器不会创建不同的类型实例，只有一个类型

- Q. 上界、下界、继承关系
- Q. 可以同时声明上下界吗？`<T super Type extends Type2>`
- Q. 类型形参可以指定下界吗？`class Box<T super Type>`
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

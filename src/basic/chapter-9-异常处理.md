# 异常处理
2021-09-29

- 异常：异常是在运行时发生的错误。
- 相关关键字：try、catch、finally、throw、throws
- 异常层次结构
- 受检异常：是 Exception 异常且不是 RuntimeException 异常，就是受检异常。
- throws：如果一个方法会产生受检异常，就要用 throws 关键字声明。
  - ps：java 默认方法会抛出非受检异常，所以不需要声明。（Error也是非受检异常）。
  - ps：语法允许 throws 中标明非受检异常。
- 多重捕获：一个catch中捕获多个异常类型称为多重捕获，否则叫做单一捕获。
  - ps：多重捕获中异常变量自动是 final 的，可以显式用final修饰，但不必要。
  - ps：单一捕获中的异常变量绝不自动为final，但可以显式指定为final或者等效为final。
- 自定义异常：用户可以通过继承 Exception 类实现自定义异常。
  - ps：是否实现为 RuntimeException，根据需要。

- finally

- JDK7 新特性-try-with-resource：在IO中介绍
- JDK7 新特性：final rethrow
## 异常的层次结构
Java 中所有的异常都用类来表示。类 Throwable 是所有异常类的根。它有两个子类：Error 和 Exception。

Error 表示发生在 JVM 中的错误，这些异常不是程序能处理的。

Exception 表示由程序导致的异常，这些异常是程序应当处理的。

Exception 有一个重要的子类 RuntimeException，用于表示各种常见的运行时错误。

## final rethrow

```java
public void testTypicalRethrow() throws Exception {
  try {
    throwSqlException();
    throwIoException();
  } catch (Exception e) {
    throw e;
  }
}

private void throwSqlException() throws SQLException {
 throw new SQLException();
}

private void throwIoException() throws IOException {
 throw new IOException();
}
```

有了 JDK 7 的 final rethrow 特性，可以把方法的异常类型声明的更精确。
```java
public void testTypicalRethrow() throws IOException, SQLException {
  try {
    throwSqlException();
    throwIoException();
  } catch (Exception e) {
    throw e;
  }
}
```

testTypicalRethrow 方法可能会抛出 SQLException 和 IOException 两种异常，我们希望
使用一个更宽泛的异常类型捕获它们，同时又期望方法签名的 throws 语句尽可能精确，
这在 jdk 7 之前是办不到的，语法不允许。

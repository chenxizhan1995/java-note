# Java 基础
## hello-world

```java
{{#include ../code/Hello.java}}
```
- 程序入口：Java 整个程序的入口是 main() 方法，有固定格式：`public static void main(String[] args){}`

- 语句：Java 语句以分号结束
- 代码块：花括号包围的一组语句叫做代码块，语法结构上，代码大块可以用在任何单条语句可以出现的地方。
- 缩进：缩进自由，但建议四个空格做一级缩进

- 注释：注释有三种：单行注释、多行注释、文档注释

- 变量：先声明，后使用
- 标识符规则：
  - 数字字母下划线、美元符号
  - 不以数字开头
  - 长度不限
  - 避开关键字
  ps：字母，不仅是英文字母，Unicode 中的各种字母都行，比如汉字。

## if 和 for

```java
int a = 5;
if (a > 3){
  System.out.println("a 大于 3 ");
} else {
  System.out.println("a 不大于3");)
}


for (int i = 1; i<10; i++){
  System.out.println("i:" + i);
}
```


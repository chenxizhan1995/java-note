# 程序控制语句
程序控制语句只要两种顺序和跳转。

顺序、分支、循环。

## 分支语句
Java 中分支语句有两种，if 和 switch。

### if 语句
```java
// if
if (condition){
  // statements
}

// if-else
if (condition){
  // statements
} else {
  // statements
}

// if-else if-else
if (condition1){
  // statements
} else if(condition2){
  // statements
} else if(condition3){

} ...

else {

}
```
例如：
```java
int a = 3, b = 4;

if (a < b){
  System.out.println("3 < 4 是真的");
} else {
  System.out.println("3 < 4 不成立");
}
```
### switch 语句
```java
switch (ch){
  case const1:
    break;
  case const2:
    break;
  default:
    break;
}
```
- ch 必须是整型或者字符串。
- 找到匹配的case分支后开始执行，一直到break或者switch语句的结束。如果没有匹配的case分支，就从default分支开始执行，一直到
  break语句或者到大switch语句的结束。

## 循环
Java 中有三种循环语句，for，while，do-while。
其中 for 语句还有若干变体。

```java
for (stat1; stat2; stat4) {
  stats3;
}

while (condition) {
  stats;
}

do {

} while(condition);

```

这三种循环的花括号语句体可以换成分号，表示空语句，比如 `int sum = 0; for (int i = 1; i<= 100; sum += i, i++;);`

例如：
```java
for (int i = 1; i< 10; i++){
    System.out.println(i + ", ");
}
```

### for 循环的变体
1. 圆括号内的语句，可以是逗号分隔的多条语句。
2. 圆括号内的三条语句都是可选的。省略stat2，则相当于判断条件永远为真。

```java
for (stat1; stat2; stat4){
  stats3;
}
```

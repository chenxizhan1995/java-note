# fianl 字段的初始化时机和限制？
2021-09-26


8.3.1.2 final Fields
A field can be declared final (§4.12.4). Both class and instance variables (static
and non-static fields) may be declared final.

A blank final class variable must be definitely assigned by a static initializer of
the class in which it is declared, or a compile-time error occurs (§8.7, §16.8).

A blank final instance variable must be definitely assigned at the end of every
constructor of the class in which it is declared, or a compile-time error occurs (§8.8,
§16.9).

A blank final is a final variable whose declaration lacks an initializer.

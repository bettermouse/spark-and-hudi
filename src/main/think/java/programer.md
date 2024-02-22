#  @FunctionalInterface注解
我们提到如果接口中只有一个抽象方法（可以包含多个默认方法或多个 static 方法）,
那么该接口就是函数式接口。@FunctionalInterface 就是用来指定某个接口必须是函数式接口,
所以 @FunInterface 只能修饰接口，不能修饰其它程序元素。
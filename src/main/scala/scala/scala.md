# Scala 传名参数和传值参数
https://juejin.cn/post/7052590747635646472

# 偏函数

# 柯里化
def sum(x:Int,y:Int)=x+y

curry化最大的意义在于把多个参数的function等价转化成多个单参数function的级联,
这样所有的函数就都统一了，方便做lambda演算。 在scala里，curry化对类型推演也有帮助，
scala的类型推演是局部的，在同一个参数列表中后面的参数不能借助前面的参数类型进行推演，
curry化以后，放在两个参数列表里，
后面一个参数列表里的参数可以借助前面一个参数列表里的参数类型进行推演。
这就是为什么 foldLeft这种函数的定义都是curry的形式 
https://blog.csdn.net/qq_32252917/article/details/103621077

def sum(x:Int)(y:Int) = x + y
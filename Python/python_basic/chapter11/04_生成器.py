# 1.生成器:
#  1.生成器函数:函数体中如果出现了 yield 关键字，那该函数是『生成器函数』。
#  2.生成器对象:调用『生成器函数』时，其函数体不会立刻执行，而是返回一个『生成器对象』。
#  备注:不管能否执行到 yield所在的位置，只要函数中有 yield关键字，那该函数，就是『生成器函数』。

# 2.写在「生成器函数』中的代码，需要通过『生成器对象』来执行:
#  1.调用『生成器对象』的_next_方法，会让『生成器函数』中的代码开始执行。
#  2.当『生成器函数』中的代码开始执行后，遇到 yield会"暂停"执行，并且其内部会记录"暂停"的位置。
#  3.后续调用_next_方法时，都会从上一次"暂停"的位置，继续运行，直到再次遇到 yield。
#  4.遇到 return 会抛出 StopIteration 异常，并将 return 后面的表达式，作为异常信息。
#  5.yield后面所写的表达式，会作为本次 next_方法的返回值。井

# 3.生成器对象是一种特殊的迭代器(本质是通过 yield 自动实现了迭代器协议)。

# 4.yield 也能写在循环里

# 5.yield from 能把一个『可迭代对象』里的东西依次 yield 出去。(替代:for + yield)

# 6.使用:生成器.send(值)可以让生成器继续执行的同时，给上一次 yield 传值。
#  备注1:next 只能取值,send 既能取值,也能送值
#  备注2:第一次启动生成器，不能传值!

# 生成器表达式:一种用类似列表推导式的语法，快速创建生成器对象的方式。
# 语法格式:(表达式for变量in可迭代对象)

# 用生成器实现斐波那契数列
# region
def fibo(total):
    per = 1
    cur = 1

    for index in range(total):
        if index < 2:
            yield 1
        else:
            value = per + cur
            per = cur
            cur = value
            yield value

f1 = fibo(10)

for item in f1:
    print(item)

# endregion
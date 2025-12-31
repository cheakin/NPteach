# 装饰器:
# 1.装饰器是一种[可调用对象](通常是函数)，它能接收一个函数作为参数，并且会返回一个新函数。
# 2.装饰器可以在不修改原函数代码的前提下，增强或改变原函数的功能。

# 关键点:
# 1.接收被装饰的函数、同时返回新函数(wrapper)
# 2.装饰器"吐出来"的是 wrapper 函数,以后别人调用的也是 wrapper 函数。
# 3.为了保证参数的兼容性，wrapper 函数要通过 *args和 **kwargs 接收参数。
# 4.wrapper 函数中主要做的是:调用原函数(被装饰的函数)、执行其它逻辑，但要记得将原函数的返回值return 出去。

# 实际应用:在不改变原函数的前提下，给函数统一加上:日志、计时、校验、缓存 等功能

# # 在不修改原函数的前提下，给函数增加额外的功能
# def say_hello(func):
#     def wrapper(*args, **kwargs):
#         print('计算！')
#         return func(*args, **kwargs)
#     return wrapper
#
# # @say_hello
# def add(x, y):
#     res = x + y
#     print(f'{x} + {y} = {res}')
#     return res
#
# # 正常调用
# # result = add(1, 2)
# # print(result)
#
#
# # 手动装饰：写起来会麻烦一些，不推荐，推荐语法糖：@装饰器名
# add_pro = say_hello(add)
# result = add_pro(1, 2)
# print(result)

print('-------------------------')
# 进阶:带参数的装饰器(三层嵌套，外层接收配置，中间层接收函数、内层接收具体参数)
# def say_hello(msg):
#     def outer(func):
#         def wrapper(*args, **kwargs):
#             print(f'{msg}计算！')
#             return func(*args, **kwargs)
#         return wrapper
#     return outer
#
# @say_hello('加法')
# def add(x, y):
#     res = x + y
#     print(f'{x} + {y} = {res}')
#     return res
#
# @say_hello('减法')
# def sub(x, y):
#     res = x - y
#     print(f'{x} - {y} = {res}')
#     return res
#
# result = add(1, 2)
# print(result)



print('====================')
# 进阶：多个装饰器一起使用

def test1(func):
    print('我是test1装饰器')
    def wrapper(*args, **kwargs):
        print('我是test1追加的逻辑')
        return func(*args, **kwargs)
    return wrapper

def test2(func):
    print('我是test2装饰器')
    def wrapper(*args, **kwargs):
        print('我是test2追加的逻辑')
        return func(*args, **kwargs)
    return wrapper

@test1
@test2
def add(x, y):
    res = x + y
    print(f'{x} + {y} = {res}')
    return res

result = add(10, 20)
print(result)
# 类装饰器:
# 1.包含__ca11__方法的类，就是类装饰器。
# 2.像调用函数一样，去调用类装饰器的实例对象，就会触发__ca11_方法的调用。
# 3.__ca11_方法通常接收一个函数作为参数，并且会返回一个新函数。


# class SayHello:
#     def __call__(self, func):
#         def wrapper(*args, **kwargs):
#             print('计算！')
#             return func(*args, **kwargs)
#         return wrapper

# @SayHello()
# def add(x, y):
#     res = x + y
#     print(f'{x} + {y} = {res}')
#     return res

# 正常调用
# result = add(1, 2)
# print(result)

# 使用 SayHello 去装饰 add 函数 （手动装饰）
# say = SayHello()
# say_pro = say(add)
# result = say_pro(1, 2)
# print(result)



# 带参数的装饰器
# class SayHello:
#     def __init__(self, msg):
#         self.msg = msg
#
#     def __call__(self, func):
#         def wrapper(*args, **kwargs):
#             print(f'计算{self.msg}！')
#             return func(*args, **kwargs)
#         return wrapper
#
# @SayHello('加法')
# def add(x, y):
#     res = x + y
#     print(f'{x} + {y} = {res}')
#     return res
#
# result = add(1, 2)
# print(result)



# 多个类装饰器的使用
class Test1:
    def __call__(self, func):
        def wrapper(*args, **kwargs):
            print('我是Test1追加的逻辑')
            return func(*args, **kwargs)
        return wrapper

class Test2:
    def __call__(self, func):
        def wrapper(*args, **kwargs):
            print('我是Test2追加的逻辑')
            return func(*args, **kwargs)
        return wrapper

@Test2()
@Test1()
def add(x, y):
    res = x + y
    print(f'{x} + {y} = {res}')
    return res

result = add(1, 2)
print(result)
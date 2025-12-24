# 定义函数(使用*args去接收,可变位置参数)
def test1(*args):
    print('test1')
    print(args)

# 调用函数
test1('张三', '男', 18, 172)
# 错误示例
# test1('张三', '男', 18, height=172)


# 定义函数(使用**kwargs去接收,可变关键字参数)
def test2(**kwargs):
    print('test2')
    print(kwargs)

# 调用函数
test2(name='张三', gender='男', age=18, height=172)
# 错误示例
# test2('张三', '男', 18, 172)
# test2('张三', '男', 18, height=172)


# 定义函数(同时使用: 可变位置参数,可变关键字参数)
def test3(a, b, *args, c='hello', **kwargs):
    print('test3')
    print(a)
    print(b)
    print(args)
    print(c)
    print(kwargs)

# 调用函数
test3('张三', '男', '抽烟', '喝酒', '你好', age = 18, height = 172)
test3('张三', '男', '抽烟', '喝酒', '你好', age = 18, height = 172)

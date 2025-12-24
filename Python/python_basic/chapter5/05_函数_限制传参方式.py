# 函数·限制传参方式
# 具体规则:`/` 前边只能用位置参数，`*` 后面只能用关键字参数。


# 定义函数(使用/和*闲置传参方式)
def greet(name, /, gender, *, age, height):
    print(f'我叫{name}, 性别{gender}, 年龄{age}, 身高{height}')


# 正确示例
greet('张三','男', age=18, height=172)
greet('张三', gender='男', height=172, age=18)


# 错误示例
# greet(name='张三', gender='男', age=18, height=172)
# greet('张三','男', 18, height=172)
# 函数·关键字参数
# 关键字参数:调用函数时，通 过`形参名=值` 的形式传递实参,


# 定义函数
def greet(name, gender, age, height):
    print(f'我叫{name}, 性别{gender}, 年龄{age}, 身高{height}')


# 调用函数（使用关键字参数）
greet(name='张三', gender='男', age=18, height=172)
greet(height=172, age=18, gender='男', name='张三')
greet('张三', '男', height=172, age=18)


# 错误示例
# greet(height=172, age=18, '张三', '男')
# greet(name='张三', gender='男', 18, 172)
# greet(name='张三', '男', age=18, 172)
# greet(height=172, age=18, gender='男', name='张三', age=18)
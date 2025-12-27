# 1.函数也是对象
# def welcome():
#     print('你好啊')
#
# print(type(welcome)) # 不是打印调用，是打印类型
# # 会发现welcome函数是function类的实例

# 2.函数可以动态添加属性
# def welcome():
#     print('你好啊')
# welcome.desc = "这是一个打招呼的函数"
# welcome.version = 1.0
#
# print(welcome.desc)
# print(welcome.version)

# 3.函数可以赋值给变量
# def welcome():
#     print('你好啊')
# welcome.desc = "这是一个打招呼的函数"
# welcome.version = 1.0
#
# say_hello = welcome
# say_hello()
# print(say_hello.desc)

# 4.可变参数vs不可变参数
# 不可变参数
# a = 666
# def welcome(data):
#     print('data修改前', data, id(data))
#     data = 888 # int 是不可变对象，所以会重新创建一个实例
#     print('data修改后', data, id(data))
#
# print('函数调用前', a, id(a))
# welcome(a)
# print('函数调用后', a, id(a))


# 可变参数
# a = [10, 20, 30]
# def welcome(data):
#     print('data修改前', data, id(data))
#     data[2] = 99
#     print('data修改后', data, id(data))
# print('函数调用前', a, id(a))
# welcome(a)
# print('函数调用后', a, id(a))


# 5.函数也可以作为参数
# def welcome():
#     print('你好啊')
#
# def caller(f):
#     print('caller被调用了')
#     f()
#
# caller(welcome) # 不是调用，是当错变量传入

# 6.函数也可以作为返回值
def welcome():
    print('你好啊')
    def show_msg(msg):
        print(msg)
    return show_msg

result = welcome()
print(type(result))
result('hello')
# 另一种写法
welcome()('hello')

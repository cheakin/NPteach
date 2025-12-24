# 定义有内容的【可变集合】
s1 = {10, 20, 30, 40, 50}
s2 = {'hello', 'world'}
s3 = {10, 'hello', 'world', True, 3.14}
print(type(s1), s1)
print(type(s2), s2)
print(type(s3), s3)

# 定义有内容的【不可变集合】
s1 = frozenset({10, 20, 30, 40, 50})
s2 = frozenset({10, 'hello', 'world', True, 3.14})
s3 = frozenset({10, 'hello', 'world', True, 3.14})
print(s1)
print(s2)
print(s3)

# fronzenset 接收的参数，可以是任意可迭代对象，但最终返回一定是【不可变集合】


# 集合中不能嵌套【可变集合】，但可以嵌套【不可变集合】
s1 = {10, 20, 30, 40, 50}
s2 = frozenset({100, 200, 300, 400, 500})
l1 = [666, 777, 888]
l2 = ('hello', 'world')

# 正确示例
s3 = {11,22,33, s2}
s4 = {11,22,33, l2}
# 错误示例
# s3 = {11,22,33, s1}
# s4 = {11,22,33, l1}
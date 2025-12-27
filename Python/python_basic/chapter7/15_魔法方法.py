# 概念：以__xxx___ 方式命名的特殊方法

class Person:
    def __init__(self, name, age, idcard):
        self.name = name
        self.age = age
        self.idcard = idcard

    # 当执行 print() 或 str() 时调用
    def __str__(self):
        return f'{self.name}-{self.age}-{self.idcard}'

    # 当执行 len() 时调用
    def __len__(self):
        return len(p1.__dict__)

    def __lt__(self, other):
        return self.age < other.age

    def __gt__(self, other):
        return self.age > other.age

    def __eq__(self, other):
        return self.__dict__ == other.__dict__

    # 当访问实例对象身上不存在的属性时调用
    def __getattr__(self, item):
        return f'您访问的{item}不存在'



p1 = Person('张三', 18, '男')
p2 = Person('李四', 19, '女')

# print(p1)
# print(p2)

# res = len(p1)
# print(res)

# print(p1 < p2)

# print(p1 > p2)

# print(p1 == p2)

print(p1.address)


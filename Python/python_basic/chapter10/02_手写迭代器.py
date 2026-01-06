# 知识点1:能被for 循环遍历的对象，被称为:可迭代对象(iterable)

# 知识点2:可迭代对象(iterable)

# 知识点3:调用 iter__方法会得到:
# 备注1:__iter_是一个魔法方法，当调用 iter 函数时，__iter__会自动调用。
# 备注2:可迭代对象.__iter___() 等价于 iter(可迭代对象)。
# 备注3:如果 iter(obj)能得到一个迭代器(iterator)，那 obj就是可迭代对象。

# 知识点4:迭代器(iterator)拥有_next_方法，每次调用都会根据当前的状态，返回下一个元素。
# 备注1:迭代器.__next_() 等价于 next(迭代器)。
# 备注2:当所有元素全都取出后，若继续调用__next_方法,Python会抛出 StopIteration 异常。

# 知识点5:迭代器(iterator)也有iter

#知识点6:迭代器协议
#   1.能被 iter()接受
#   2.能被 next()一步一步取值

class Person:
    def __init__(self, name, age, gender, address):
        self.name = name
        self.age = age
        self.gender = gender
        self.__index = 0
        self.__attr = [name, age, gender, address]

    def __iter__(self):
        self.__index = 0
        return self

    def __next__(self):
        if self.__index >= len(self.__attr):
            raise StopIteration
        self.__index += 1
        return self.__attr[self.__index - 1]

# 验证
p = Person('张三', 18, '男', '云南昆明')
# it = iter(p)

for item in p:
    print(item)

for item in p:
    print(item)
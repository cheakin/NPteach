# 定义一个Person类
class Person:
    def __init__(self, name, age, idcard):
        self.name = name        # 公有属性:当前类中、子类中、类外部，都可以访问
        self._age = age         # 受保护的属性：当前类中、子类中，都可以访问
        self.__idcard = idcard  # 私有属性：仅能在当前类中访问
    def speak(self):
        print(f'我叫：{self.name}-{self._age}-{self.__idcard}')

# 定义一个Student类，继承自Person类
class Student(Person):
    def hello(self):
        print(f'我是学生：{self.name}-{self._age}-{self.__idcard}')


p1 = Person('张三', 18, 430201199509193270)
p1.speak()
print(p1.name)
# 在类的外部，如果强制访问【受保护的属性】也能访问到，但不推荐
print(p1._age)
# 在类的外部，如果强制访问【私有属性】会报错
# print(p1.__idcard) # 会报错
# Python底层是通过重命名的方式，实现私有属性的
print(p1.__dict__)

# s1 = Student('张三', 18, 430201199509193270) # 会报错
# s1.hello()
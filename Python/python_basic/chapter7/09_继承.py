# 定义一个Person类
class Person:

    # 初始化方法（给实例添加属性）
    def __init__(self, name, age, gender):
        self.name = name
        self.age = age
        self.gender = gender

    def speak(self, msg):
        print(f'我叫{self.name}, 年龄是{self.age}，性别是{self.gender}，我想说{msg}')

# 定义一个Student类，继承自Person类
class Student(Person):
    # pass
    def __init__(self, name, age, gender, stu_id, grade):
        # 方式1, 推荐
        # super().__init__(name, age, gender)

        # 方式2, 多重继承时可以使用
        Person.__init__(self, name, age, gender)

        # 子类独有的属性,需要自己手动完成初始化
        self.stu_id = stu_id
        self.grade = grade


# 创建Student类的实例对象
s1 = Student('李华', 16, '男')
print(s1.__dict__)
print(type(s1))

s1.speak('你好！')

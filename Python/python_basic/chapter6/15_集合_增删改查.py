# 增
# add方法, 向集合中添加元素
# s1 = {10, 20, 30, 40, 50}
# s1.add(60)
# print(s1)

# update方法，向集合中添加元素（必须传递可迭代对象，如：列表、元组，集合等）
# s1 = {10, 20, 30, 40, 50}
# s1.update([60,70])
# s1.update((70,80))
# s1.update({100, 200})
# s1.update(range(300, 308))
# print(s1)



# 删
# remove方法：从集合中移除元素（移除不存在的元素，会报错）
# s1 = {10, 20, 30, 40, 50}
# s1.remove(20)
# # s1.remove(60)
# print(s1)

# discard方法:从集合中移除元素（移除不存在的元素，不会报错）
# s1 = {10, 20, 30, 40, 50}
# s1.discard(20)
# s1.discard(60)
# print(s1)

# pop方法：从集合中移除一个任意元素，返回值是移除的元素
# s1 = {10, 20, 30, 40, 50, 60}
# result = s1.pop()
# print(result)

# clear方法：清空集合
# s1 = {10, 20, 30, 40, 50, 60}
# s1.clear()
# print(s1)




# 改，使用 remove + add 方法实现
s1 = {10, 20, 30, 40, 50, 60}
s1.remove(20)
s1.add(66)
print(s1)

list1 = [10, 20, 30, 40, 50, 70, 80, 90, 100]
print(list1)

list2 = list1[0:5:1]
print(list2)

list3 = list1[1:8:3]
print(list3)

list4 = list1[::]
print(list4)

list5 = list1[:999:]
print(list5)

list6 = list1[3::]
print(list6)

list7 = list1[::4]
print(list7)

list8 = list1[7:2:-1]
print(list8)

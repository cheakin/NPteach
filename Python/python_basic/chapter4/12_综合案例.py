print('🏆欢迎来到：答题闯关挑战赛（输入q可随时退出）\n')

# 题目和答案
ques1, ans1 = 'Python中用于输出的的函数是？', 'print'
ques2, ans2 = 'Python中用语表示逻辑“并且”的关键字是？', 'and'
ques3, ans3 = 'Python属于编译型还是解释型语言？', '解释型'

# 最多可尝试次数
max_tries = 3
# 总关卡数
total_levels = 3
# 是否处于可以游戏状态
is_playing = True

tries = 0
for level in range(1, total_levels + 1):
    print(f'******🏷当前是第{level}关*****')
    if level == 1:
        ques, ans = ques1, ans1
    elif level == 2:
        ques, ans = ques2, ans2
    else:
        ques, ans = ques3, ans3

    while tries < max_tries:
        user_input = input('📢' + ques)
        if user_input == ans:
            print('✅回答正确！\n')
            level += 1
            break
        elif not user_input:
            print('⚠️你的输入为空，请重新作答:')
        elif user_input == 'q':
            print('主动退出游戏......\n')
            break
        else:
            tries += 1
            print(f'❌回答错误，你还有{max_tries - tries}次机会\n')
    if tries == max_tries:
        break
if tries < max_tries:
    print('🎉🎉🎉🎉🎉恭喜你已经全部通关🎉🎉🎉🎉🎉')
else :
    print('🚪游戏结束，已退出')

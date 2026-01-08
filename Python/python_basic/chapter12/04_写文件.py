# 文件操作的核心 open函数:它可以打开或创建文件，且支持多种操作模式，返回值是『文件对象』。
# open 函数最常用的三个参数如下:
#   1.file:要操作的文件路径
#   2.mode:文件的打开模式
#       r:读取(默认值)
#       w:写入，并先截断文件
#       x:排它性创建，如果文件已存在，则创建失败
#       a:打开文件用于写入，如果文件存在，则在文件末尾追加内容
#       b:二进制模式
#       t:文本模式(默认值)
#       +:打开用于更新(读取与写入)3.encoding:字符编码
#   3.encoding:字符编码#
import time

# 测试 w 模式
# with open('b.txt','wt', encoding='utf-8') as file :
#     file.write('hello world')


# 测试 x 模式
# with open('b.txt','xt', encoding='utf-8') as file :
#     file.write('hello world')

# 测试 a 模式
# with open('b.txt','at', encoding='utf-8') as file :
#     file.write('!!!')

# 在 Python 中文件写入时，并不是每写一次就立刻落盘，而是:先写到"缓冲区"里。
# with open('demo.txt','wt', encoding='utf-8') as file :
#     file.write('你好1')
#     file.flush()
#     file.write('你好2')
#     time.sleep(1000)
#     file.write('你好3')
#     file.write('你好3')

# 测试 rt+
with open('b.txt','rt', encoding='utf-8') as file :
    # seek(offset，whence)方法:用于改变文件对象指针的位置，参数说明如下:
    #   offset:偏移量，要移动多少距离
    #   whence: 参考点，从哪里开始计算偏移，有三种取值:
    #       0: 从文件开头计算(默认值)
    #       1: 从当前位置计算
    #       2: 从文件末尾计算
    # 注意: 在文本模式下，不要随意去定位中文字符位置，否则可能破坏文件编码。
    file.seek(0, 2)
    file.write('你好')


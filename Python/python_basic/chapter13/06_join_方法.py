# join 方法的作用:阻塞当前进程，等 join 前面的进程执行完，再继续往下执行。
# join(timeout)，其中 timeout 是可选参数，表示等多久，单位是秒。

# 注意点:
# 1.p.join()不是让进程 p等，而是让"执行这行join 代码的那个进程"去等，等的是 p这个进程。
# 2.当达到了 timeout 所指定的时间后，进程并不会终止，只是"不再等"了。
# 3.join 必须在 start 之后

import os
import time
from multiprocessing import Process

def speak():
    for index in range(10):
        print(f'我是说话{index}, 进程pid:{os.getpid()}, 父进程id:{os.getppid()}')
        time.sleep(1)

def study():
    for index in range(15):
        print(f'我是学习{index}, 进程pid:{os.getpid()}, 父进程id:{os.getppid()}')
        time.sleep(1)

if __name__ == '__main__':
    print('我是主进程中的[第一行]打印')
    p1 = Process(target=speak)
    p2 = Process(target=study)
    p1.start()
    p1.join(5)
    p2.start()

    # p1.join() # 让主进程等p1进程执行完毕后，主进程再继续执行。
    # p2.join() # 让主进程等p2进程执行完毕后，主进程再继续执行。

    print('我是主进程中的[最后一行]打印')
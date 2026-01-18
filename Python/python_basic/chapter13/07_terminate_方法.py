
import os
import time
from multiprocessing import Process

def speak():
    for index in range(10):
        print(f'我是说话{index}, 进程pid:{os.getpid()}, 父进程id:{os.getppid()}')
        time.sleep(1)

def study():
    try:
        for index in range(15):
            print(f'我是学习{index}, 进程pid:{os.getpid()}, 父进程id:{os.getppid()}')
            time.sleep(1)
    # 注意:使用terminate终止进程，不会引起 finally执行!#
    finally:
        print('finally里的逻辑')


if __name__ == '__main__':
    print('我是主进程中的[第一行]打印')
    p1 = Process(target=speak)
    p2 = Process(target=study)
    p1.start()
    p2.start()

    time.sleep(3)
    print('准备终止p1......')
    p1.terminate()
    print(p1.is_alive()) # 查看p1是否“活着”
    p1.join() # 等操作系统彻底终止掉了p1进程
    print(p1.is_alive()) # 查看p1是否“活着”

    print('我是主进程中的[最后一行]打印')
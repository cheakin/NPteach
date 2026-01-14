
import os
import time
from multiprocessing import Process, current_process

def speak(a, b):
    for index in range(10):
        print(f'{current_process().name} 我是说话{index}, args:{a} {b}, 进程pid:{os.getpid()}, 父进程id:{os.getppid()}')
        time.sleep(1)

def study(msg):
    for index in range(15):
        print(f'我是学习{index}, kwargs:{msg}, 进程pid:{os.getpid()}, 父进程id:{os.getppid()}')
        time.sleep(1)

if __name__ == '__main__':
    print('我是主进程中的[第一行]打印')
    # Process的参数:
    #       group:  默认值为None(应当始终为None)。
    #       target: 子进程要执行的可调用对象，默认值为 None
    #       name:   进程名称，默认为 None，如果设置为None，Python会自动分配名字。
    #       args:   给 target 传的位置参数(元组)
    #       kwargs: 给 target 传的关键字参数(字典)。
    #       daemon: 标记进程是否为守护进程，取值为布尔值(默认为 None，表示从创建方进程继承)。 |
    p1 = Process(target=speak, name='-说话-', args=(6, 8))
    p2 = Process(target=study, kwargs={'msg': 'hello'})
    p1.start()
    p2.start()
    print(p1.name)
    print(p2.name)

    print('我是主进程中的[最后一行]打印')
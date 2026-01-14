import os
import time

print(f'当前进程的pid:{os.getpid()}')
print(f'当前进程的父进程pid:{os.getppid()}')
time.sleep(1000)
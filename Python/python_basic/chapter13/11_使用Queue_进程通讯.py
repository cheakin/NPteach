import time
from multiprocessing import Process, Queue

# 子进程1:往队列里放数据
def test1(q):
    for i in range(5):
        print(f'test1入队:{i}')
        q.put(i)
        time.sleep(0.5)

# 子进程2:从队列里取数据
def test2(q):
    for i in range(5):
        print(f'test2出队:{i}')
        q.put(i)
        time.sleep(1)



if __name__ == '__main__':
    q = Queue()

    p1 = Process(target=test1, args=(q,))
    p2 = Process(target=test2, args=(q,))

    p1.start()
    p2.start()

    p1.join()
    p2.join()
# 我们之前学过 1ist、tuple、dict，它们的特点是:数据想怎么拿数据，就怎么拿。
# 队列(Queue)是:一种"先进先出"的数据结构(先放进去的数据，一定会先取出来)
import time
from multiprocessing import Process, Queue   # multiprocessing 中的 Queue, 变量跨进程
# from queue import Queue # queue 中的 Queue, 变量不跨进程

# 创建一个队列(不限制大小，即:不设置容量上限)
# q1 = Queue()

# 创建一个队列(最多能保存3个元素)
# q2 = Queue(3)

# 1. 入队
# q1.put(10)
# q1.put(20)
# q1.put(30)

# 2. 出队
# v1 = q1.get()
# v2 = q1.get()
# v3 = q1.get()
# print(v1)
# print(v2)
# print(v3)

# 3. empty方法,判断队列是否为空
# isEmpty = q1.empty()
# print(isEmpty)

# 4. full方法, 判断队列是否已满
# isFull = q2.full()
# print(isFull)

# 5. qsize方法, 获取队列长度
# size = q2.qsize()
# print(size)

# 6. 队列具有等待模式
# q2.put(100)
# q2.put(200)
# q2.put(300)

# (1).当队列已满，继续 put，就会进入等待模式，等别人调get方法，取走元素
# q2.put(400)
# print(f'放入完毕{q2}')

# (2).当队列已满，执行:put(元素，timeout=秒数)，就会等待指定秒数。
# q2.put(400, timeout=3)
# print('放入完毕')

# (3).put_nowait 方法，会直接向队列中添加元素，不会进入等待模式，若队列已满，会抛出异常。
# 备注:put_nowait 等价于 put(元素，block=False),block的默认值为 True
# q2.put_nowait(400)
# q2.put(400, block=False)

# get读多了，也会进入等待模式
# q2.get()
# q2.get()
# q2.get()

# (1).当队列已空，继续 get，就会进入等待模式。
# q2.get()

# (2).当队列已空，执行 get(timeout=秒数)，就会等待指定秒数。
# q2.get(timeout=3)

# (3).get_nowait 方法，会直接读取队列中的元素，不会进入等待模式，若队列已空，会抛出异常
# 备注:get nowait 等价于 get(block=False)
# q2.get_nowait()
# q2.get(block=False)




# 通过多进程，演示一下:当队列满了以后，再次put会等待，当有人从队列中取出元素后，put会继续
def test(q):
    time.sleep(3)
    result = q.get()
    print(f'出队:{result}')

if __name__ == '__main__':
    q = Queue(2)
    q.put(1)
    q.put(2)
    print(f'队列是否满{q.full()}')

    p1 = Process(target=test, args=(q, ))
    p1.start()
    p1.join()

    q.put(3)

    print('队列元素：')
    print(q.get())
    print(q.get())

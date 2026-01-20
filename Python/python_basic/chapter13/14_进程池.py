import os, time
from concurrent.futures import ProcessPoolExecutor, as_completed

# # 1. 创建『进程池执行器』、使用 submit 方法提交任务、使用 shutdown 方法等待任务完成。
# def work(n):
#     print(f'work n:{n}.......{os.getpid()}')
#     time.sleep(1)
#
# if __name__ == '__main__':
#     print('----------start----------')
#     # 创建一个进程池执行器
#     executor = ProcessPoolExecutor(3)
#
#     # 使用 submit 方法提交任务(submit 只负责"提交任务"，不会阻塞主进程)
#     executor.submit(work, 1)
#     executor.submit(work, 2)
#     executor.submit(work, 3)
#     executor.submit(work, 4)
#     executor.submit(work, 5)
#     executor.submit(work, 6)
#     executor.submit(work, 7)
#
#     # shutdown 的作用:不再接收新的任务.
#     # wait=False 的作用:阻塞主进程,等待进程池中所有任务执行完毕。
#     executor.shutdown(wait=True)
#     print('----------end----------')


# # 2. 获取子进程执行后的返回结果(Future类的实例对象 +result方法)
# def work(n):
#     print(f'work n:{n}.......{os.getpid()}')
#     time.sleep(1)
#     return f'任务{n}的结果'
#
# if __name__ == '__main__':
#     print('----------start----------')
#     # 创建一个进程池执行器
#     executor = ProcessPoolExecutor(3)
#
#     # 使用 submit 方法提交任务(submit 只负责"提交任务"，不会阻塞主进程)
#     # f1 = executor.submit(work, 1)
#     # f2 = executor.submit(work, 2)
#     # f3 = executor.submit(work, 3)
#     # f4 = executor.submit(work, 4)
#     # f5 = executor.submit(work, 5)
#     # f6 = executor.submit(work, 6)
#     # f7 = executor.submit(work, 7)
#     futures = [executor.submit(work, index) for index in range(1,8)]
#     # 阻塞主进程,等待进程池中所有任务执行完毕。
#     executor.shutdown(wait=True)
#     # print(f1)
#     # print(f1.result())
#     # print(f2.result())
#     # print(f3.result())
#     # print(f4.result())
#     # print(f5.result())
#     # print(f6.result())
#     # print(f7.result())
#     # 打印每个任务结果
#     for future in futures:
#         print(future.result())
#     print('----------end----------')


# # 3. 使用 as_completed:按"完成顺序"获取结果
# def work(n):
#     print(f'work n:{n}.......{os.getpid()}')
#     if n == 1:
#         time.sleep(8)
#     elif n == 2:
#         time.sleep(5)
#     else:
#         time.sleep(1)
#     return f'任务{n}的结果'
#
# if __name__ == '__main__':
#     print('----------start----------')
#     # 创建一个进程池执行器
#     executor = ProcessPoolExecutor(3)
#     # 使用 submit 方法提交任务(submit 只负责"提交任务"，不会阻塞主进程)
#     futures = [executor.submit(work, index) for index in range(1,8)]
#     # 收集每个任务结果
#     results = []
#     for f in as_completed(futures):
#         results.append(f.result())
#     print(results)
#     # 阻塞主进程,等待进程池中所有任务执行完毕。
#     executor.shutdown(wait=True)
#     print('----------end----------')


# # 4. 使用 add_done_cal1back 方法，为任务添加完成时的回调函数
# def work(n):
#     print(f'work n:{n}.......{os.getpid()}')
#     if n == 1:
#         time.sleep(8)
#     elif n == 2:
#         time.sleep(5)
#     else:
#         time.sleep(1)
#     return f'任务{n}的结果'
#
# if __name__ == '__main__':
#     print('----------start----------')
#     # 创建一个进程池执行器
#     executor = ProcessPoolExecutor(3)
#
#     results = []
#     # 回调函数
#     def done_func(future):
#         results.append(future.result())
#
#
#     for index in range(1, 8):
#         f = executor.submit(work, index)
#         f.add_done_callback(done_func)
#
#     executor.shutdown(wait=True)
#     print(results)
#     print('----------end----------')


# # 5. 使用 map 方法批量提交任务(注意:map方法是阻塞的，并且得到结果的顺序，与任务分配的顺序是一致的)
# def work(n):
#     print(f'work n:{n}.......{os.getpid()}')
#     if n == 1:
#         time.sleep(8)
#     elif n == 2:
#         time.sleep(5)
#     else:
#         time.sleep(1)
#     return f'任务{n}的结果'
#
# if __name__ == '__main__':
#     print('----------start----------')
#     # 创建一个进程池执行器
#     executor = ProcessPoolExecutor(3)
#
#     # 通过 map 方法批量提交任务(结果按照提交的顺序来)
#     results = executor.map(work, range(1, 8))
#     print(results)
#
#     # 打印结果
#     print(list(results))
#
#     executor.shutdown(wait=True)
#     print('----------end----------')


# 6. 使用 with:进程池的"自动回收"写法,离开 with 代码块时自动执行 shutdown(wait=True)
def work(n):
    print(f'work n:{n}.......{os.getpid()}')
    if n == 1:
        time.sleep(8)
    elif n == 2:
        time.sleep(5)
    else:
        time.sleep(1)
    return f'任务{n}的结果'

if __name__ == '__main__':
    print('----------start----------')
    with ProcessPoolExecutor(3) as executor:
        # 通过 map 方法批量提交任务(结果按照提交的顺序来)
        results = executor.map(work, range(1, 8))
        # 打印结果
        print(list(results))

    print('----------end----------')






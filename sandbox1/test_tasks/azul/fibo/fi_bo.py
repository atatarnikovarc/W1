__author__ = 'atatarnikov'

import time


def fib(n):
    if n < 2:
        return 1
    return fib(n-2) + fib(n-1)


def fib_u(n):
    fib1 = fib2 = 1

    i = 1
    while i < n:
        fib_sum = fib2 + fib1
        fib1 = fib2
        fib2 = fib_sum
        i += 1
    return fib_sum


if __name__ == "__main__":

    start_time = time.time()

    for i in range(5):
        print fib(30)

    print 'first time taken: ' + str(time.time() - start_time)

    start_time = time.time()

    for i in range(5):
        print fib_u(30)

    print 'second time taken: ' + str(time.time() - start_time)
__author__ = 'atatarnikov'

import time

def p1(y, *args):
    print "it is y: " + y
    for k in args:
        print k

def run():
    parms = [3, 1, 2]
    #p1("my", *parms)

    arr1 = (1, 2, 3)
    for i in range(len(parms)):
        print i

def primes(n):
    if n==2: return [2]
    elif n<2: return []
    s=range(3,n+1,2)
    mroot = n ** 0.5
    half=(n+1)/2-1
    i=0
    m=3
    while m <= mroot:
        if s[i]:
            j=(m*m-3)/2
            s[j]=0
            while j<half:
                s[j]=0
                j+=m
        i=i+1
        m=2*i+3
    return [2]+[x for x in s if x]

def simple_num(stop=10):
    for i in xrange(2, stop):

        flag = 1

        for k in xrange(2, stop):

            if k == i:
                continue

            div_res = float(i) / float(k)
            div_res_frac = str(div_res - int(div_res))[1:]

            if div_res_frac == ".0" and int(div_res) >= 1:
                flag = 0
                break

        if flag == 1:
            print i

if __name__ == "__main__":
    #run()
    s = '1'
    d = 'd1'
    #print id(s), id(d)

    start_time = time.time()
    simple_num(8)
    print("my --- %s seconds ---" % (time.time() - start_time))

    start_time = time.time()
    print primes(8)

    print("lib --- %s seconds ---" % (time.time() - start_time))
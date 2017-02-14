__author__ = 'atatarnikov'
import os, sys

def main():
    root = "/home/atatarnikov/work/home/TESTDEV/"

    for root, subs, files in os.walk(root):
        for filename in subs:
            print filename
            os.rename(os.path.join(root, filename), os.path.join(root, filename.lower()))
#
# def main1():
#     plt.ion()
#     i=0
#
#     while i <NT-1:
#         i+=1
#         plt.figure(1)
#         plt.plot(xi,C[i,:])
#         plt.pause(0.0001)


def main2(a, b):
        result = 0
        x3found = []

        for i in range(a, b + 1):
            number = float(i) / 3
            curr_div = str(number-int(number))[1:]

            if curr_div == '.0':
                x3found.extend([i])

        for e in x3found:
            result += e

        return result


if __name__ == "__main__":
    print("res: " + str(main2(3, 6)))
    print("res: " + str(main2(1, 2)))
    print("res: " + str(main2(1, 10000)))
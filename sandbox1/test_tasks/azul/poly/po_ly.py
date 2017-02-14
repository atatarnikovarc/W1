#!/usr/bin/env python

__author__ = 'atatarnikov'

import time


def is_it_polly2(s):
    """
    this method would implement several intervals search method (n intervals at left and right
        of the string) - I would assume, it will work effectively than simple search for
        strings 10^6 probably
    :param s:
    :return:
    """
    pass


def is_it_polly(s):
    # outcome
    oc = True
    # TODO yeah, we should care of all whitespaces(tabs, doubled spaces, etc.)
    s = s.replace(' ', '')
    left_i = 0
    right_i = len(s) - 1

    while True:
        if left_i >= right_i:
            break
        if s[left_i] != s[right_i]:
            oc = False
            break
        left_i += 1
        right_i -= 1

    return oc


# use it to generate data
def gen_poly(n):
    oc = ''

    # TODO yeah we would use .join()
    for i in range(0, n):
        oc += 'A'

    return oc


# test data
check_arr = [
    ('a roza upala na lapu azora', True),
    ('a', True),
    ('b b', True),
    ('bb b', True),
    ('e bbe', True),
    ('arozaazoro', False),
    ('', True),
    (gen_poly(100), True)
]

if __name__ == "__main__":

    for elm in check_arr:
        curr_elm = elm[0]
        curr_elm_len = len(curr_elm.replace(' ', ''))
        curr_expected = elm[1]

        start_time = time.time()
        assert is_it_polly(curr_elm) == curr_expected

        # yeah, it is Python 2.X depends
        print 'sample \'' + curr_elm + '\' len = ' + str(curr_elm_len) + \
              ' time taken: ' + str(time.time() - start_time)
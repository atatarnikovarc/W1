__author__ = 'atatarnikov'

# as I'm not Python expert, hence, there's possibility of better choices for used libs
# I didn't researched it well ))=^

import os
import sys
import urllib2
import shutil
from lxml import html
from urlparse import urlparse

try:
    input_url = str(sys.argv[1])
except IndexError:
    print 'No URL input parameter, please, specify one'

parsed_iu = urlparse(input_url)
iu_scheme = parsed_iu.scheme

if iu_scheme not in ('http', 'https', ''):
    print 'The utility doesn\'t support \'' + parsed_iu.scheme + '\' scheme'
    print 'Exiting...'
    sys.exit(-1)

# divide input url into parts
iu_scheme = 'http' if iu_scheme == '' else iu_scheme  # (=^
iu_base_url = parsed_iu.netloc
iu_path = parsed_iu.path

validated_input_url = iu_scheme + '://' + iu_base_url + iu_path

print 'URL to process is: ' + validated_input_url


# it is also good idea to accompanying this script with tests
# I've tested it on 'http://pythonicway.com/python-exceptions-handling' and
# 'https://habrahabr.ru/post/280238/' only ))=^
def run():
    # it's better to put the content at a disk file
    # to ensure RAM is not overload
    # plus we need make sure to not be reflected as a scrapping robot
    # we do not consider getting authenticated for in this version

    try:
        content = urllib2.urlopen(validated_input_url).read()
    except urllib2.URLError as err:
        print 'Cannot get content for input URL: ' + validated_input_url
        print err
        print 'Exiting...'
        sys.exit(-1)

    # supporting 'img' way to embedd images only
    # otherwise here should be get_images() method
    # !!we have no 'script tag contained' img support - coming up in next releases! ^=)
    img_tags = html.fromstring(content).xpath('//img')

    img_links = []
    for e in img_tags:
        # we possibly need some analysis of 'curr_url' value
        # ex.: if it's empty
        curr_url = make_url(e.get('src'))
        img_links.append(curr_url)

    # !! in a case of much of images - it is nice idea to store them
    # by pre-created folders, say, 100 per folder

    # create new txt file and put all urls into it
    # we would use config file if necessary (filename, data folder etc.)
    urls_file = open('./urls_list.txt', 'w')
    for e in img_links:
        urls_file.write(e)
        urls_file.write('\n')
    urls_file.close()

    # create img folder and download all pictures to it
    imgs_fold = './imgs'

    try:
        # need to add cannot-delete-folder error processing
        shutil.rmtree(imgs_fold)
    except OSError as err:
        pass

    if not os.path.exists(imgs_fold):
        os.makedirs(imgs_fold)

    # !! To have this script production ready it's good idea to
    # add multi-thread support - for an instance - to divide urls into parts and
    # pass each one to dedicated thread - should help =)

    # as we have no requirement to get file's original names
    # (and therefore avoid names collisions) we gonna use simple counter
    print '\n'
    print 'Starting images processing...'
    print '\n'

    f_count = 0
    for e in img_links:
        print 'Current URL: ' + e
        # we don't detect image's format for the moment, so treating each as a 'png'
        # !! we also do not work around cases to be refused as robots(set up user-agent and so on)

        try:
            curr_pic = urllib2.urlopen(e).read()
        except urllib2.URLError as err:
            print 'Cannot get content for URL: ' + e
            print err
            pass
        curr_img_file = open(os.path.join(imgs_fold, str(f_count) + '.png'), 'w')
        # no validating for content read - zero, wrong format and so on...
        curr_img_file.write(curr_pic)
        curr_img_file.close()
        f_count += 1

    print '\n'
    print 'Total images processed: ' + str(len(img_links))


def make_url(u):
    parts = urlparse(u)

    t_scheme = ''
    t_base = ''
    t_path = ''

    t_scheme = iu_scheme if parts.scheme == '' else parts.scheme
    t_base = parts.netloc if parts.netloc != '' else iu_base_url
    t_path = parts.path

    return t_scheme + '://' + t_base + t_path


# run it all!!
run()
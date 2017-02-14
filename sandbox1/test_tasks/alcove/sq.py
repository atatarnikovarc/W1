__author__ = 'atatarnikov'

from PIL import Image

orig_name = "./challenge.x.png"

im = Image.open(orig_name)
pix = im.load()
iks = im.size[0]
ygrek = im.size[1]

colors_list = []

for i in range(0, iks):
    for j in range(0, ygrek):
        if pix[i, j] != (0, 0, 0):
            colors_list.append([pix[i, j]])

f1 = open('asc', 'w')
for e in reversed(colors_list):
    f1.write(chr(e[0][1]))

# Congratulations, were deciphable
# Follow the circle to succeed this?
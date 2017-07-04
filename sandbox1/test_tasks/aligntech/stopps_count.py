__author__ = 'atatarnikov'

from geopy.distance import vincenty
import time
from decimal import Decimal
import xlrd

data_path = './data/'

sub_wb = xlrd.open_workbook(data_path + 'subway.xlsx', on_demand=True)
surf_wb = xlrd.open_workbook(data_path + 'surface.xlsx', on_demand=True)

sub_ws = sub_wb.sheet_by_index(0)
surf_ws = surf_wb.sheet_by_index(0)

# to speed the script up I'd consider the following
# 1. improve the algorithm (somehow) or better surface coords narrowing
# 2. add multi-thread processing
# 3. move it to compute cloud =]

# the algorithm considers each subway station exit as the station,
# otherwise, additional post-processing required

# subway_ws['B2'].value  name
# subway_ws['C2'].value  dolgota
# subway_ws['D2'].value  shirota

# surface_ws['B2'].value  name
# surface_ws['C2'].value  dolgota
# surface_ws['D2'].value  shirota


def distance(d1, s1, d2, s2):
    return vincenty((d1, s1), (d2, s2)).kilometers

start = time.time()

# form a list of surface coordinates

surf_coord = []
surf_idx1 = 1

surf_time1 = time.time()

while True:
    try:
        val_added = (Decimal(surf_ws.cell_value(surf_idx1, 2)),
                     Decimal(surf_ws.cell_value(surf_idx1, 3)))
    except IndexError:
        break

    surf_coord.append(val_added)
    surf_idx1 += 1


surf_time2 = time.time()

print 'Forming surf coords list taken: ' + str(surf_time2 - surf_time1)

# kind of empiric value rather than calculated
coords_delta = Decimal(0.01)
freq_subway = []
subway_idx = 1

# the loop over all subway stations
while True:
    curr_station_stop_count = 0

    print 'Current subway idx: ' + str(subway_idx)

    try:
        subway_curr_coord_pair = (Decimal(sub_ws.cell_value(subway_idx, 2)),
                                  Decimal(sub_ws.cell_value(subway_idx, 3)))
    except IndexError:
        break

    narrow_surf_coords = [x for x in surf_coord
                          if abs(x[0] - subway_curr_coord_pair[0]) < coords_delta
                          or abs(x[1] - subway_curr_coord_pair[1]) < coords_delta]

    for x in narrow_surf_coords:
        if distance(subway_curr_coord_pair[0], subway_curr_coord_pair[1],
                    x[0], x[1]) <= 0.5:
            curr_station_stop_count += 1

    freq_subway.append((curr_station_stop_count, sub_ws.cell_value(subway_idx, 1)))
    subway_idx += 1

try:
    # was not able to find pytonic way to make such output )=^
    sorted_freq_subway = sorted(freq_subway, reverse=True)

    max_val = sorted_freq_subway[0][0]  # get max value

    # try to print equal-to-max elements
    for elm in sorted_freq_subway:
        if elm[0] != max_val:
            break

        print str(elm[0]) + ' ' + elm[1]

except IndexError:
    print 'Freq array is empty'

end = time.time()
print 'Time taken: ' + str(end - start)
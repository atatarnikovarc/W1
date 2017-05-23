If you want to add new ip into ip.txt

Example:
200.24.24.2;7;0;0;0;0;Universidad De Antioquia;1

line consists from 8 values,which separated by ';'
Values are:
1)ip
2)industry_code
3)business_size
4)value_min
5)value_max
6)zip
7)business_name
8)isISP

isISP = 0 or 1
0 - that means that ip mustn't be added into our system
1 - that means that ip must be added into our system

auto-tests checked at DB equals by:
- industry_code
- business_size
- value_min
- value_max
- zip
- business_name

auto-tests checked at DB equals by(see /data/etl/userBusinessDataCall.xml):
- industry_code
- business_name
- ip
- created_date(uses regExp)

not working  26.03.12
11.4.4.50;14;2768886;5000000;0;47656566;US Department of Defense;1
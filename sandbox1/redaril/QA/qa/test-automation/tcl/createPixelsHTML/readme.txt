��� �������� �������� � ���� ��� ������ ppt �� .155 ������
� ������� ����� ������� Eclipce ��������� ����� "�������"
100001.txt 100002.txt 100003.txt � �.�.
��� ����� ��� pixelID ������� ��� ������
���������� ����� (������ ��� �������� ��������� � ����� �����),
��� ����� ���������� ������� ���� ������� ��� �������� �������.

� ������� ��������� ������� �����
proc init
#������� ����� ������� �������, ��� ����� ����� "�������"
set templateFolder "D:\\WorkSpace\\eclipse_workspace\\makePixel155\\pixels\\Templates\\"
#������� ����� apache ���� ����� �������� ��������� .html ����� ��� �� ���������� ����� �������
set htmlFolder "C:\\Program Files\\Apache Software Foundation\\Apache2.2\\htdocs\\htmlTemplates\\"

������ ���������� ��� ����� � templateFolder
�� �� ���������� ������� .html ����� � htmlFolder


- 100001.txt --------------------------------------------------
#TEMPLATE
#DATA PIXEL
id= 100001
name= testDataPixelID_100001
ruleSelection= Piggyback
dataPlatform= 3
isActive= Y
isHttps= Y
dataPixelFormat= S
dataPixelIsDataEnabler= N
dataPixelIsDataConsumer= N
dataPixelServerURL= http://tsvetaev.narod.ru
maxPiggybacks= 5

- 100016.txt --------------------------------------------------
#TEMPLATE
#CAMPAIGN PIXEL
id= 100016
name= testCampaignPixelID_100016
ruleSelection= Retargeting
campaignPixelType= T
dataPlatform= 3
pixelFormat= S
isActive= Y
isHttps= Y
generateNdlNdr= N
maxPiggybacks= 5

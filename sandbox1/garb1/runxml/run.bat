@echo off

rem xml ���� ��� ����� ������� - �� ��� ������ ��������� �������� xml ��� ������
set xmlFilename=.\\data\\1253.xml
rem ��� ��������� xml ����� 
set outputXmlFileName=.\\output\\1155.xml
rem ��� ����� �������, �� ������ �������� ���������� ����� ��� ������ ������� ������
set melodyName=.\\data\\0000.wav
rem ��� ����� ������� ���������
set bestPartName=.\\data\\3321b.wav
rem ��� ����� ������� ���������
set bestPartFile=3321b.wav
rem �����, ���� ���������� ����� �������
set copyPath=.\\output\\1\\

rem ��������������, ��� � �������� ����� �������� ��� ���������� ���������
rem ������, � �������� ����� ���������� ��������� ������� ������(����� ���� ����� 1 � ����)
set startIndex=280
rem ����� ������� � ������
set total=30

call .\bin\run.bat %total% %startIndex% %copyPath% %bestPartFile% %bestPartName% %melodyName% %outputXmlFileName% %xmlFileName%
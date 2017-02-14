@echo off

rem xml файл для одной мелодии - на его основе генерится выходной xml для архива
set xmlFilename=.\\data\\1253.xml
rem имя выходного xml файла 
set outputXmlFileName=.\\output\\1155.xml
rem имя файла мелодии, на основе которого копируются файлы для других мелодий архива
set melodyName=.\\data\\0000.wav
rem имя файла лучшего фрагмента
set bestPartName=.\\data\\3321b.wav
rem имя файла лучшего фрагмента
set bestPartFile=3321b.wav
rem папка, куда копируются файлы мелодий
set copyPath=.\\output\\1\\

rem предполагается, что в основном будут меняться два нижеидущих параметра
rem индекс, с которого будет начинаться нумерация мелодий архива(может быть равно 1 и выше)
set startIndex=280
rem всего мелодий в архиве
set total=30

call .\bin\run.bat %total% %startIndex% %copyPath% %bestPartFile% %bestPartName% %melodyName% %outputXmlFileName% %xmlFileName%
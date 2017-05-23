при создании пикселов в базе при помощи ppt на .155 машине
в рабочей папке проекта Eclipce создаются файлы "шаблоны"
100001.txt 100002.txt 100003.txt и т.п.
имя файла это pixelID который был создан
содержимое файла (пример два варианта приведены в конце файла),
это набор параметров которые были указаны при создании пикселя.

в скрипте указываем рабочие папки
proc init
#рабочая папка проекта эклипсе, где лежат файлы "шаблоны"
set templateFolder "D:\\WorkSpace\\eclipse_workspace\\makePixel155\\pixels\\Templates\\"
#рабочая папка apache куда будут выложены созданные .html файлы для их исполнения через браузер
set htmlFolder "C:\\Program Files\\Apache Software Foundation\\Apache2.2\\htdocs\\htmlTemplates\\"

скрипт перебирает все файлы в templateFolder
по их содержанию создает .html файлы в htmlFolder


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

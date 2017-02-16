1. install django

2.create django project: django-admin startproject mysite

3. start django dev server: python manage.py runserver
create app: python manage.py startapp polls

django application has views

add simplest view
add app urlconf
add ref to 'polls' app urlconf at root urlconf

setting up database - sqlite as default
set timezone if needed
python manage.py migrate (apps -> database)

define data model
add 2 classes(models) - Question and Choice, define their fields
add poll app into settings.py apps list
python manage.py makemigrations polls (activate models)
django has 'migration' term - is a changes to database, stored at a file at disk
python manage.py sqlmigrate polls 0001 - 'sqlmigrate' command to manage migrations
python manage.py check - checks the project for any issues
developer able to change database over time, not loosing any data - it is due to
migrations

3 steps making model changes:

changes models(models.py)
python manage.py makemigrations - to create migrations
python manage.py migrate - to apply migrations to database

we can work with database via django api

python manage.py shell
django has good api to work with database via models

?timezone to gmt+3
?apply change in models

python manage.py createsuperuser

add Question model to admin.py to have it adminable at admin site

===overviews

view - page - function - template

a view can use template to change page's design not in python
views, mapping urls, render etc....

when design multiuser system - think of race condition
when designing django app - one should decide - do we use generic views system or not

==tests
1. tests will save you time
2. tests don't just identify problem, they prevent them
3. tests make your code more attractive
4. tests help teams work together

before starting django based(and not only) development,
one should care of how test automation
will work(test coverage, unit testing, in browser testing)



пожелания\требования

1. сопровождать свой код тестами
-юнит - через темповую базу
-фантом

2. писать интерфейс сразу с поддержкой для всех устройств(адаптивный лэйаут?)




-===требования\задачи к моим двум сайтам
1. сделать джанго приложение с одним view, чтобы оно отображало то, что есть сейчас
-фаст, бантик
2. сделать деплой приложений на хостинге
3. попробовать прикрутить к интерфейсу фреймворки адаптивной верстки - чтобы эти сайты сразу
работали и на мобильных устройствах
4. fastsqa (русская и английская версии)
должен уметь принимать заявки от клиентов(клиент должен мочь что то делать на сайте!)
5. бантик - клиент должен иметь возможнось записаться онлайн
(свободное время - реализация календаря)
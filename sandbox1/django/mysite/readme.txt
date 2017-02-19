=================
1. install django

2. create django project: "django-admin startproject mysite"

3. start django dev server: "python manage.py runserver"

4. create app: "python manage.py startapp polls"

5. django application has views

A view in it's starter look - just has to respond with something like
HttpReponse()

(it is for predefined views)
-add view class
-add view template
-add to 'urls.py' ref to a view(+name to share among templates)

6. db, timezone
-use "settings.py"
-make sure to create database if it's not the sqlite

7. add "poll" app into "settings.py" apps list

8. defining data model

-add classes, defining their fields
-"python manage.py makemigrations polls" (activate models)
-django has 'migration' term - is a changes to database, stored at a file at disk
-"python manage.py sqlmigrate polls 0001" - 'sqlmigrate' shows SQL code produced
by the migration

9. 3 steps making model changes:

-changes models(models.py)
-"python manage.py makemigrations" - to create migrations
-"python manage.py migrate" - to apply migrations to database

10. python manage.py check - checks the project for any issues

11. we can work with database via django api

-"python manage.py shell"
-from polls.models import Question, Choice
-Question.objects.all()

12. "python manage.py createsuperuser"

13. add Question model to admin.py to have it adminable at admin site

==tests
1. tests will save you time
2. tests don't just identify problem, they prevent them
3. tests make your code more attractive
4. tests help teams work together

before starting django based(and not only) development,
one should care of how test automation
will work(test coverage, unit testing, in browser testing)



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
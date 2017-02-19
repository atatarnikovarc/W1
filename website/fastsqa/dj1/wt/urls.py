from django.conf.urls import url

from . import views

app_name = 'wt'
urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^ru$', views.index_ru, name='index_ru'),
]